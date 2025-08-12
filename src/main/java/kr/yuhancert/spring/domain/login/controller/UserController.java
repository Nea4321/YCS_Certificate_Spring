package kr.yuhancert.spring.domain.login.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.yuhancert.spring.domain.login.dto.LoginResponseDTO;
import kr.yuhancert.spring.domain.login.dto.SocialLoginRequestDTO;
import kr.yuhancert.spring.domain.login.dto.SocialUserResponseDTO;
import kr.yuhancert.spring.domain.login.dto.UserResponseDTO;
import kr.yuhancert.spring.domain.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

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
        SocialUserResponseDTO user = userService.doSocialLogin(request);

        return ResponseEntity.ok(
                userService.Jwt_Token_Create( user.getName(), user.getEmail(),user.getSocialType(), response)
        );
    }

    /** 기본 로그인 처리 */
    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody @Valid LoginResponseDTO request, HttpServletResponse response) {
        return userService.doLogin(request,response);
    }

    /** 회원가입 */
    @PostMapping("/singup")
    public ResponseEntity<?> doSingup(@RequestBody @Valid UserResponseDTO request) {
        return userService.doSingUp(request);
    }


}
