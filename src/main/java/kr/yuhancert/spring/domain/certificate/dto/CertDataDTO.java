package kr.yuhancert.spring.domain.certificate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertDataDTO {
    private Long certificate_id;
    private String certificate_name;
    private String infogb;
    private String contents;
}
