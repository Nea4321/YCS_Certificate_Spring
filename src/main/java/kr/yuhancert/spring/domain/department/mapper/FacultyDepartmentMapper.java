package kr.yuhancert.spring.domain.department.mapper;

import kr.yuhancert.spring.domain.department.dto.FacultyandDepartmentDTO;
import kr.yuhancert.spring.domain.department.entity.DeptMap;
import org.mapstruct.Mapper;

import java.util.*;

@Mapper(componentModel = "spring")
public interface FacultyDepartmentMapper {

    default List<FacultyandDepartmentDTO> toHierarchy(List<DeptMap> deptMaps) {
        Map<String, FacultyandDepartmentDTO> map = new HashMap<>();

        if (deptMaps == null || deptMaps.isEmpty()) return new ArrayList<>();

        for (DeptMap dm : deptMaps) {
            if (dm.getFaculty() != null && dm.getDepartment() != null) {
                String facultyName = dm.getFaculty().getFacultyName();
                String deptName = dm.getDepartment().getDepartmentName();

                // 이미 facultyName이 있으면 가져오고 없으면 새로 생성
                FacultyandDepartmentDTO dto = map.computeIfAbsent(facultyName,
                        k -> new FacultyandDepartmentDTO(facultyName, new ArrayList<>()));

                // 학과 중복 방지
                if (!dto.getDepartments().contains(deptName)) {
                    dto.getDepartments().add(deptName);
                }
            }
        }
        return new ArrayList<>(map.values());
    }
}
