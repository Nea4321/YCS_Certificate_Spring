package kr.yuhancert.spring.domain.certificate.mapper;

import kr.yuhancert.spring.domain.certificate.dto.CertificateDTO;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.certificate.entity.NationalCert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CertificateMapper {
    @Mapping(source = "id", target = "certificate_id")
    @Mapping(source = "certificateName", target = "certificate_name")
    @Mapping(source = "jmcd", target = "jmcd")
    CertificateDTO toCertificateDTO(Certificate certificate);
    List<CertificateDTO> toCertificateDTOList(List<Certificate> __certificateList);

    default String mapJmcd(NationalCert jmcd) {
        return jmcd != null ? jmcd.getJmcd() : null;
    }

}
