package kr.yuhancert.spring.domain.department.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor  // 모든 필드를 초기화하는 생성자
@NoArgsConstructor   // JSON 직렬화/역직렬화를 위한 기본 생성자
public class DeptListDTO {

    private String parent_type;
    private Long parent_id;
    private String parent_name;
    private List<DeptListChildDTO> child;

}
