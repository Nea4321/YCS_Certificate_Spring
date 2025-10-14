package kr.yuhancert.spring.domain.certificate.mapper;

import kr.yuhancert.spring.domain.certificate.dto.TagDTO;
import kr.yuhancert.spring.domain.certificate.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Mapper(componentModel = "spring")
@Qualifier("tagMapper")
public interface TagMapper {
    @Mapping(source = "id" ,target = "tag_id")
    @Mapping(source = "tagName", target = "tag_name")
    @Mapping(source = "color", target = "color")
    TagDTO toTagDTO(Tag tag);

    List<TagDTO> toTagListDTO(List<Tag> __tagList);
}
