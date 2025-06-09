package kr.yuhancert.spring.domain.department.service;

import kr.yuhancert.spring.domain.department.dto.DeptListDTO;
import kr.yuhancert.spring.domain.department.entity.*;
import kr.yuhancert.spring.domain.department.repository.*;
import kr.yuhancert.spring.global.cache.service.CacheService;
import kr.yuhancert.spring.global.cache.util.CacheList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DepartmentService {

    private final CacheService cacheService;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final MajorRepository majorRepository;
    private final DeptMapRepository deptMapRepository;
    private final DeptMapDataRepository deptMapDataRepository;
    private List<Department> departmentEntities;
    private List<Faculty> facultyEntities;
    private List<Major> majorEntities;
    private List<DeptMap> deptMapEntities;
    private Map<Long, DeptMapData> deptMapDataEntities;
    private DepartmentList departmentList;
    Logger logger = LoggerFactory.getLogger(DepartmentService.class);
    private String CACHE_KEY_DM = "map";
    private String CACHE_KEY_DL = "list";


    public DepartmentService(CacheService __cacheService,
                             DepartmentRepository __departmentRepository,
                             FacultyRepository __facultyRepository,
                             MajorRepository __majorRepository,
                             DeptMapRepository __deptMapRepository,
                             DeptMapDataRepository __deptMapDataRepository) {

        this.cacheService = __cacheService;
        this.departmentRepository = __departmentRepository;
        this.facultyRepository = __facultyRepository;
        this.majorRepository = __majorRepository;
        this.deptMapRepository = __deptMapRepository;
        this.deptMapDataRepository = __deptMapDataRepository;
        this.departmentList = new DepartmentList();
    }


    private void checkDeptEntities(){

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

        if (deptMapDataEntities == null || deptMapDataEntities.isEmpty()) {
            deptMapDataEntities = deptMapDataRepository.findAll().stream()
                .collect(Collectors.toMap(DeptMapData::getId, Function.identity()));
        }

    }


    @Transactional(readOnly = true)
    public List<DeptListDTO> getDeptList() {

        List<DeptListDTO> cacheDeptList =cacheService.get(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL);
        if (cacheDeptList != null) {
            return cacheDeptList;
        }

        checkDeptEntities();

        List<DeptListDTO> deptList = departmentList.createDeptList(deptMapEntities);

        cacheService.put(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL, deptList);

        return deptList;
    }

    public List<DeptMap> getDeptMap() {

        List<DeptMap> cacheDeptMap = cacheService.get(CacheList.DEPT_MAP_CACHE.getName(), CACHE_KEY_DM);
        if (cacheDeptMap != null) {
            return cacheDeptMap;
        }

        checkDeptEntities();

        cacheService.put(CacheList.DEPT_MAP_CACHE.getName(), CACHE_KEY_DM, this.deptMapEntities);

        return this.deptMapEntities;

    }

    public DeptMapData getDeptMapData(Long __id) {
        
        DeptMapData cacheDeptMapData = cacheService.get(CacheList.DEPT_DATA_CACHE.getName(), __id);
        if (cacheDeptMapData != null) {
            return cacheDeptMapData;
        }

        checkDeptEntities();

        cacheService.put(CacheList.DEPT_DATA_CACHE.getName(), deptMapDataEntities.get(__id), __id);

        return deptMapDataEntities.get(__id);

    }

}