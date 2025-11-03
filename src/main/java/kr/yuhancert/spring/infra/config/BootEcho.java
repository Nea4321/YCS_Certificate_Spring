package kr.yuhancert.spring.infra.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BootEcho {
    @Value("${app.base}") String appBase;
    @Value("${public.script}") String publicScript;
    @Value("${python.path}") String pythonPath;
    @Value("${cert.json.root}") String jsonRoot;

    @PostConstruct
    void echo() {
        log.info("[cfg] app.base={}", appBase);
        log.info("[cfg] public.script={}", publicScript);
        log.info("[cfg] python.path={}", pythonPath);
        log.info("[cfg] cert.json.root={}", jsonRoot);
    }
}

