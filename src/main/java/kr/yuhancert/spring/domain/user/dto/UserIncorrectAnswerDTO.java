package kr.yuhancert.spring.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserIncorrectAnswerDTO {
    Long answer_id;
    Boolean bool;
    String content;
    String img;
}
