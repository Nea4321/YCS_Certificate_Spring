package kr.yuhancert.spring.domain.certificate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.yuhancert.spring.domain.certificate.entity.CertData;
import kr.yuhancert.spring.domain.certificate.repository.CertDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

//2번째 json 저장할 위치와 어떤 식으로 구분해서 저장할지와 데이터베이스 안에 넣을것까지의 과정
@Service
public class JsonCertificateParser {

    @Autowired
    private CertDataRepository certDataRepository;

    private final String jsonRelativePath = "json/linux_master_full.json";
    private final String jsonAbsolutePath = "src/main/resources/" + jsonRelativePath;

    public void parseAndSave() throws IOException {
        File jsonFile = new File(jsonAbsolutePath);
        if (!jsonFile.exists()) {
            throw new FileNotFoundException("JSON 파일이 존재하지 않습니다: " + jsonFile.getAbsolutePath());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonFile);

        Long certificateId = 614L;//일단 임의로 씀 certificate_id가 614일때부터 증가하는걸 기준으로 잡은거니 일단 둠

        saveCertData("종목소개", root.get("종목소개").toString(), certificateId);
        saveCertData("시험내용", root.get("시험내용").toString(), certificateId);
        saveCertData("시험일정", root.get("시험일정").toString(), certificateId);
        saveCertData("자격활용사례", root.get("자격활용사례").toString(), certificateId);
        saveCertData("교육협력기관", root.get("교육협력기관").toString(), certificateId);
        saveCertData("응시지역 및 수수료", root.get("응시지역 및 수수료").toString(), certificateId);
    }

    private void saveCertData(String infoGb, String content, Long certificateId) {
        CertData certData = new CertData();
        certData.setInfogb(infoGb);
        certData.setContents(content);
        certData.setId(certificateId);
        certDataRepository.save(certData);
    }
}
