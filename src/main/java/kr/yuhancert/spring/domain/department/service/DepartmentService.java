package kr.yuhancert.spring.domain.department.service;

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
    private DepartmentList departmentList;
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
        this.departmentList = new DepartmentList();
    }


    public void checkEntities(){

        if(departmentEntities == null || departmentEntities.isEmpty()){
            departmentEntities = departmentRepository.findAll();
        }

        if (facultyEntities == null || facultyEntities.isEmpty()){
            facultyEntities = facultyRepository.findAll();
        }

        if (majorEntities == null || majorEntities.isEmpty()){
            majorEntities = majorRepository.findAll();
        }

        if (deptMapEntities == null || deptMapEntities.isEmpty()) {
            deptMapEntities = deptMapRepository.findAll();
        }

    }


    @Transactional(readOnly = true)
    public List<DeptListDTO> getDeptList() {

        String CACHE_KEY_DL = "list";
        List<DeptListDTO> cacheDeptList =cacheService.get(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL);
        if (cacheDeptList != null) {
            return cacheDeptList;
        }

        checkEntities();

        List<DeptListDTO> deptList = departmentList.createDeptList(deptMapEntities);

        cacheService.put(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL, deptList);

        return deptList;
    }

}
