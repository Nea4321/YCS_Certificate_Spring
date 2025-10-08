package kr.yuhancert.spring.domain.certificate.mapper;

import kr.yuhancert.spring.domain.certificate.dto.CertificateDTO;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
@Qualifier("certificateMapper")
public interface CertificateMapper {
//    @Mapping(source = "id", target = "certificate_id")
//    @Mapping(source = "certificateName", target = "certificate_name")
//    @Mapping(source = "jmcd", target = "jmcd")
//    @Mapping(source = "organization.id", target = "organization_id")
//    List<CertificateDTO> toCertificateDTOList(List<Certificate> __certificateList);

    default List<CertificateDTO> toCertificateDTOList(List<Certificate> __certificateList, Map<Long, List<Long>> __tagMapMap) {

        List<CertificateDTO> certificateDTOList = new ArrayList<>();

        for (Certificate cert : __certificateList) {

            List<Long> tagList = __tagMapMap.getOrDefault(cert.getId(), new ArrayList<>());

            certificateDTOList.add( new CertificateDTO(cert.getId(),
                    cert.getCertificateName(),
                    cert.getJmcd(),
                    cert.getOrganization().getId(),
                    tagList));
        }

        return certificateDTOList;
    }

}
