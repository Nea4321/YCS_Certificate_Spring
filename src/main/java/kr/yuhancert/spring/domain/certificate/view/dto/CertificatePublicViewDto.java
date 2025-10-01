package kr.yuhancert.spring.domain.certificate.view.dto;

public record CertificatePublicViewDto(
        PublicMetaDto _meta,
        ScheduleDto schedule,
        ExamInfoDto examInfo,
        BasicInfoDto basicInfo,
        PreferenceDto preference
) {
    public static CertificatePublicViewDto empty() {
        return new CertificatePublicViewDto(
                null,
                ScheduleDto.empty(),
                ExamInfoDto.empty(),
                BasicInfoDto.empty(),
                PreferenceDto.empty()
        );
    }
}
