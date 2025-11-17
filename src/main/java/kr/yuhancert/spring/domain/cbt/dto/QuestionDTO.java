package kr.yuhancert.spring.domain.cbt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    Long question_num;
    Long question_id;
    String text;
    String content;
    String img;
    List<AnswerDTO> answers;
}
