package kr.yuhancert.spring.domain.department.service;

import kr.yuhancert.spring.domain.department.dto.DeptListDTO;
import kr.yuhancert.spring.domain.department.dto.DeptMapDTO;
import kr.yuhancert.spring.domain.department.dto.DeptMapDataDTO;
import kr.yuhancert.spring.domain.department.entity.*;
import kr.yuhancert.spring.domain.department.mapper.DeptListMapper;
import kr.yuhancert.spring.domain.department.mapper.DeptMapDataMapper;
import kr.yuhancert.spring.domain.department.mapper.DeptMapMapper;
import kr.yuhancert.spring.domain.department.repository.*;
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
    private final DeptMapDataRepository deptMapDataRepository;
    private final DeptCertRepository deptCertRepository;
    private List<Department> departmentEntities;
    private List<Faculty> facultyEntities;
    private List<Major> majorEntities;
    private List<DeptMap> deptMapEntities;
    private List<DeptMapData> deptMapDataEntities;
    private List<DeptCert> deptCertEntities;
    private final DeptMapMapper deptMapMapper;
    private final DeptListMapper deptListMapper;
    private final DeptMapDataMapper deptMapDataMapper;
    Logger logger = LoggerFactory.getLogger(DepartmentService.class);


    public DepartmentService(CacheService __cacheService,
                             DepartmentRepository __departmentRepository,
                             FacultyRepository __facultyRepository,
                             MajorRepository __majorRepository,
                             DeptMapRepository __deptMapRepository,
                             DeptMapDataRepository __deptMapDataRepository,
                             DeptCertRepository __deptCertRepository,
                             DeptMapMapper __deptMapMapper,
                             DeptListMapper __deptListMapper,
                             DeptMapDataMapper __deptMapDataMapper) {

        this.cacheService = __cacheService;
        this.departmentRepository = __departmentRepository;
        this.facultyRepository = __facultyRepository;
        this.majorRepository = __majorRepository;
        this.deptMapRepository = __deptMapRepository;
        this.deptMapDataRepository = __deptMapDataRepository;
        this.deptCertRepository = __deptCertRepository;
        this.deptMapMapper = __deptMapMapper;
        this.deptListMapper = __deptListMapper;
        this.deptMapDataMapper = __deptMapDataMapper;
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
            deptMapDataEntities = deptMapDataRepository.findAll();
        }

        if (deptCertEntities == null || deptCertEntities.isEmpty()) {
            deptCertEntities = deptCertRepository.findAll();
        }

    }


    @Transactional(readOnly = true)
    public List<DeptListDTO> getDeptList() {

        String CACHE_KEY_DL = "list";
        List<DeptListDTO> cacheDeptList =cacheService.get(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL);
        if (cacheDeptList != null) {
            return cacheDeptList;
        }

        checkDeptEntities();

        List<DeptListDTO> deptList = deptListMapper.toDeptListDTOList(deptMapEntities);

        cacheService.put(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL, deptList);

        return deptList;
    }

    public List<DeptMapDTO> getDeptMap() {

        String CACHE_KEY_DM = "map";
        List<DeptMapDTO> cacheDeptMap = cacheService.get(CacheList.DEPT_MAP_CACHE.getName(), CACHE_KEY_DM);
        if (cacheDeptMap != null) {
            return cacheDeptMap;
        }

        checkDeptEntities();

        List<DeptMapDTO> deptMapDTO = deptMapMapper.toDeptMapDTOList(deptMapEntities);

        cacheService.put(CacheList.DEPT_MAP_CACHE.getName(), CACHE_KEY_DM, deptMapDTO);

        return deptMapDTO;

    }

    public List<DeptMapDataDTO> getDeptMapData() {

        String CACHE_KEY_DMD = "data";
        List<DeptMapDataDTO> cacheDeptMapDataDTO = cacheService.get(CacheList.DEPT_DATA_CACHE.getName(), CACHE_KEY_DMD);
        if (cacheDeptMapDataDTO != null) {
            return cacheDeptMapDataDTO;
        }

        checkDeptEntities();

        List<DeptMapDataDTO> deptMapDataDTO = deptMapDataMapper.toDeptMapDataDTOList(deptMapDataEntities, deptCertEntities);

        cacheService.put(CacheList.DEPT_DATA_CACHE.getName(), CACHE_KEY_DMD, deptMapDataDTO);

        return deptMapDataDTO;

    }

}