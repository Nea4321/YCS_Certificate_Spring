package kr.yuhancert.spring.domain.department.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyandDepartmentDTO {
        private String facultyName;
        private List<String> departments;

}
