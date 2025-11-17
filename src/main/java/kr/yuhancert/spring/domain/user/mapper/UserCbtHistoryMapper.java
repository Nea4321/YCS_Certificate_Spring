package kr.yuhancert.spring.domain.user.mapper;

import kr.yuhancert.spring.domain.user.dto.UserCbtHistoryResponseDTO;
import kr.yuhancert.spring.domain.user.entity.UserCbtHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;

@Mapper(componentModel = "spring")
@Qualifier("userCbtHistoryMapper")
public interface UserCbtHistoryMapper {
    @Mapping(source = "id", target = "user_cbt_history_id")
    @Mapping(source = "user.id", target = "user_id")
    @Mapping(source = "certificate.id", target = "certificate_id")
    @Mapping(source = "certificate.name", target = "certificate_name")
    @Mapping(source = "score", target = "score")
    @Mapping(source = "correctCount", target = "correct_count")
    @Mapping(source = "previous", target = "previous")
    @Mapping(source = "createdAt", target = "created_at")
    @Mapping(source = "leftTTime", target = "left_time")
    UserCbtHistoryResponseDTO toUserCbtHistoryResponseDTO(UserCbtHistory __userCbtHistory);
}
