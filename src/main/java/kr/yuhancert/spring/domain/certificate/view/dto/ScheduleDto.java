package kr.yuhancert.spring.domain.certificate.view.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScheduleDto(
        @JsonProperty("events") List<ExamEventDto> events,
        @JsonProperty("_meta")  PublicMetaDto _meta
) {
    // ❶ compact canonical ctor: null 들어오면 기본값으로
    public ScheduleDto {
        if (events == null) events = List.of();
    }

    // ❷ 편의 팩토리
    public static ScheduleDto empty() {
        return new ScheduleDto(List.of(), null);
    }
}
