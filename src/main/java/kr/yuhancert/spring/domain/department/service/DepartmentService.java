package kr.yuhancert.spring.domain.department.service;

import kr.yuhancert.spring.domain.department.dto.*;
import kr.yuhancert.spring.domain.department.entity.*;
import kr.yuhancert.spring.domain.department.mapper.DeptListMapper;
import kr.yuhancert.spring.domain.department.mapper.FacultyDepartmentMapper;
import kr.yuhancert.spring.domain.department.mapper.DeptMapDataMapper;
import kr.yuhancert.spring.domain.department.mapper.DeptMapMapper;
import kr.yuhancert.spring.domain.department.repository.*;
import kr.yuhancert.spring.global.cache.service.CacheService;
import kr.yuhancert.spring.global.cache.util.CacheList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
    private DeptMapData deptMapDataEntities;
    private List<DeptCert> deptCertEntities;
    private final DeptMapMapper deptMapMapper;
    private final DeptListMapper deptListMapper;
    private final FacultyDepartmentMapper facultyDepartmentMapper;
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
                             FacultyDepartmentMapper __facultyDepartmentMapper,
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
        this.facultyDepartmentMapper = __facultyDepartmentMapper;
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

        if (deptCertEntities == null || deptCertEntities.isEmpty()) {
            deptCertEntities = deptCertRepository.findAll();
        }

    }


    @Transactional(readOnly = true)
    public List<DeptListDTO> getDeptList() {

        String CACHE_KEY_DL = "list";
        List<DeptListDTO> cacheDeptList =cacheService.getList(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL, DeptListDTO.class);
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
        List<DeptMapDTO> cacheDeptMap = cacheService.getList(CacheList.DEPT_MAP_CACHE.getName(), CACHE_KEY_DM, DeptMapDTO.class);
        if (cacheDeptMap != null) {
            return cacheDeptMap;
        }

        checkDeptEntities();

        List<DeptMapDTO> deptMapDTO = deptMapMapper.toDeptMapDTOList(deptMapEntities);

        cacheService.put(CacheList.DEPT_MAP_CACHE.getName(), CACHE_KEY_DM, deptMapDTO);

        return deptMapDTO;

    }

    public DeptMapDataDTO getDeptMapData(Long __id) {

        String CACHE_KEY_DMD = "data";
        DeptMapDataDTO cacheDeptMapDataDTO = cacheService.get(CacheList.DEPT_DATA_CACHE.getName(), CACHE_KEY_DMD, DeptMapDataDTO.class);
        if (cacheDeptMapDataDTO != null) {
            return cacheDeptMapDataDTO;
        }

        checkDeptEntities();

        deptMapDataEntities = deptMapDataRepository.findById(__id).orElse(null);

        DeptMapDataDTO deptMapDataDTO = deptMapDataMapper.toDeptMapDataDTO(deptMapDataEntities, deptCertEntities);

        cacheService.put(CacheList.DEPT_DATA_CACHE.getName(), CACHE_KEY_DMD, deptMapDataDTO);

        return deptMapDataDTO;

    }

    public List<Department> getDepartments() {return departmentRepository.findAll();}

    public List<FacultyandDepartmentDTO> getFacultyDepartment() {
        List<DeptMap> deptMaps = deptMapRepository.findAll(); // faculty, department fetch join 필요
        return facultyDepartmentMapper.toHierarchy(deptMaps);
    }

    /** 학부,학고,전공 이름 수정 */
    @Transactional
    public ResponseEntity<?> updateFacultyDepartmentMajor(DeptEditRequestDTO deptEditRequestDTO) {
        String type = deptEditRequestDTO.getType();
        Long id = deptEditRequestDTO.getId();
        String newName = deptEditRequestDTO.getValue();

        String CACHE_KEY_DL = "list";
        cacheService.evict(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL);

        if(type.equals("faculty")){
            facultyRepository.findById(id).ifPresent(faculty -> {
                faculty.setFacultyName(newName);
                facultyRepository.save(faculty);
            });
        }
        else if(type.equals("department")){
            departmentRepository.findById(id).ifPresent(faculty -> {
                faculty.setDepartmentName(newName);
                departmentRepository.save(faculty);
            });
        }
        else if(type.equals("major")){
            majorRepository.findById(id).ifPresent(faculty -> {
                faculty.setMajorName(newName);
                majorRepository.save(faculty);
            });
        }

        return ResponseEntity .status(HttpStatus.CREATED) .body("수정 완료");
    }


    /** 추가한 학부,학과,전공 연결해서 dept_map 에 저장*/
    @Transactional
    public void setDeptDate(FacultyCreateRequestDTO dto) {

        String CACHE_KEY_DL = "list";
        cacheService.evict(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL);
        // 학부 조회
        Faculty facultyEntity = null;
        if (dto.getFacultyName() != null && !dto.getFacultyName().isBlank()) {
            facultyEntity = facultyRepository.findByFacultyName(dto.getFacultyName());
        }

        // 학과DB 리스트 가져옴.
        Map<String, Department> departmentMap = departmentRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Department::getDepartmentName, d -> d));
        // 전공DB 리스트 가져옴.
        Map<String, Major> majorMap = majorRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Major::getMajorName, m -> m));

        // 추가하는 정보에 학과가 있을 때
        if (dto.getDepartment() != null && !dto.getDepartment().isEmpty()) {
            for (DepartmentCreateDTO depDTO : dto.getDepartment()) {

                // 학과 엔티티 가져오기 (Map에서 바로)
                Department departmentEntity = null;
                if (depDTO.getName() != null && !depDTO.getName().isBlank()) {
                    departmentEntity = departmentMap.get(depDTO.getName());
                }

                // 전공 엔티티 리스트 가져오기
                List<Major> majorEntities = Optional.ofNullable(depDTO.getMajors())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(majorMap::get) // Map에서 바로 조회
                        .filter(Objects::nonNull)
                        .toList();

                //  dept_map 저장 (학과,학부,전공 or 학과,학부 만약에 학부가 null이면 학과,전공만 저장)
                if (!majorEntities.isEmpty()) {
                    for (Major majorEntity : majorEntities) {
                        DeptMap deptMap = new DeptMap();
                        deptMap.setFaculty(facultyEntity);
                        deptMap.setDepartment(departmentEntity);
                        deptMap.setMajor(majorEntity);
                        deptMapRepository.save(deptMap);
                    }
                    // (학부,학과 만 저장  만약에 학부가 null이면 학과만 저장)
                } else if (departmentEntity != null) {
                    DeptMap deptMap = new DeptMap();
                    deptMap.setFaculty(facultyEntity);
                    deptMap.setDepartment(departmentEntity);
                    deptMap.setMajor(null);
                    deptMapRepository.save(deptMap);
                }
            }
        }
        //  학부만 있는 경우
        else if (facultyEntity != null) {
            DeptMap deptMap = new DeptMap();
            deptMap.setFaculty(facultyEntity);
            deptMap.setDepartment(null);
            deptMap.setMajor(null);
            deptMapRepository.save(deptMap);
        }


    }




    /**
     * 추가된 학부,학과,전공 db에 저장
     * */
    @Transactional
    public ResponseEntity<?> setFacultyDepartmentMajor(FacultyCreateRequestDTO facultyCreateRequestDTO) {
        logger.info("facultyCreateRequestDTO: " + facultyCreateRequestDTO);
        String facultyName_create = facultyCreateRequestDTO.getFacultyName();

        // 학부 저장
        if (facultyName_create != null && !facultyName_create.isBlank()) {
            if (!facultyRepository.existsByFacultyName(facultyName_create)) {
                Faculty faculty = new Faculty();
                faculty.setFacultyName(facultyName_create);
                facultyRepository.save(faculty);
            }
        }

// 학과/전공 저장
        if (facultyCreateRequestDTO.getDepartment() != null) {
            for (DepartmentCreateDTO depDTO : facultyCreateRequestDTO.getDepartment()) {

                // 학과 저장
                if (depDTO.getName() != null && !depDTO.getName().isBlank()) {
                    if (!departmentRepository.existsByDepartmentName(depDTO.getName())) {
                        Department department = new Department();
                        department.setDepartmentName(depDTO.getName());
                        departmentRepository.save(department);
                        departmentRepository.flush();
                    }
                    // 이미 존재하면 그냥 넘어감
                }

                // 전공 저장
                if (depDTO.getMajors() != null) {
                    for (String majorName : depDTO.getMajors()) {
                        if (majorName != null && !majorName.isBlank() &&
                                !majorRepository.existsByMajorName(majorName)) {
                            Major major = new Major();
                            major.setMajorName(majorName);
                            majorRepository.save(major);
                        }
                    }
                }
            }
        }
        setDeptDate(facultyCreateRequestDTO);
        return ResponseEntity .status(HttpStatus.CREATED) .body("학부/학과/전공 저장 완료");
    }

    }