package kr.yuhancert.spring.domain.certificate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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

    @PersistenceContext
    private final EntityManager em;

    /** JSON 존재/형식만 검증 (DB 저장 안 함) */
    public void parseJsonOnly(String jsonPath) throws IOException {
        Path p = Path.of(jsonPath);
        if (Files.notExists(p)) {
            throw new FileNotFoundException("❌ JSON 파일이 존재하지 않습니다: " + jsonPath);
        }
        // 파일 핸들 잠김 회피: 바이트로 읽어 파싱
        byte[] bytes = Files.readAllBytes(p);
        objectMapper.readTree(bytes);
        System.out.println("✅ JSON 파싱/검증 완료(저장 생략): " + jsonPath);
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

    @Transactional
    public void parseAndSave(String jsonPath, Long certId) throws IOException {
        if (certId == null) throw new IllegalArgumentException("certId가 null 입니다.");

        Path p = Path.of(jsonPath);
        if (Files.notExists(p)) throw new FileNotFoundException("JSON 파일 없음: " + jsonPath);

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

        // 4) 업서트 (비관적 락으로 동시 저장 방지 권장)
        CertData entity = certDataRepository.findByIdForUpdate(certId) // @Lock(PESSIMISTIC_WRITE)
                .orElseGet(() -> {
                    CertData x = new CertData();
                    // 공유 PK(@MapsId)라면 setId() 대신 setCertificate()만!
                    x.setCertificate(cert);
                    return x;
                });

        entity.setCertificateName(cert.getCertificateName());
        entity.setOrganization(cert.getOrganization());
        entity.setBasicInfo(asMap(obj.get("기본정보")));
        entity.setSchedule(asListOfMap(obj.get("시험일정"))); // 배열 형태 유지

        // ---- other_info 조립 ----
        var otherObj = objectMapper.createObjectNode();
        if (has(obj.get("우대현황")))           otherObj.set("우대현황", obj.get("우대현황"));
        if (has(obj.get("링크")))               otherObj.set("링크", obj.get("링크"));
        if (has(obj.get("종목별검정현황")))     otherObj.set("종목별검정현황", obj.get("종목별검정현황"));
        if (has(obj.get("시험정보")))           otherObj.set("시험정보", obj.get("시험정보"));   // ⬅️ 추가
        if (has(obj.get("기타정보")))           otherObj.set("기타정보", obj.get("기타정보")); // 호환용

        entity.setOtherInfo(asMap(otherObj));   // ← Map<String,Object>로 변환되어 저장

        certDataRepository.save(entity);
    }
}
