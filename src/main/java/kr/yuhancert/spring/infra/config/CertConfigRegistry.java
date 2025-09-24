package kr.yuhancert.spring.infra.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class CertConfigRegistry {

    // certId → 개별 설정(있으면 이걸 우선 사용)
    private final Map<Long, CertConfig> configRegistry = new HashMap<>();

    // 이름 기반 fallback 설정(프로젝트 루트 기준 상대 경로로 동적 생성)
    private final Map<String, CertConfig> fallbackConfigs = new HashMap<>();

    // application.properties 에서 덮어쓸 수 있는 루트 경로(기본값 제공)
    @Value("${cert.engine.root:private-cert-crawl}")
    private String engineRoot;   // 예: private-cert-crawl

    @Value("${cert.json.root:src/main/resources/json}")
    private String jsonRoot;     // 예: src/main/resources/json

    @PostConstruct
    void init() {
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path engineBase  = projectRoot.resolve(engineRoot); // private-cert-crawl
        Path jsonBase    = projectRoot.resolve(jsonRoot);   // src/main/resources/json

        // run_once.py는 루트에 존재
        String runner = engineBase.resolve("run_once.py").toString();

        fallbackConfigs.put("linux_master", new CertConfig(
                runner,
                jsonBase.resolve("linux_master_full.json").toString(),
                "linux_master",
                Collections.emptyList()          // ← 추가
        ));

        fallbackConfigs.put("digital_information", new CertConfig(
                runner,
                jsonBase.resolve("digital_information_full.json").toString(),
                "digital_information",
                Collections.emptyList()          // ← 추가
        ));

        fallbackConfigs.put("coding_ability", new CertConfig(
                runner,
                jsonBase.resolve("coding_ability_full.json").toString(),
                "coding_ability",
                Collections.emptyList()          // ← 추가
        ));

        fallbackConfigs.put("Computerized_tax_accounting", new CertConfig(
                runner,
                jsonBase.resolve("Computerized_tax_accounting_full.json").toString(),
                "Computerized_tax_accounting",
                Collections.emptyList()          // ← 추가
        ));
        fallbackConfigs.put("barista", new CertConfig(
                runner,
                jsonBase.resolve("barista_full.json").toString(),
                "barista",
                Collections.emptyList()          // ← 추가
        ));

        fallbackConfigs.put("CS_Leaders", new CertConfig(
                runner,
                jsonBase.resolve("Cs_Leader_full.json").toString(),
                "CS_Leaders",
                Collections.emptyList()          // ← 추가
        ));

        fallbackConfigs.put("gtq", new CertConfig(
                runner,
                jsonBase.resolve("gtq_full.json").toString(),
                "gtq",
                Collections.emptyList()          // ← 추가
        ));

        fallbackConfigs.put("erp_information", new CertConfig(
                runner,
                jsonBase.resolve("erp_information_full.json").toString(),
                "erp_information",
                Collections.emptyList()          // ← 추가
        ));

        fallbackConfigs.put("itq", new CertConfig(
                runner,
                jsonBase.resolve("itq_full.json").toString(),
                "itq",
                Collections.emptyList()          // ← 추가
        ));
        //이쪽은 하드코딩이어서 나중에 고칠수 있음
    }

    public CertConfig get(Long certId) {
        return configRegistry.get(certId);
    }

    public CertConfig getFallback(String certName) {
        return fallbackConfigs.get(certName);
    }

    public void register(Long certId, CertConfig config) {
        configRegistry.put(certId, config);
    }
}
