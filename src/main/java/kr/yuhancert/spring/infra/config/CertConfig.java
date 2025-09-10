package kr.yuhancert.spring.infra.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CertConfig {
    private String scriptPath;
    private String jsonPath;
    private final String certName;   // ex) "linux_master"
}
