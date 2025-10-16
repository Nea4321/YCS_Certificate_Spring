package kr.yuhancert.spring.domain.department.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import kr.yuhancert.spring.domain.department.dto.*;
import kr.yuhancert.spring.domain.department.entity.Department;
import kr.yuhancert.spring.domain.department.mapper.DeptMapMapper;
import kr.yuhancert.spring.domain.department.repository.DeptMapRepository;
import kr.yuhancert.spring.domain.department.service.DepartmentService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dept")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());
    private final DeptMapRepository deptMapRepository;
    private final DeptMapMapper deptMapMapper;

    private Map<String, String> errorResponse = new HashMap<>();

    public DepartmentController(DepartmentService __departmentService, DeptMapRepository deptMapRepository, DeptMapMapper deptMapMapper) {
        this.departmentService = __departmentService;
        this.deptMapRepository = deptMapRepository;
        this.deptMapMapper = deptMapMapper;
    }


    @GetMapping("/list")
    public ResponseEntity<?> getDeptList() {
        try {
            List<DeptListDTO> deptList = departmentService.getDeptList();
            return ResponseEntity.ok(deptList);
        } catch (Exception e) {
            logger.error("Error getting department list", e);
            errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/map")
    public ResponseEntity<?> getDeptMap() {
        try {
            List<DeptMapDTO> deptMap = departmentService.getDeptMap();
            return ResponseEntity.ok(deptMap);
        } catch (Exception e) {
            logger.error("Error getting department mapping", e);
            errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/data/{id}")
    public ResponseEntity<?> getDeptData(@PathVariable("id") Long id) {
        try{
            DeptMapDataDTO deptMapDataDTO = departmentService.getDeptMapData(id);
            return ResponseEntity.ok(deptMapDataDTO);
        } catch (Exception e) {
            logger.error("Error getting department data", e);
            errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/department")
    public ResponseEntity<?> getDepartmentData() {
        try{
            List<Department> departmentsDTO = departmentService.getDepartments();
            return ResponseEntity.ok(departmentsDTO);
        } catch (Exception e) {
            logger.error("Error getting department data", e);
            errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @GetMapping("/list/edit")
    public ResponseEntity<?> getDeptListEdit() {
        try {
            List<FacultyandDepartmentDTO> list = departmentService.getFacultyDepartment();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            logger.error("Error getting department list", e);
            errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<?> changeFacultyDepartMajor(@RequestBody DeptEditRequestDTO request) {
        try {return departmentService.updateFacultyDepartmentMajor(request);}
        catch (Exception e) { return ResponseEntity.badRequest().body("수정 실패: " + e.getMessage());}
    }



    @PostMapping("/delete")
    public ResponseEntity<?> deleteFaDeMa(@RequestBody @Valid DeptEditRequestDTO request) {
        return departmentService.deleteFacultyDepartmentMajor(request);
    }

    @PostMapping("/create")
    public ResponseEntity<?> setFacultyDepartMajor(@RequestBody @Valid FacultyCreateRequestDTO request) {
        return departmentService.setFacultyDepartmentMajor(request);
    }

}
