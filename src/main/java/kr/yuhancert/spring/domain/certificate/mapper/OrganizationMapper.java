package kr.yuhancert.spring.domain.certificate.mapper;

import kr.yuhancert.spring.domain.certificate.dto.OrganizationDTO;
import kr.yuhancert.spring.domain.certificate.entity.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@Mapper(componentModel = "spring")
@Qualifier("organizationMapper")
public interface OrganizationMapper {
    @Mapping(source = "id", target = "organization_id")
    @Mapping(source = "organizationName", target = "organization_name")
    OrganizationDTO toOrganizationDTO(Organization organization);
    List<OrganizationDTO> toOrganizationDTOList(List<Organization> __organizationList);
}
