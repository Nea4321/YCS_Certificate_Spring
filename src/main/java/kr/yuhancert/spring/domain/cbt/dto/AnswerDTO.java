package kr.yuhancert.spring.domain.cbt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
    Long answer_id;
    Long question_id;
    Boolean bool;
    String content;
    String img;
}
