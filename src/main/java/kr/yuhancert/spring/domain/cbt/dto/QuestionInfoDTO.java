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
public class QuestionInfoDTO {
    Long question_info_id;
    String question_info_name;
    List<QuestionTypeDTO> question_types;
}
