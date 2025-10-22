package kr.yuhancert.spring.domain.user.mapper;

import kr.yuhancert.spring.domain.user.dto.UserFavoriteDTO;
import kr.yuhancert.spring.domain.user.entity.UserFavorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Mapper(componentModel = "spring")
@Qualifier("userFavoriteMapper")
public interface UserFavoriteMapper {
    @Mapping(source = "type", target = "type")
    @Mapping(source = "typeId", target = "type_id")
    UserFavoriteDTO toUserFavoriteDTO(UserFavorite __userFavorite);
    List<UserFavoriteDTO> toUserFavoriteDTOList(List<UserFavorite> __userFavoriteList);
}
