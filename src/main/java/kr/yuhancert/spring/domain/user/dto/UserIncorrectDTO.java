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
public class UserIncorrectDTO {
    Long certificate_id;
    String certificate_name;
    List<UserIncorrectQuestionDTO> userIncorrectQuestionDTOList;
}
