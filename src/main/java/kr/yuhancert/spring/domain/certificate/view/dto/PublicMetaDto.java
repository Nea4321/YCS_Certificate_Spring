package kr.yuhancert.spring.domain.certificate.view.dto;

public record PublicMetaDto(
        String schema,          // 예: public_norm_v1
        String schemaVersion,   // 예: v1
        String generatedAt,     // ISO-8601 문자열
        String jmcd,
        String name
) {}
