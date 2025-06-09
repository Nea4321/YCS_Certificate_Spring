package kr.yuhancert.spring.domain.certificate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CertDeptDTO {
    private Long id;
    private Long certificate;
    private Long deptMap;
}
