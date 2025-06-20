package kr.yuhancert.spring.domain.department.mapper;

import kr.yuhancert.spring.domain.department.dto.DeptListChildDTO;
import kr.yuhancert.spring.domain.department.dto.DeptListDTO;
import kr.yuhancert.spring.domain.department.entity.DeptMap;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface DeptListMapper {
    String DP_TABLE = "department";
    String FC_TABLE = "faculty";
    String MJ_TABLE = "major";

    default List<DeptListDTO> toDeptListDTOList(List<DeptMap> __deptMap) {
        if (__deptMap == null || __deptMap.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, DeptListDTO> dtoMap = new HashMap<>();

        for (DeptMap dm : __deptMap) {
            DeptListDTO parentDTO = createParentDTO(dm);
            DeptListChildDTO childDTO = createChildDTO(dm);

            String key = parentDTO.getParent_type() + ":" + parentDTO.getParent_id();

            if (!dtoMap.containsKey(key)) {
                dtoMap.put(key, parentDTO);
            }

            dtoMap.get(key).getChild().add(childDTO);
        }

        return new ArrayList<>(dtoMap.values());
    }

     private DeptListDTO createParentDTO(DeptMap __deptMap) {
        DeptMap dm = __deptMap;

        if ( dm != null && dm.getFaculty() != null) {
            return new DeptListDTO(
                    FC_TABLE,
                    dm.getFaculty().getId(),
                    dm.getFaculty().getFacultyName(),
                    new ArrayList<>()
            );
        } else if ( dm != null && dm.getDepartment() != null) {
            return new DeptListDTO(
                    DP_TABLE,
                    dm.getDepartment().getId(),
                    dm.getDepartment().getDepartmentName(),
                    new ArrayList<>()
            );
        } else {
            return new DeptListDTO(
                    MJ_TABLE,
                    dm.getMajor().getId(),
                    dm.getMajor().getMajorName(),
                    new ArrayList<>()
            );
        }
    }

    private DeptListChildDTO createChildDTO(DeptMap __deptMap) {
        DeptMap dm = __deptMap;

        if ( dm != null && dm.getMajor() != null) {
            return new DeptListChildDTO(
                    MJ_TABLE,
                    dm.getMajor().getId(),
                    dm.getMajor().getMajorName(),
                    __deptMap.getId()
            );
        } else if ( dm != null && dm.getDepartment() != null) {
            return new DeptListChildDTO(
                    DP_TABLE,
                    __deptMap.getDepartment().getId(),
                    __deptMap.getDepartment().getDepartmentName(),
                    __deptMap.getId()
            );
        } else {
            return new DeptListChildDTO(
                    FC_TABLE,
                    __deptMap.getFaculty().getId(),
                    __deptMap.getFaculty().getFacultyName(),
                    __deptMap.getId()
            );
        }
    }
}
