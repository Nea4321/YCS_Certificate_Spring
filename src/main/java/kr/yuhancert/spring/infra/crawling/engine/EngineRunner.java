package kr.yuhancert.spring.infra.crawling.engine;

import org.springframework.stereotype.Component;

import java.io.*;

//3번째 파이썬 실행하기 위한 코드 파이썬 exe 위치와 내 파이썬의 위치를 기반으로 실행함
@Component
public class EngineRunner {

    private final String pythonExe = "C:/Users/이상우/AppData/Local/Programs/Python/Python311/python.exe";
    private final String scriptPath = "D:/Crawling/Certificate/YCS_Certificate_Engine/linux_master/main.py";

    public void runLinuxMasterScript() {
        try {
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
}
