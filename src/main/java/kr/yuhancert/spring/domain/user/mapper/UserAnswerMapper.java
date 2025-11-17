package kr.yuhancert.spring.domain.user.mapper;

import kr.yuhancert.spring.domain.user.dto.UserAnswerDTO;
import kr.yuhancert.spring.domain.user.entity.UserAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Mapper(componentModel = "spring")
@Qualifier("userAnswerMapper")
public interface UserAnswerMapper {
    @Mapping(source = "answer.id", target = "answer_id")
    @Mapping(source = "bool", target = "bool")
    UserAnswerDTO touUserAnswerDTO(UserAnswer userAnswer);
    List<UserAnswerDTO> toUserAnswerDTOList(List<UserAnswer> userAnswers);
}
