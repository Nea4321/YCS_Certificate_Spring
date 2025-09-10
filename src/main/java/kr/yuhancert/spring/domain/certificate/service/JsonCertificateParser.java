package kr.yuhancert.spring.domain.certificate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import kr.yuhancert.spring.domain.certificate.entity.CertData;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.certificate.repository.CertDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

//2번째 json 저장할 위치와 어떤 식으로 구분해서 저장할지와 데이터베이스 안에 넣을것까지의 과정
@Service
@RequiredArgsConstructor
public class JsonCertificateParser {

    private final CertDataRepository certDataRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @PersistenceContext
    private EntityManager entityManager; // ← 추가


    /** JSON 존재/형식만 검증 (DB 저장 안 함) */
    public void parseJsonOnly(String jsonPath) throws IOException {
        File jsonFile = new File(jsonPath);
        if (!jsonFile.exists()) {
            throw new FileNotFoundException("❌ JSON 파일이 존재하지 않습니다: " + jsonPath);
        }
        // 최소 검증
        mapper.readTree(jsonFile);
        System.out.println("✅ JSON 파싱/검증 완료(저장 생략): " + jsonPath);
    }

    /** certId 1행에 '전체 JSON'을 통째로 저장 (섹션별 다중 insert 없음) */
    @Transactional
    public void parseAndSave(String jsonPath, Long certId) throws IOException {
        if (certId == null) {
            throw new IllegalArgumentException("certId가 null 입니다. DB 저장 불가");
        }

        File jsonFile = new File(jsonPath);
        if (!jsonFile.exists()) {
            throw new FileNotFoundException("❌ JSON 파일이 존재하지 않습니다: " + jsonPath);
        }

        JsonNode root = mapper.readTree(jsonFile);

        // 기존 행 삭제 (PK=certId 기준 1행)
        if(certDataRepository.existsById(certId)) {
            certDataRepository.deleteById(certId);
        }

        // ★ 핵심: 한 행에 전체 JSON을 그대로 저장
        CertData row = new CertData();
        row.setId(certId);                 // ← 현재 스키마 유지 (PK=certId)
        row.setCertificate(entityManager.getReference(Certificate.class, certId)); // ← 중요
        row.setInfogb("FULL");             // 구분용 태그(원하면 "JSON" 등으로)
        row.setContents(root.toString());  // 전체 JSON 문자열
        certDataRepository.save(row);

        System.out.println("✅ DB 저장 완료 (단일 행 FULL JSON). certId=" + certId);
    }
}
