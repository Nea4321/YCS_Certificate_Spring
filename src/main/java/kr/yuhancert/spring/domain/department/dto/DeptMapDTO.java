package kr.yuhancert.spring.domain.department.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeptMapDTO {
    private Long id;
    private Long faculty_id;
    private Long department_id;
    private Long major_id;
}
