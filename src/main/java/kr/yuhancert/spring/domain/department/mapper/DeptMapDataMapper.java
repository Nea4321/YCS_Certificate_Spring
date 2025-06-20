package kr.yuhancert.spring.domain.department.mapper;

import kr.yuhancert.spring.domain.department.dto.DeptMapDataCertDTO;
import kr.yuhancert.spring.domain.department.dto.DeptMapDataDTO;
import kr.yuhancert.spring.domain.department.entity.DeptCert;
import kr.yuhancert.spring.domain.department.entity.DeptMap;
import kr.yuhancert.spring.domain.department.entity.DeptMapData;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface DeptMapDataMapper {
    default List<DeptMapDataDTO> toDeptMapDataDTOList(List<DeptMapData> __deptMapDataList, List<DeptCert> __deptCert) {

        if (__deptMapDataList == null || __deptMapDataList.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, DeptMapDataDTO> dtoMap = new HashMap<>();

        for (DeptMapData dm : __deptMapDataList) {

            DeptMapDataDTO deptMapDataDTO = createDeptMapDataDTO(dm);

            Long key = deptMapDataDTO.getDept_map_id();

            dtoMap.put(key, deptMapDataDTO);

        }

        for (DeptCert dc : __deptCert) {

            DeptMapDataCertDTO deptMapDataCertDTO = createDeptMapDataCertDTO(dc);

            dtoMap.get(dc.getDeptMap().getId()).getCert().add(deptMapDataCertDTO);

        }

        return new ArrayList<>(dtoMap.values());
    }

    private DeptMapDataDTO createDeptMapDataDTO(DeptMapData __deptMapData) {

        DeptMap dm = __deptMapData.getDeptMap();

        if(dm != null && dm.getMajor() != null) {
            return new DeptMapDataDTO(
                    __deptMapData.getId(),
                    dm.getMajor().getMajorName(),
                    new ArrayList<>(),
                    __deptMapData.getDescription()
            );
        }else if( dm != null && dm.getDepartment() != null) {
            return new DeptMapDataDTO(
                    __deptMapData.getId(),
                    dm.getDepartment().getDepartmentName(),
                    new ArrayList<>(),
                    __deptMapData.getDescription()
            );
        }else {
            return new DeptMapDataDTO(
                    __deptMapData.getId(),
                    dm.getFaculty().getFacultyName(),
                    new ArrayList<>(),
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
