package kr.yuhancert.spring.infra.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CertConfig {
    private String scriptPath;
    private String jsonPath;
    private final String certName;   // ex) "linux_master"
    private final List<String> extraArgs;
}
