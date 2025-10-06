package kr.yuhancert.spring.domain.certificate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertDataDTO {
    private Long certificate_id;
    private String certificate_name;
    private Map<String, Object> basic_info;
    private List<Map<String, Object>> schedule;
    private Map<String, Object> other_info;
    private Long organization_id;
}
