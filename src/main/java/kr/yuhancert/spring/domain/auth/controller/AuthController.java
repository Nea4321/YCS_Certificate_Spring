package kr.yuhancert.spring.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.yuhancert.spring.domain.auth.dto.LoginResponseDTO;
import kr.yuhancert.spring.domain.auth.dto.SocialLoginRequestDTO;
import kr.yuhancert.spring.domain.auth.dto.UserResponseDTO;
import kr.yuhancert.spring.domain.auth.service.AuthService;
import kr.yuhancert.spring.domain.auth.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;

    public AuthController(AuthService authService, EmailService emailService) {this.authService = authService; this.emailService = emailService;}

    /**
     * 소셜 로그인 처리해서 jwt 토큰 넘겨줌
     *
     * doSocialLogin() - 소셜 유저 정보 얻어옴.
     * 얻은 정보로 jwt 토큰 생성
     * 액세스토큰, 리프레시 토큰 전달
     * 리프레시 토큰은 httponly cookie로 전달
     * */
    @PostMapping("/social_login")
    public ResponseEntity<?> doSocialLogin(@RequestBody @Valid SocialLoginRequestDTO request, HttpServletResponse response) {
        try {
                return authService.doSocialLogin(request,response);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** 기본 로그인 처리 */
    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody @Valid LoginResponseDTO request, HttpServletResponse response) {
        return authService.doLogin(request,response);
    }

    /** 회원가입 */
    @PostMapping("/singup")
    public ResponseEntity<?> doSingup(@RequestBody @Valid UserResponseDTO request) {
        return authService.doSingUp(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> checkRefreshToken(HttpServletRequest request) {
        return authService.checkRefreshToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> RefreshTokenDelete(HttpServletResponse request) {
        return authService.logout(request);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateName(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {
        String name = requestBody.get("name");
        return authService.updateName(name, request);
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestParam String email) {
        emailService.sendVerificationCode(email); // 메일 전송
        return ResponseEntity.ok("인증 코드가 발송되었습니다.");
    }

    //이메일 인증 코드 확인
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
        boolean valid = emailService.verifyCode(email, code); // 코드 검증
        if (valid) return ResponseEntity.ok("인증 성공");
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패 또는 만료됨"); // 실패 시 401
        }

}
