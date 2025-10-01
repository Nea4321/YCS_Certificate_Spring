package kr.yuhancert.spring.domain.certificate.view.dto;

public record HistoryItemDto(
        String date,     // "1974-10-16"
        String title,    // "기계기술사(건설기계)"
        String law,      // "제7283호"
        String rawTop    // 원문(있으면)
) {}
