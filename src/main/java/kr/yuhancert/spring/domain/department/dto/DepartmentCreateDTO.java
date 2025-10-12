package kr.yuhancert.spring.domain.department.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 리액트에서 추가 하기위한 학과,전공 리스트
 *
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentCreateDTO {
    private String name;
    private List<String> majors;
}
