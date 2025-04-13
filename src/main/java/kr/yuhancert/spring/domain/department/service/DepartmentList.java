package kr.yuhancert.spring.domain.department.service;

import kr.yuhancert.spring.domain.department.dto.DeptListChildDTO;
import kr.yuhancert.spring.domain.department.dto.DeptListDTO;
import kr.yuhancert.spring.domain.department.entity.DeptMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentList {

    private List<DeptMap> deptMapEntities;
    private Map<String, DeptListDTO> dtoMap;


    public DepartmentList() {
        this.deptMapEntities = new ArrayList<>();
        this.dtoMap = new HashMap<>();
    }


    public List<DeptListDTO> createDeptList(List<DeptMap> __deptMap){

        deptMapEntities = __deptMap;
        dtoMap = new HashMap<>();
        String TABLE_TYPE_DP = "department";
        String TABLE_TYPE_FC = "faculty";
        String TABLE_TYPE_MJ = "major";

        for(DeptMap dm : deptMapEntities){

            DeptListDTO parentDTO;
            DeptListChildDTO childDTO;

            if (dm.getFaculty() != null) {
                parentDTO = new DeptListDTO(
                        TABLE_TYPE_FC,
                        dm.getFaculty().getId(),
                        dm.getFaculty().getFacultyName(),
                        new ArrayList<>()
                );
            } else if (dm.getDepartment() != null) {
                parentDTO = new DeptListDTO(
                        TABLE_TYPE_DP,
                        dm.getDepartment().getId(),
                        dm.getDepartment().getDepartmentName(),
                        new ArrayList<>()
                );
            } else {
                parentDTO = new DeptListDTO(
                        TABLE_TYPE_MJ,
                        dm.getMajor().getId(),
                        dm.getMajor().getMajorName(),
                        new ArrayList<>()
                );
            }

            if (dm.getMajor() != null) {
                childDTO = new DeptListChildDTO(
                        TABLE_TYPE_MJ,
                        dm.getMajor().getId(),
                        dm.getMajor().getMajorName()
                );
            } else if (dm.getDepartment() != null) {
                childDTO = new DeptListChildDTO(
                        TABLE_TYPE_DP,
                        dm.getDepartment().getId(),
                        dm.getDepartment().getDepartmentName()
                );
            } else {
                childDTO = new DeptListChildDTO(
                        TABLE_TYPE_FC,
                        dm.getFaculty().getId(),
                        dm.getFaculty().getFacultyName()
                );
            }

            String key = parentDTO.getParent_type() + ":" + parentDTO.getParent_id();

            if (!dtoMap.containsKey(key)) {
                dtoMap.put(key, parentDTO);
            }

            dtoMap.get(key).getChild().add(childDTO);

        }

        return new ArrayList<>(dtoMap.values());
    }


}
