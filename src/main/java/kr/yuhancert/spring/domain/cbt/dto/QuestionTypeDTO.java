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
public class QuestionTypeDTO {
    Long question_type_id;
    String question_type_name;
    List<QuestionDTO> questions;
}
