package kr.yuhancert.spring.domain.certificate.view.dto;

public record InfoSectionDto(
        String key,      // 내부 키: "method", "fee" 등
        String title,    // 화면용 타이틀: "시험방법", "수수료"
        String html      // 섹션 내용(HTML/문단 텍스트)
) {}
