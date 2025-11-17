package kr.yuhancert.spring.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserIncorrectQuestionDTO {
    Long question_id;
    String text;
    String content;
    String img;
    List<UserIncorrectAnswerDTO> userIncorrectAnswerDTOList;
}
