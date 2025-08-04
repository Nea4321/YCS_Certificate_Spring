/*
package kr.yuhancert.spring.domain.certificate.loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.yuhancert.spring.domain.certificate.entity.CertData;
import kr.yuhancert.spring.domain.certificate.repository.CertDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class CertDataLoader {

    @Autowired
    private CertDataRepository certDataRepository;

    private final String jsonRelativePath = "json/linux_master_full.json";
    private final String jsonAbsolutePath = "src/main/resources/" + jsonRelativePath;

    public void runPythonScript() {
        try {
            // ✅ python.exe 경로 명시 + main.py 경로 명시
            String pythonExe = "C:/Users/이상우/AppData/Local/Programs/Python/Python311/python.exe";
            String scriptPath = "D:/Crawling/Certificate/YCS_Certificate_Engine/linux_master/main.py";

            ProcessBuilder pb = new ProcessBuilder(pythonExe, scriptPath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Python] " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("📦 Python 종료 코드: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void saveCertDataFromJson() throws IOException {
        File jsonFile = new File(jsonAbsolutePath);
        if (!jsonFile.exists()) {
            throw new FileNotFoundException("JSON 파일이 존재하지 않습니다: " + jsonFile.getAbsolutePath());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonFile);

        Long certificateId = 614L;

        saveCertData("종목소개", root.get("종목소개").toString(), certificateId);
        saveCertData("시험내용", root.get("시험내용").toString(), certificateId);
        saveCertData("시험일정", root.get("시험일정").toString(), certificateId);
        saveCertData("자격활용사례", root.get("자격활용사례").toString(), certificateId);
        saveCertData("교육협력기관", root.get("교육협력기관").toString(), certificateId);
        saveCertData("응시지역 및 수수료", root.get("응시지역 및 수수료").toString(), certificateId);
    }

    public void runPythonAndSaveCertData() {
        runPythonScript();

        // ✅ 최대 5초간 JSON 생성 대기
        File jsonFile = new File(jsonAbsolutePath);
        int waitTime = 0;
        while (!jsonFile.exists() && waitTime < 5000) {
            try {
                Thread.sleep(500);  // 0.5초 대기
                waitTime += 500;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!jsonFile.exists()) {
            System.err.println("❌ JSON 파일 생성 실패. 경로: " + jsonFile.getAbsolutePath());
            return;
        }

        try {
            saveCertDataFromJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCertData(String infoGb, String content, Long certificateId) {
        CertData certData = new CertData();
        certData.setInfogb(infoGb);
        certData.setContents(content);
        certData.setId(certificateId);
        certDataRepository.save(certData);
    }
}
*/
