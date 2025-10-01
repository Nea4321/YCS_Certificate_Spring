package kr.yuhancert.spring.domain.certificate.view.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PreferenceDto(
        List<InfoSectionDto> sections,
        PublicMetaDto _meta
) {
    public PreferenceDto {
        if (sections == null) sections = List.of();
    }
    public static PreferenceDto empty() {
        return new PreferenceDto(List.of(), null);
    }
}
