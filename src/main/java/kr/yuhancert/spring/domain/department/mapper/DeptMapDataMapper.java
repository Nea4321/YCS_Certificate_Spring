package kr.yuhancert.spring.domain.department.mapper;

import kr.yuhancert.spring.domain.department.dto.DeptMapDataCertDTO;
import kr.yuhancert.spring.domain.department.dto.DeptMapDataDTO;
import kr.yuhancert.spring.domain.department.entity.DeptCert;
import kr.yuhancert.spring.domain.department.entity.DeptMap;
import kr.yuhancert.spring.domain.department.entity.DeptMapData;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface DeptMapDataMapper {

    default DeptMapDataDTO toDeptMapDataDTO(DeptMapData __deptMapData, List<DeptCert> __deptCert) {

        if (__deptMapData == null) {
            return null;
        }

        List<DeptMapDataCertDTO> deptMapDataCertDTOList = new ArrayList<>();

        for (DeptCert dc : __deptCert) {

            if (Objects.equals(dc.getDeptMap().getId(), __deptMapData.getId())) {

                DeptMapDataCertDTO deptMapDataCertDTO = createDeptMapDataCertDTO(dc);

                deptMapDataCertDTOList.add(deptMapDataCertDTO);
            }

        }

        DeptMap dm = __deptMapData.getDeptMap();

        if(dm != null && dm.getMajor() != null) {
            return new DeptMapDataDTO(
                    __deptMapData.getId(),
                    dm.getMajor().getMajorName(),
                    deptMapDataCertDTOList,
                    __deptMapData.getDescription()
            );
        }else if( dm != null && dm.getDepartment() != null) {
            return new DeptMapDataDTO(
                    __deptMapData.getId(),
                    dm.getDepartment().getDepartmentName(),
                    deptMapDataCertDTOList,
                    __deptMapData.getDescription()
            );
        }else {
            return new DeptMapDataDTO(
                    __deptMapData.getId(),
                    dm.getFaculty().getFacultyName(),
                    deptMapDataCertDTOList,
                    __deptMapData.getDescription()
            );
        }
    }

    private DeptMapDataCertDTO createDeptMapDataCertDTO(DeptCert __deptCert) {
        return new DeptMapDataCertDTO(
                __deptCert.getCertificate().getId(),
                __deptCert.getCertificate().getCertificateName()
        );
    }

}
