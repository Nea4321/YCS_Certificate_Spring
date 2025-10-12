package kr.yuhancert.spring.domain.department.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 리액트에서 받아오는 학부,학과,전공 리스트
 *
 * */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultyCreateRequestDTO {
    private String facultyName;
    private List<DepartmentCreateDTO> department;
}
