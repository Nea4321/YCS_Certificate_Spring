package kr.yuhancert.spring.domain.certificate.mapper;

import kr.yuhancert.spring.domain.certificate.dto.CertDataDTO;
import kr.yuhancert.spring.domain.certificate.entity.CertData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CertDataMapper {
    @Mapping(source = "id", target = "cert_data_id")
    CertDataDTO toCertDataDTO(CertData __certData);
}
