package kr.yuhancert.spring.domain.login.controller;

import ch.qos.logback.classic.Logger;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.certificate.dto.ScheduleDTO;
import kr.yuhancert.spring.domain.login.dto.UserDataDTO;
import kr.yuhancert.spring.domain.login.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private Map<String, String> errorResponse = new HashMap<>();
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/data")
    public ResponseEntity<?> getUserData(HttpServletRequest request) {
        try {
            UserDataDTO userDataDTO = userService.getUserData(request);
            return ResponseEntity.ok(userDataDTO);
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

    @GetMapping("/schedule")
    public ResponseEntity<?> getUserSchedule(HttpServletRequest request) {
        try {
            List<ScheduleDTO> scheduleDTOS = userService.getUserSchedule(request);
            return ResponseEntity.ok(scheduleDTOS);
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

}
