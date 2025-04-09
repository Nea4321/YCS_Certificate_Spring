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

        for(DeptMap dm : deptMapEntities){

            DeptListDTO parentDTO;
            DeptListChildDTO childDTO;

            if (dm.getFaculty() != null) {
                parentDTO = new DeptListDTO(
                        "faculty",
                        dm.getFaculty().getId(),
                        dm.getFaculty().getFacultyName(),
                        new ArrayList<>()
                );
            } else if (dm.getDepartment() != null) {
                parentDTO = new DeptListDTO(
                        "department",
                        dm.getDepartment().getId(),
                        dm.getDepartment().getDepartmentName(),
                        new ArrayList<>()
                );
            } else {
                parentDTO = new DeptListDTO(
                        "major",
                        dm.getMajor().getId(),
                        dm.getMajor().getMajorName(),
                        new ArrayList<>()
                );
            }

            if (dm.getMajor() != null) {
                childDTO = new DeptListChildDTO(
                        "major",
                        dm.getMajor().getId(),
                        dm.getMajor().getMajorName()
                );
            } else if (dm.getDepartment() != null) {
                childDTO = new DeptListChildDTO(
                        "department",
                        dm.getDepartment().getId(),
                        dm.getDepartment().getDepartmentName()
                );
            } else {
                childDTO = new DeptListChildDTO(
                        "faculty",
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

        List<DeptListDTO> deptList = new ArrayList<>(dtoMap.values());
        cacheService.put(CacheList.DEPT_LIST_CACHE.getName(), "all", deptList);
        return deptList;
    }

}
