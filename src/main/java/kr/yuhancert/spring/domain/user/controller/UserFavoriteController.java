package kr.yuhancert.spring.domain.user.controller;

import ch.qos.logback.classic.Logger;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.user.dto.UserFavoriteDTO;
import kr.yuhancert.spring.domain.user.service.UserFavoriteService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/favorite")
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;
    private Map<String, String> errorResponse = new HashMap<>();
    private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

    public UserFavoriteController(UserFavoriteService __userFavoriteService) {
        this.userFavoriteService = __userFavoriteService;
    }

    @GetMapping
    public ResponseEntity<?> getUserFavorite(HttpServletRequest request){
        try {
            List<UserFavoriteDTO> userFavoriteDTO = userFavoriteService.getUserFavorite(request);
            return ResponseEntity.ok(userFavoriteDTO);
        } catch (Exception e) {
            logger.error("Error getting user favorite list", e);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new java.util.Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body(errorResponse);
        }
    }

    @GetMapping("/{type}/{id}")
    public ResponseEntity<?> getUserFavoriteById(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("id") Long id){
        try {
            Boolean isFavorite = userFavoriteService.isFavorite(request, type, id);
            return ResponseEntity.ok(isFavorite);
        } catch (Exception e) {
            logger.error("Error getting user favorite by id", e);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new java.util.Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

    @PutMapping("/{type}/{id}")
    public ResponseEntity<?> addUserFavorite(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("id") Long id){
        try {
            userFavoriteService.addUserFavorite(request, type, id);
            return ResponseEntity.ok().build();
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

    @DeleteMapping("/{type}/{id}")
    public ResponseEntity<?> deleteUserFavorite(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("id") Long id){
        try {
            userFavoriteService.deleteUserFavorite(request, type, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting user favorite", e);
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", new java.util.Date().toString());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }

}
