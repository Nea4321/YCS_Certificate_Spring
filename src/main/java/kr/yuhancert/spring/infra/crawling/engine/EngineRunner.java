package kr.yuhancert.spring.infra.crawling.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
@RequiredArgsConstructor
public class EngineRunner {

    /** C안(venv) 고정: application.properties에서 경로 지정. 비워두면 B안으로 폴백 */
    @Value("${python.path:}")
    private String pythonPath;

    /** 공통 실행 인자:  application.properties에 있지만 안전하게 쓰기 위해 씀*/
    @Value("${python.args:-X utf8}")
    private String pythonArgs;

    public void run(String scriptPath, String certName, String jsonPath)
            throws IOException, InterruptedException {
        if (jsonPath == null || jsonPath.isBlank()) {
            throw new IllegalArgumentException("jsonPath 가 비어있습니다.");
        }
        Files.createDirectories(Path.of(jsonPath).getParent());

        List<String> cmd = new ArrayList<>(resolvePythonCmd());
        if (pythonArgs != null && !pythonArgs.isBlank()) {
            cmd.addAll(Arrays.asList(pythonArgs.trim().split("\\s+")));
        }

        cmd.add(scriptPath);
        cmd.add("--cert"); cmd.add(certName);
        cmd.add("--out");  cmd.add(jsonPath);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        pb.environment().putIfAbsent("PYTHONIOENCODING", "utf-8");

        // run_once.py가 루트에 있으므로, CWD를 루트로 고정(상대경로 안전)
        pb.directory(Path.of(scriptPath).getParent().toFile());

        System.out.println("[EngineRunner] Run: " + String.join(" ", cmd));
        Process process = pb.start();

        StringBuilder outBuf = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                outBuf.append(line).append(System.lineSeparator());
                System.out.println("[Python] " + line);
            }
        }

        int exit = process.waitFor();
        System.out.println("📦 종료 코드: " + exit);
        if (exit != 0) {
            throw new IllegalStateException("Python process exited with code " + exit
                    + "\n--- python output ---\n" + outBuf);
        }
    }


    /** python.path가 비어있으면 OS별로 리트라이 (B안) */
    private List<String> resolvePythonCmd() {
        if (pythonPath != null && !pythonPath.isBlank()) {
            return List.of(pythonPath);
        }
        String os = System.getProperty("os.name", "").toLowerCase();

        List<List<String>> candidates = new ArrayList<>();
        //win은 window이다
        if (os.contains("win")) {
            candidates.add(List.of("py", "-3.11"));
            candidates.add(List.of("py", "-3"));
            candidates.add(List.of("python"));
        } else {
            candidates.add(List.of("python3"));
            candidates.add(List.of("python"));
        }

        for (List<String> c : candidates) {
            if (canStart(c)) {
                System.out.println("[EngineRunner] using interpreter: " + String.join(" ", c));
                return c;
            }
        }
        throw new IllegalStateException("Python 실행기를 찾을 수 없습니다. application.properties의 python.path를 설정하세요.");
    }

    /** 후보 실행기가 실제로 시작되는지 간단 확인 */
    private boolean canStart(List<String> base) {
        try {
            Process p = new ProcessBuilder(base).redirectErrorStream(true).start();
            p.getOutputStream().close();
            // 일부 인터프리터는 즉시 0으로 종료, 일부는 대기 -> 둘 다 허용
            p.waitFor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    // 레거시 시그니처는 막아두기(그대로 유지해도 됨)
    public void run(String scriptPath, String jsonPath) {
        throw new IllegalArgumentException("run(scriptPath, jsonPath) 대신 run(scriptPath, certName, jsonPath)를 사용하세요.");
    }
}