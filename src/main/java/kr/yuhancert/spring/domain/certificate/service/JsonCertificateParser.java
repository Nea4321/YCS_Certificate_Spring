package kr.yuhancert.spring.domain.certificate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import kr.yuhancert.spring.domain.certificate.entity.CertData;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.certificate.entity.NationalCert;
import kr.yuhancert.spring.domain.certificate.repository.CertDataRepository;
import kr.yuhancert.spring.domain.certificate.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@RequiredArgsConstructor
public class JsonCertificateParser {

    private final CertDataRepository certDataRepository;
    private final CertificateRepository certificateRepository;
    private final ObjectMapper objectMapper; // 스프링 빈 주입

    private Path resolveJsonFilePath(String jsonPath) throws IOException {
        Path base = Path.of(jsonPath);

        if (Files.notExists(base)) {
            throw new FileNotFoundException("JSON 경로 없음: " + jsonPath);
        }

        // 1) 파일이면 그대로 반환
        if (!Files.isDirectory(base)) {
            return base;
        }

        // 2) 디렉터리이면: 우선 "이름이 숫자만이 아닌 *.norm.json" 을 찾는다 (ex. linux_master.norm.json)
        try (var stream = Files.list(base)) {
            var main = stream
                    .filter(child -> child.getFileName().toString().endsWith(".norm.json"))
                    .filter(child -> !child.getFileName().toString().matches("\\d+\\.norm\\.json"))
                    .findFirst();

            if (main.isPresent()) {
                return main.get();
            }
        }

        // 3) 그래도 못 찾으면, 그냥 아무 *.norm.json 이나 하나 사용 (fallback)
        try (var stream = Files.list(base)) {
            return stream
                    .filter(child -> child.getFileName().toString().endsWith(".norm.json"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "폴더 내 .norm.json 파일을 찾지 못했습니다: " + base
                    ));
        }
    }


    public void parseJsonOnly(String jsonPath) throws IOException {
        Path p = resolveJsonFilePath(jsonPath);   // ← 새 헬퍼 사용

        byte[] bytes = Files.readAllBytes(p);
        objectMapper.readTree(bytes);
        System.out.println("✅ JSON 파싱/검증 완료(저장 생략): " + p);
    }


    /**
     * JSON 파일을 읽어 cert_data( PK = certId )에 업서트.
     * - _meta.name / _meta.jmcd / _meta.cert_id 주입/보강
     * - 기존 행이 있으면 update, 없으면 insert
     */

    private Map<String,Object> asMap(JsonNode n) {
        return (n == null || n.isNull() || n.isMissingNode())
                ? null
                : objectMapper.convertValue(n, new TypeReference<Map<String,Object>>(){});
    }
    private List<Map<String,Object>> asListOfMap(JsonNode n) {
        return (n == null || n.isNull() || n.isMissingNode())
                ? null
                : objectMapper.convertValue(n, new TypeReference<List<Map<String,Object>>>(){});
    }

    private boolean has(JsonNode n) {
        return n != null && !n.isNull() && !n.isMissingNode();
    }

    @Transactional(timeout = 120) // 2분 정도 여유
    public void parseAndSave(String jsonPath, Long certId) throws IOException {
        if (certId == null) throw new IllegalArgumentException("certId가 null 입니다.");

        // ★ 파일/폴더 모두 처리
        Path p = resolveJsonFilePath(jsonPath);

        // 1) JSON 로드
        byte[] bytes = Files.readAllBytes(p);
        JsonNode root = objectMapper.readTree(bytes);

        final ObjectNode obj = (root instanceof ObjectNode)
                ? (ObjectNode) root
                : objectMapper.createObjectNode().set("data", root);

        // 2) 인증서(외래키/공유PK 주인)
        Certificate cert = certificateRepository.findById(certId)
                .orElseThrow(() -> new IllegalArgumentException("no certificate: " + certId));

        // 3) _meta 보강
        ObjectNode meta = obj.with("_meta");
        meta.put("name", Optional.ofNullable(cert.getCertificateName()).orElse(""));
        String jmcd = Optional.ofNullable(cert.getJmcd()).orElse("");
        if (!jmcd.isBlank()) meta.put("jmcd", jmcd);
        meta.put("cert_id", certId);

        // 4) 업서트
        CertData entity = certDataRepository.findByIdForUpdate(certId)
                .orElseGet(() -> {
                    CertData x = new CertData();
                    x.setCertificate(cert);
                    return x;
                });

        entity.setCertificateName(cert.getCertificateName());
        entity.setOrganization(cert.getOrganization());
        entity.setBasicInfo(asMap(obj.get("기본정보")));
        entity.setSchedule(asListOfMap(obj.get("시험일정")));

        var otherObj = objectMapper.createObjectNode();
        if (has(obj.get("우대현황")))           otherObj.set("우대현황", obj.get("우대현황"));
        if (has(obj.get("링크")))               otherObj.set("링크", obj.get("링크"));
        if (has(obj.get("종목별검정현황")))     otherObj.set("종목별검정현황", obj.get("종목별검정현황"));
        if (has(obj.get("시험정보")))           otherObj.set("시험정보", obj.get("시험정보"));
        // ★ 민간용 추가
        if (has(obj.get("시험시간")))           otherObj.set("시험시간", obj.get("시험시간"));
        if (has(obj.get("시험내용")))           otherObj.set("시험내용", obj.get("시험내용"));
        if (has(obj.get("기타정보")))           otherObj.set("기타정보", obj.get("기타정보"));

        entity.setOtherInfo(asMap(otherObj));
        certDataRepository.save(entity);
    }

}
