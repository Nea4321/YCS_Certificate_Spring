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


            deptMapEntities = deptMapRepository.findAll();


        if (deptCertEntities == null || deptCertEntities.isEmpty()) {
            deptCertEntities = deptCertRepository.findAll();
        }

    }


    @Transactional(readOnly = false)
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

        DeptMapDataDTO cacheDeptMapDataDTO = cacheService.get(CacheList.DEPT_DATA_CACHE.getName(), __id, DeptMapDataDTO.class);
        if (cacheDeptMapDataDTO != null) {
            return cacheDeptMapDataDTO;
        }

        checkDeptEntities();

        deptMapDataEntities = deptMapDataRepository.findById(__id).orElse(null);

        DeptMapDataDTO deptMapDataDTO = deptMapDataMapper.toDeptMapDataDTO(deptMapDataEntities, deptCertEntities);

        cacheService.put(CacheList.DEPT_DATA_CACHE.getName(), __id, deptMapDataDTO);

        return deptMapDataDTO;

    }

    public List<Department> getDepartments() {return departmentRepository.findAll();}

    /** 학부 or 학과 or 전공 삭제
     * @param deptEditRequestDTO Long id, String type, String value
     *
     * @implNote
     * 학부 지우면 연결된 학과-전공 전부 삭제 - 자동으로 dept_map 컬럼도 삭제 됨.
     * 학과 지우면 연결된 전공 전부 삭제 - 자동으로 dept_map 컬럼도 삭제 됨.
     *
     *
     * @문제점
     * 1. A학과-B전공 이 연결된 상태에서 B전공을 지운다면..
     * 1-1. dept_map DB 는 참조하는 값이 사라지면 해당 row가 전부 사라짐
     * 1-2. 근데 A학과는 department DB에 남아 있음
     * 1-3. 즉 A학과는 DB에만 있고 사용이 안 되는 상태.
     *
     * @문제점_해결  테이블 제약조건을 바꾸면 됨. (참조 값 사라져도 dept_map 데이터는 유지되게)
     *
     * @문제점_해결하면_생기는_문제
     * 1. 참조 값 사라지면 dept_map 테이블도 삭제 하는 로직을 추가해야됨. -> 이건 repository 써서 간단하게 해결가능.
     * 2. 삭제 한 후 학부,학과,전공을 읽어서 다시 연결하는 로직 구현 해야함.
     * 2-1. setDeptDate 라고 학부,학과,전공 연결하는 서비스는 있음
     * 2-2 근데 이 서비스는 추가 할 때 연결하려고 만든 서비스라 매개변수가 문자열 배열의 문자열 배열 형태임. (string,string[])[]
     * 2-3. 즉 삭제 하기위해 필요한 매개변수(deptEditRequestDTO) 랑 학부,학과,전공을 연결하기 위해 필요한 매개변수(FacultyCreateRequestDTO) 2개가 필요함
     * 2-4. 프론트 쪽에는 추가할 정보를 입력해야 FacultyCreateRequestDTO 가 백엔드에 전달이 되는 구조인데 전공 삭제 버튼만 누를 때 자동으로 채워줘야하는 로직을 새로 만들어야댐
     * 2-5 결론 요약 : 해결 할 수 있을거 같은데 로직이 너무 복잡해 질거 같아서 보류중
     * 2-6. 갑자기 든 생각 : 그냥 major 제약조건만 풀면 정상화 될지도?
     * */
    public ResponseEntity<?> deleteFacultyDepartmentMajor(DeptEditRequestDTO deptEditRequestDTO){
        String type = deptEditRequestDTO.getType();
        Long id = deptEditRequestDTO.getId();

        String CACHE_KEY_DL = "list";
        cacheService.evict(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL);
        cacheService.clear(CacheList.DEPT_LIST_CACHE.getName());

        try {
            if ("faculty".equals(type)) {
                // 1️⃣ faculty 연결된 dept_map 모두 조회
                List<DeptMap> relatedMaps = deptMapRepository.findByFaculty(facultyRepository.findById(id));

                // 2️⃣ 연결된 department/major ID 수집
                Set<Long> departmentIds = new HashSet<>();
                Set<Long> majorIds = new HashSet<>();
                for (DeptMap dept : relatedMaps) {
                    if (dept.getDepartment() != null)
                        departmentIds.add(dept.getDepartment().getId());
                    if (dept.getMajor() != null)
                        majorIds.add(dept.getMajor().getId());
                }

                // 4️⃣ 연결된 major, department, faculty 순으로 삭제
                if (!majorIds.isEmpty()) majorRepository.deleteAllById(majorIds);
                if (!departmentIds.isEmpty()) departmentRepository.deleteAllById(departmentIds);
                facultyRepository.deleteById(id);
            }

            else if ("department".equals(type)) {
                // 1️⃣ department 연결된 dept_map 조회
                List<DeptMap> relatedMaps = deptMapRepository.findByDepartment(departmentRepository.findById(id));

                // 2️⃣ 연결된 major ID 수집
                Set<Long> majorIds = new HashSet<>();
                for (DeptMap map : relatedMaps) {
                    if (map.getMajor() != null)
                        majorIds.add(map.getMajor().getId());
                }

                // 4️⃣ 연결된 major, department 삭제
                if (!majorIds.isEmpty()) majorRepository.deleteAllById(majorIds);
                departmentRepository.deleteById(id);
            }

            else if ("major".equals(type)) {
                majorRepository.deleteById(id);
            }

            else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("잘못된 type 값입니다: " + type);
            }

            return ResponseEntity.ok("삭제 완료");

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("삭제 중 오류 발생: " + e.getMessage());
        }
    }

    /** 학부-학과 연결 리스트 가져오기 */
    public List<FacultyandDepartmentDTO> getFacultyDepartment() {
        List<DeptMap> deptMaps = deptMapRepository.findAll();
        return facultyDepartmentMapper.toHierarchy(deptMaps);
    }

    /** 학부,학과,전공 이름 수정 */
    @Transactional
    public ResponseEntity<?> updateFacultyDepartmentMajor(DeptEditRequestDTO deptEditRequestDTO) {
        String type = deptEditRequestDTO.getType();
        Long id = deptEditRequestDTO.getId();
        String newName = deptEditRequestDTO.getValue();

        String CACHE_KEY_DL = "list";
        cacheService.evict(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL);
        cacheService.clear(CacheList.DEPT_LIST_CACHE.getName());

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

        String CACHE_KEY_DL = "list";
        cacheService.evict(CacheList.DEPT_LIST_CACHE.getName(), CACHE_KEY_DL);
        cacheService.clear(CacheList.DEPT_LIST_CACHE.getName());

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