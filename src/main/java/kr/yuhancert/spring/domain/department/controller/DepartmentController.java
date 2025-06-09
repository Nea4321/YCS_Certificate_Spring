package kr.yuhancert.spring.domain.department.controller;

import ch.qos.logback.classic.Logger;
import kr.yuhancert.spring.domain.department.dto.DeptListDTO;
import kr.yuhancert.spring.domain.department.dto.DeptMapDTO;
import kr.yuhancert.spring.domain.department.entity.DeptMap;
import kr.yuhancert.spring.domain.department.entity.DeptMapData;
import kr.yuhancert.spring.domain.department.service.DepartmentService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dept")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    private Map<String, String> errorResponse = new HashMap<>();

    public DepartmentController(DepartmentService __departmentService) {
        this.departmentService = __departmentService;
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
            DeptMapData deptMapData = departmentService.getDeptMapData(id);
            return ResponseEntity.ok(deptMapData);
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

}
