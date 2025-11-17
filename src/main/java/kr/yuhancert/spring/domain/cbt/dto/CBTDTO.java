package kr.yuhancert.spring.domain.cbt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CBTDTO {
    Long question_info_id;
    String question_info_name;
    Boolean main;
}
