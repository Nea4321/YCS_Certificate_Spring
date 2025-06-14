package kr.yuhancert.spring.domain.certificate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CertDeptDTO {
    private Long cert_dept_id;
    private Long certificate_id;
    private Long dept_map_id;
}
