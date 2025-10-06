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
import java.util.Optional;

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
    @Transactional
    public void parseAndSave(String jsonPath, Long certId) throws IOException {
        if (certId == null) throw new IllegalArgumentException("certId가 null 입니다.");

        Path p = Path.of(jsonPath);
        if (Files.notExists(p)) throw new FileNotFoundException("JSON 파일 없음: " + jsonPath);

        // 1) JSON 읽기 (파일 핸들 잠김 회피)
        byte[] bytes = Files.readAllBytes(p);
        JsonNode root = objectMapper.readTree(bytes);

        // 1-1) 최상위가 객체가 아닐 수도 있으니 ObjectNode로 확보
        final ObjectNode obj;
        if (root instanceof ObjectNode) {
            obj = (ObjectNode) root;
        } else {
            obj = objectMapper.createObjectNode();
            obj.set("data", root);
        }

        // 2) 인증서 정보
        Certificate cert = certificateRepository.findById(certId)
                .orElseThrow(() -> new IllegalArgumentException("no certificate: " + certId));

        String certName = Optional.ofNullable(cert.getCertificateName()).orElse("");
        String jmcd = Optional.ofNullable(cert.getJmcd()).orElse("");

        // 3) _meta 보강/주입
        ObjectNode meta = obj.with("_meta");  // 없으면 생성
        meta.put("name", certName);
        if (!jmcd.isBlank()) meta.put("jmcd", jmcd);
        meta.put("cert_id", certId);

        // 4) 업서트
        CertData entity = certDataRepository.findById(certId).orElse(null);
        if (entity == null) {
            entity = new CertData();
            entity.setId(certId); // 공유 PK 구조
            entity.setCertificate(em.getReference(Certificate.class, certId));
        }
//        entity.setInfogb("공공 자격증");
//        entity.setContents(obj.toString());

        certDataRepository.save(entity);
    }

}
