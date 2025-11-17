package kr.yuhancert.spring.domain.cbt.mapper;

import kr.yuhancert.spring.domain.cbt.dto.PreviousDTO;
import kr.yuhancert.spring.domain.cbt.entity.Previous;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;

@Mapper(componentModel = "spring")
@Qualifier("previousMapper")
public interface PreviousMapper {
    @Mapping(source = "id", target = "previous_id")
    @Mapping(source = "previousName", target = "previous_name")
    @Mapping(source = "list", target = "list")
    PreviousDTO toPreviousDTO(Previous previous);
}
