package kr.yuhancert.spring.domain.user.controller;

import ch.qos.logback.classic.Logger;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.user.dto.UserIncorrectDTO;
import kr.yuhancert.spring.domain.user.service.UserCbtService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/cbt")
public class UserCbtController {
    private final UserCbtService userCbtService;
    private final Map<String, String> errorResponse = new HashMap<>();
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public UserCbtController(UserCbtService __userCbtService) {
        this.userCbtService = __userCbtService;
    }



    @GetMapping("/incorrect/{cert_id}")
    public ResponseEntity<?> getUserIncorrect(@PathVariable("cert_id") Long __certId, HttpServletRequest request) {
        try {
            UserIncorrectDTO userIncorrectDTO = userCbtService.getUserIncorrect(__certId, request);
            logger.info(userIncorrectDTO.toString());
            return ResponseEntity.ok(userIncorrectDTO);
        } catch (Exception e) {
            logger.error("Error getting user incorrect", e);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new java.util.Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}
