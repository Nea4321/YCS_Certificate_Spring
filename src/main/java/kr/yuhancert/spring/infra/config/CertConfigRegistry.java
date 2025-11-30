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

    private final Map<Long, CertConfig> configRegistry = new HashMap<>();
    private final Map<String, CertConfig> fallbackConfigs = new HashMap<>();

    @Value("${cert.engine.root:private-cert-crawl}")
    private String engineRoot;

    @Value("${cert.json.root:src/main/resources/json}")
    private String jsonRoot;

    @PostConstruct
    void init() {
        Path projectRoot = Paths.get(System.getProperty("user.dir"));
        Path engineBase  = projectRoot.resolve(engineRoot);
        Path jsonBase    = projectRoot.resolve(jsonRoot);

        String runner = engineBase.resolve("run_once.py").toString();

        // ★ 여기 추가: private cert용 config 경로
        String privateConfigPath = engineBase
                .resolve("private-cert-crawl/configs/cert_map.yaml")
                .toString();

        // ===== 1) 이름 기반 fallback =====
        fallbackConfigs.put("linux_master", new CertConfig(
                runner,
                jsonBase.resolve("linux_master_full.json").toString(),
                "linux_master",
                // ★ 여기서 --config 옵션을 extraArgs로 넘김
                java.util.List.of("--config", privateConfigPath)
        ));

        fallbackConfigs.put("digital_information", new CertConfig(
                runner,
                jsonBase.resolve("digital_information_full.json").toString(),
                "digital_information",
                java.util.List.of("--config", privateConfigPath)
        ));

        fallbackConfigs.put("coding_ability", new CertConfig(
                runner,
                jsonBase.resolve("coding_ability_full.json").toString(),
                "coding_ability",
                java.util.List.of("--config", privateConfigPath)
        ));

        // 나머지도 전부 같은 식으로
        fallbackConfigs.put("Computerized_tax_accounting", new CertConfig(
                runner,
                jsonBase.resolve("Computerized_tax_accounting_full.json").toString(),
                "Computerized_tax_accounting",
                java.util.List.of("--config", privateConfigPath)
        ));

        fallbackConfigs.put("barista", new CertConfig(
                runner,
                jsonBase.resolve("barista_full.json").toString(),
                "barista",
                java.util.List.of("--config", privateConfigPath)
        ));
        fallbackConfigs.put("CS_Leaders", new CertConfig(
                runner,
                jsonBase.resolve("Cs_Leader_full.json").toString(),
                "CS_Leaders",
                java.util.List.of("--config", privateConfigPath)
        ));
        fallbackConfigs.put("gtq", new CertConfig(
                runner,
                jsonBase.resolve("gtq_full.json").toString(),
                "gtq",
                java.util.List.of("--config", privateConfigPath)
        ));
        fallbackConfigs.put("erp_information", new CertConfig(
                runner,
                jsonBase.resolve("erp_information_full.json").toString(),
                "erp_information",
                java.util.List.of("--config", privateConfigPath)
        ));
        fallbackConfigs.put("itq", new CertConfig(
                runner,
                jsonBase.resolve("itq_full.json").toString(),
                "itq",
                java.util.List.of("--config", privateConfigPath)
        ));

        // ===== 2) 민간 certificate_id → config 연결 =====
        // DB에서 확인한 id 기준으로 매핑 (예: 665 리눅스마스터, 675 ERP정보관리사, 666 디지털정보활용능력)
        configRegistry.put(665L, fallbackConfigs.get("linux_master"));
        configRegistry.put(675L, fallbackConfigs.get("erp_information"));
        configRegistry.put(666L, fallbackConfigs.get("digital_information"));
        configRegistry.put(669L, fallbackConfigs.get("coding_ability"));
        configRegistry.put(672L, fallbackConfigs.get("barista"));
        configRegistry.put(673L, fallbackConfigs.get("Computerized_tax_accounting"));
        configRegistry.put(676L, fallbackConfigs.get("gtq"));
        configRegistry.put(677L, fallbackConfigs.get("itq"));
        configRegistry.put(674L, fallbackConfigs.get("CS_Leaders"));
        // 나머지 민간도 필요하면 여기 계속 추가하면 됨
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
