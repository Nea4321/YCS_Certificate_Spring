package kr.yuhancert.spring.domain.department.dto;

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
public class DeptMapDataDTO {
    private Long dept_map_id;
    private String dept_map_name;
    private List<DeptMapDataCertDTO> cert;
    private Map<String, Object> description;
}
