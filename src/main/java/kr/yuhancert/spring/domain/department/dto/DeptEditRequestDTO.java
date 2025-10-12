package kr.yuhancert.spring.domain.department.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 학부,학과,전공 간단한 이름 수정 DTO
 *
 * */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeptEditRequestDTO {
    private Long id;
    private String type;
    private String value;
}
