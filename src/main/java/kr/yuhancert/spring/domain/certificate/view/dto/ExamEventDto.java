package kr.yuhancert.spring.domain.certificate.view.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public record ExamEventDto(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
        String label,                 // 예: "필기접수"
        ExamEventType type            // DOC_REG 등
) {}
