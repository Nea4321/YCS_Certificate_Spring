package kr.yuhancert.spring.domain.certificate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CertDataDTO {
    private Long id;
    private Integer implYy;
    private Integer implSeq;
    private String description;
    private Long docRegStartDt;
    private Long docRegEndDt;
    private Long docExamStartDt;
    private Long docExamEndDt;
    private Long docPassDt;
    private Long pracRegStartDt;
    private Long pracRegEndDt;
    private Long pracExamStartDt;
    private Long pracExamEndDt;
    private Long pracPassDt;
}
