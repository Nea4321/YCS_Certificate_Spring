package kr.yuhancert.spring.domain.certificate.mapper;

import kr.yuhancert.spring.domain.certificate.dto.CertDataDTO;
import kr.yuhancert.spring.domain.certificate.entity.CertData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;

@Mapper(componentModel = "spring")
@Qualifier("certDataMapper")
public interface CertDataMapper {
    @Mapping(source = "id", target = "certificate_id")
    @Mapping(source = "certificateName", target = "certificate_name")
    @Mapping(source = "basicInfo", target = "basic_info")
    @Mapping(source = "schedule", target = "schedule")
    @Mapping(source = "otherInfo", target = "other_info")
    @Mapping(source = "organization.id", target = "organization_id")
    CertDataDTO toCertDataDTO(CertData __certData);
}
