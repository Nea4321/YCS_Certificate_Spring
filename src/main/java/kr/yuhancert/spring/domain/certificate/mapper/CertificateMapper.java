package kr.yuhancert.spring.domain.certificate.mapper;

import kr.yuhancert.spring.domain.certificate.dto.CertificateDTO;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Mapper(componentModel = "spring")
@Qualifier("certificateMapper")
public interface CertificateMapper {
    @Mapping(source = "id", target = "certificate_id")
    @Mapping(source = "certificateName", target = "certificate_name")
    @Mapping(source = "jmcd.jmcd", target = "jmcd")
    CertificateDTO toCertificateDTO(Certificate certificate);
    List<CertificateDTO> toCertificateDTOList(List<Certificate> __certificateList);

}
