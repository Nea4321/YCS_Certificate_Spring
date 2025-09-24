package kr.yuhancert.spring.infra.crawling.manager;

import kr.yuhancert.spring.domain.certificate.service.JsonCertificateParser;
import kr.yuhancert.spring.infra.config.CertConfig;
import kr.yuhancert.spring.infra.config.CertConfigRegistry;
import kr.yuhancert.spring.infra.crawling.engine.EngineRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class CertificateExecutor {

    private final CertConfigRegistry configRegistry;
    private final EngineRunner engineRunner;
    private final JsonCertificateParser parser;

    /**
     * certId 가 있으면 DB 저장(+parseAndSave), 없으면 fallback 이름으로 실행 후 JSON만 파싱(parseJsonOnly)
     */
    public void runAndSave(Long certId, String fallbackKeyIfNoId) throws Exception {
        String jsonPath, certName;

        if (certId != null) {
            CertConfig cfg = configRegistry.get(certId);
            if (cfg == null) throw new IllegalArgumentException("❌ config 없음: " + certId);

            engineRunner.run(
                    cfg.getScriptPath(),
                    null,
                    cfg.getCertName(),
                    cfg.getJsonPath(),
                    cfg.getExtraArgs() == null ? Collections.emptyList() : cfg.getExtraArgs()
            );
            parser.parseAndSave(cfg.getJsonPath(), certId);
            return;
        } else {
            CertConfig fallback = configRegistry.getFallback(fallbackKeyIfNoId);
            if (fallback == null) throw new IllegalStateException("❌ fallback도 없음: " + fallbackKeyIfNoId);

            jsonPath   = fallback.getJsonPath();
            certName   = fallback.getCertName();

            System.err.println("⚠ fallback 실행. name: " + fallbackKeyIfNoId);
            engineRunner.run(certName, jsonPath);
            parser.parseJsonOnly(jsonPath);
        }
    }
}
