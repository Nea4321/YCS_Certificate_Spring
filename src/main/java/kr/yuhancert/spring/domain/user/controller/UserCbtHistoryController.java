package kr.yuhancert.spring.domain.user.controller;

import ch.qos.logback.classic.Logger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.yuhancert.spring.domain.user.dto.UserCbtHistoryCertDTO;
import kr.yuhancert.spring.domain.user.dto.UserCbtHistoryDTO;
import kr.yuhancert.spring.domain.user.dto.UserPreviousDTO;
import kr.yuhancert.spring.domain.user.service.UserCbtHistoryService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/cbt")
public class UserCbtHistoryController {
    private final UserCbtHistoryService userCbtHistoryService;
    private final Map<String, String> errorResponse = new HashMap<>();
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public UserCbtHistoryController(UserCbtHistoryService __userCbtHistoryService) {this.userCbtHistoryService = __userCbtHistoryService;}

    @GetMapping
    public ResponseEntity<?> getUserCbtHistory(HttpServletRequest request){
        try {
            List<UserCbtHistoryCertDTO> response = userCbtHistoryService.getCbtHistory(request);
            logger.info(response.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting user cbt list", e);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new java.util.Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body(errorResponse);
        }
    }

    @GetMapping("previous/{previous_id}")
    public ResponseEntity<?> getUserPrevious(@PathVariable("previous_id") Long __previousId, HttpServletRequest request) {
        try {
            UserPreviousDTO userPreviousDTO = userCbtHistoryService.getUserPreviousDTO(__previousId, request);
            logger.info(userPreviousDTO.toString());
            return ResponseEntity.ok(userPreviousDTO);
        } catch (Exception e) {
            logger.error("Error getting user previous", e);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new java.util.Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUserFavorite(@RequestBody @Valid UserCbtHistoryDTO response, HttpServletRequest request){
        try {
            return userCbtHistoryService.addCbtHistory(response,request);
        } catch (Exception e) {
            logger.error("Error adding user favorite", e);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new java.util.Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body(errorResponse);
        }
    }
}
