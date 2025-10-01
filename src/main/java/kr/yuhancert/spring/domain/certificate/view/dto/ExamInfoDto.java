package kr.yuhancert.spring.domain.certificate.view.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExamInfoDto(
        List<InfoSectionDto> sections,
        PublicMetaDto _meta
) {
    public ExamInfoDto {
        if (sections == null) sections = List.of();
    }
    public static ExamInfoDto empty() {
        return new ExamInfoDto(List.of(), null);
    }
}
