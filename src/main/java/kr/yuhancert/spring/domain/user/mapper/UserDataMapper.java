package kr.yuhancert.spring.domain.user.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;

@Mapper(componentModel = "spring")
@Qualifier("userDataMapper")
public interface UserDataMapper {

}
