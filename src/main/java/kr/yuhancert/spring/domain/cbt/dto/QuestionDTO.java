package kr.yuhancert.spring.domain.cbt.dto;

import java.util.List;

public class QuestionDTO {
    Long question_num;
    Long question_id;
    String text;
    String content;
    String img;
    List<AnswerDTO> answers;
}
