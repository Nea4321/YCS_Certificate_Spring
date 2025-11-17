package kr.yuhancert.spring.domain.user.dto;

import kr.yuhancert.spring.domain.cbt.dto.PreviousDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreviousDTO {
    PreviousDTO previous;
    List<UserAnswerDTO> userAnswer;
}
