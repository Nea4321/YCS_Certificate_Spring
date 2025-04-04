package kr.yuhancert.spring.domain.department.service;

import kr.yuhancert.spring.domain.department.dto.DeptListChildDTO;
import kr.yuhancert.spring.domain.department.dto.DeptListDTO;
import kr.yuhancert.spring.domain.department.entity.Department;
import kr.yuhancert.spring.domain.department.entity.DeptMap;
import kr.yuhancert.spring.domain.department.entity.Faculty;
import kr.yuhancert.spring.domain.department.entity.Major;
import kr.yuhancert.spring.domain.department.repository.DepartmentRepository;
import kr.yuhancert.spring.domain.department.repository.DeptMapRepository;
import kr.yuhancert.spring.domain.department.repository.FacultyRepository;
import kr.yuhancert.spring.domain.department.repository.MajorRepository;
import kr.yuhancert.spring.global.cache.service.CacheService;
import kr.yuhancert.spring.global.cache.util.CacheList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DepartmentService {

    private final CacheService cacheService;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final MajorRepository majorRepository;
    private final DeptMapRepository deptMapRepository;
    private List<Department> departmentEntities;
    private List<Faculty> facultyEntities;
    private List<Major> majorEntities;
    private List<DeptMap> deptMapEntities;
    Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    public DepartmentService(CacheService __cacheService,
                             DepartmentRepository __departmentRepository,
                             FacultyRepository __facultyRepository,
                             MajorRepository __majorRepository,
                             DeptMapRepository __deptMapRepository) {

        this.cacheService = __cacheService;
        this.departmentRepository = __departmentRepository;
        this.facultyRepository = __facultyRepository;
        this.majorRepository = __majorRepository;
        this.deptMapRepository = __deptMapRepository;

    }

    public void resetEntities(){
        departmentEntities = departmentRepository.findAll();
        facultyEntities = facultyRepository.findAll();
        majorEntities = majorRepository.findAll();
        deptMapEntities = deptMapRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<DeptListDTO> getDeptList() {

        List<DeptListDTO> cacheDeptList =cacheService.get(CacheList.DEPT_LIST_CACHE.getName(), "all");
        if (cacheDeptList != null) {
            return cacheDeptList;
        }

        if (deptMapEntities == null || deptMapEntities.isEmpty()) {
            logger.debug("Entities are empty");
            resetEntities();
        }

        Map<String, DeptListDTO> dtoMap = new HashMap<>();

        for(DeptMap dm : deptMapEntities) {

            String parent_type;
            Long parent_id;
            String parent_name;
            String child_type;
            Long child_id;
            String child_name;

            /// 부모찾기
            if (dm.getFaculty() != null) {
                Faculty facultyP = dm.getFaculty();
                parent_type = "faculty";
                parent_id = facultyP.getId();
                parent_name = facultyP.getFacultyName();
            } else if (dm.getDepartment() != null) {
                Department departmentP = dm.getDepartment();
                parent_type = "department";
                parent_id = departmentP.getId();
                parent_name = departmentP.getDepartmentName();
            } else {
                Major majorP = dm.getMajor();
                parent_type = "major";
                parent_id = majorP.getId();
                parent_name = majorP.getMajorName();
            }

            /// 자식찾기
            if (dm.getMajor() != null) {
                Major majorC = dm.getMajor();
                child_type = "major";
                child_id = majorC.getId();
                child_name = majorC.getMajorName();
            } else if (dm.getDepartment() != null) {
                Department departmentC = dm.getDepartment();
                child_type = "department";
                child_id = departmentC.getId();
                child_name = departmentC.getDepartmentName();
            } else {
                Faculty facultyC = dm.getFaculty();
                child_type = "faculty";
                child_id = facultyC.getId();
                child_name = facultyC.getFacultyName();
            }

            DeptListChildDTO childDTO = new DeptListChildDTO(child_type, child_id, child_name);

            String key = parent_type + ":" + parent_id;

            DeptListDTO parentDTO = dtoMap.get(key);

            if (parentDTO == null) {
                List<DeptListChildDTO> deptListChildDTOList = new ArrayList<>();
                deptListChildDTOList.add(childDTO);

                parentDTO = new DeptListDTO(parent_type, parent_id, parent_name, deptListChildDTOList);
                dtoMap.put(key, parentDTO);
            } else {
                parentDTO.getChild().add(childDTO);
            }

        }

        List<DeptListDTO> deptList = new ArrayList<>(dtoMap.values());
        cacheService.put(CacheList.DEPT_LIST_CACHE.getName(), "all", deptList);
        return deptList;
    }

}
