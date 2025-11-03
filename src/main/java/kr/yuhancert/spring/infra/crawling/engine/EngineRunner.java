package kr.yuhancert.spring.infra.crawling.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
@RequiredArgsConstructor
public class EngineRunner {

    @Value("${python.path:}")              private String pythonPath;
    @Value("${python.args:-X utf8}")       private String pythonArgs;

    @Value("${engine.script:../Engine/run_once.py}")
    private String engineScript;

    @Value("${engine.config:}")
    private String engineConfig; // 없으면 미전달

    /** 기본(run_once.py) 실행 */
    public void run(String certName, String jsonPath)
            throws IOException, InterruptedException {
        run(engineScript, engineConfig, certName, jsonPath, Collections.emptyList());
    }

    /** 스크립트/컨피그 지정 실행 (run_once.py, run_public.py 공용) */
    public void run(String scriptPath,
                    String configPath,
                    String certName,
                    String jsonPath)
            throws IOException, InterruptedException {
        run(scriptPath, configPath, certName, jsonPath, Collections.emptyList());
    }

    /** 스크립트/컨피그 + 추가 인자 실행 (public: --jmcd, --root 등) */
    public void run(String scriptPath,
                    String configPath,
                    String certName,
                    String jsonPath,
                    List<String> extraArgs)
            throws IOException, InterruptedException {

        if (jsonPath == null || jsonPath.isBlank())
            throw new IllegalArgumentException("jsonPath 가 비어있습니다.");

        Files.createDirectories(Path.of(jsonPath));

        // 1) 스크립트 절대경로 계산
        Path scriptAbs = Path.of(scriptPath);
        if (!scriptAbs.isAbsolute()) {
            scriptAbs = Path.of(System.getProperty("user.dir"))
                    .resolve(scriptAbs).normalize();
        }
        Path scriptDir  = scriptAbs.getParent();        // 예: .../Engine/public_cert_api
        Path engineRoot = scriptDir.getParent();        // 예: .../Engine
        String scriptFn = scriptAbs.getFileName().toString();

        // 2) 패키지 내부 스크립트 여부 (public_cert_api/run_public.py 등)
        boolean isPackageScript = scriptDir.resolve("__init__.py").toFile().exists();

        // 3) 작업 디렉터리 결정
        Path workdir = isPackageScript ? engineRoot : scriptDir;

        // 4) 커맨드 구성
        List<String> cmd = new ArrayList<>(resolvePythonCmd());
        if (pythonArgs != null && !pythonArgs.isBlank()) {
            cmd.addAll(Arrays.asList(pythonArgs.trim().split("\\s+")));
        }

       // ★ 패키지 스크립트이면 -m public_cert_api.run_public 형태로 실행
        if (isPackageScript) {
            String pkgName = scriptDir.getFileName().toString();                 // public_cert_api
            String modName = scriptFn.endsWith(".py")
                    ? scriptFn.substring(0, scriptFn.length() - 3)               // run_public
                    : scriptFn;
            cmd.add("-m");
            cmd.add(pkgName + "." + modName);
        } else {
            // 일반 파일이면 파일명 그대로
            cmd.add(scriptFn);
        }

        if (configPath != null && !configPath.isBlank()) {
            cmd.add("--config"); cmd.add(configPath);
        }
        if (certName != null && !certName.isBlank()) {
            cmd.add("--cert"); cmd.add(certName);
        }
        cmd.add("--out"); cmd.add(jsonPath);
        if (extraArgs != null && !extraArgs.isEmpty()) {
            cmd.addAll(extraArgs);
        }


        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        // 5) 환경변수 보강 (패키지 모듈 import 위해)
        Map<String, String> env = pb.environment();
        env.putIfAbsent("PYTHONIOENCODING", "utf-8");
        if (isPackageScript && engineRoot != null) {
            String cur = env.getOrDefault("PYTHONPATH", "");
            String val = engineRoot.toString();
            if (!cur.isBlank()) val = cur + File.pathSeparator + val;
            env.put("PYTHONPATH", val);
        }

        // 6) 작업 디렉터리 적용
        pb.directory(workdir.toFile());

        System.out.println("[EngineRunner] CWD: " + workdir);
        System.out.println("[EngineRunner] Run: " + String.join(" ", cmd));

        Process p = pb.start();

        StringBuilder outBuf = new StringBuilder();
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            for (String line; (line = r.readLine()) != null; ) {
                outBuf.append(line).append(System.lineSeparator());
            }
        }

        int exit = p.waitFor();
        System.out.println("📦 종료 코드: " + exit);
        if (exit != 0) {
            throw new IllegalStateException("Python process exited with code " + exit
                    + "\n--- python output ---\n" + outBuf);
        }
    }

    /** python.path가 비어있으면 OS별로 리트라이 */
    private List<String> resolvePythonCmd() {
        if (pythonPath != null && !pythonPath.isBlank()) {
            return List.of(pythonPath);
        }
        String os = System.getProperty("os.name", "").toLowerCase();
        List<List<String>> candidates = new ArrayList<>();
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
            p.waitFor(); // 즉시 종료/대기 모두 허용
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
