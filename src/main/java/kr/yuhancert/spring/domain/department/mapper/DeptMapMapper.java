package kr.yuhancert.spring.domain.department.mapper;

import kr.yuhancert.spring.domain.department.dto.DeptMapDTO;
import kr.yuhancert.spring.domain.department.entity.DeptMap;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeptMapMapper {
    @Mapping(source = "id", target = "dept_map_id")
    @Mapping(source = "faculty.id", target = "faculty_id")
    @Mapping(source = "department.id", target = "department_id")
    @Mapping(source = "major.id", target = "major_id")
    DeptMapDTO toDeptMapDTO(DeptMap __deptMap);
    List<DeptMapDTO> toDeptMapDTOList(List<DeptMap> __deptMapList);
}
