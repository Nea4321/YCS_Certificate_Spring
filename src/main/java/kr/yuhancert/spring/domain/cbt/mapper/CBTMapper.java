package kr.yuhancert.spring.domain.cbt.mapper;

import kr.yuhancert.spring.domain.cbt.dto.CBTDTO;
import kr.yuhancert.spring.domain.cbt.entity.QuestionInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Mapper(componentModel = "spring")
@Qualifier("cbtMapper")
public interface CBTMapper {
    @Mapping(source = "id", target = "question_info_id")
    @Mapping(source = "questionInfoName", target = "question_info_name")
    @Mapping(source = "main", target = "main")
    CBTDTO toCBTDTO(QuestionInfo questionInfo);
    List<CBTDTO> toCBTDTOList(List<QuestionInfo> questionInfos);
}
