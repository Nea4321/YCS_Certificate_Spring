package kr.yuhancert.spring.domain.department.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeptMapDataCertDTO {
    private Long certificate_id;
    private String certificate_name;
}
