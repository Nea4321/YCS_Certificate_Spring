package kr.yuhancert.spring.domain.department.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor  // 모든 필드를 초기화하는 생성자
@NoArgsConstructor   // JSON 직렬화/역직렬화를 위한 기본 생성자
public class DeptListChildDTO {

    private String child_type;
    private Long child_id;
    private String child_name;

}
