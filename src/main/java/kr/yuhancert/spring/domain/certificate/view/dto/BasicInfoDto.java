package kr.yuhancert.spring.domain.certificate.view.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BasicInfoDto(
        String overview,
        List<HistoryItemDto> history,
        PublicMetaDto _meta
) {
    public BasicInfoDto {
        if (overview == null) overview = "";
        if (history == null) history = List.of();
    }
    public static BasicInfoDto empty() {
        return new BasicInfoDto("", List.of(), null);
    }
}

