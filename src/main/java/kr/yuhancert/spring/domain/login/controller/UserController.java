package kr.yuhancert.spring.domain.login.controller;

import jakarta.validation.Valid;
import kr.yuhancert.spring.domain.login.dto.LoginResponseDTO;
import kr.yuhancert.spring.domain.login.dto.SocialLoginRequestDTO;
import kr.yuhancert.spring.domain.login.dto.SocialUserResponseDTO;
import kr.yuhancert.spring.domain.login.dto.UserResponseDTO;
import kr.yuhancert.spring.domain.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    // 구글 로그인 창 에서 로그인 하게 되면 해당 포스트 매핑을 실행 함. (구글 오어스의 code~(클라이언트 아이디), 소셜타입 을 넘김)
    // 이 포스트 매핑은 프론트 엔드(view) 쪽에다 리소스가 정상적으로 생성 됬음과 동시에 데이터를 넘김.
    public ResponseEntity<SocialUserResponseDTO> doSocialLogin(@RequestBody @Valid SocialLoginRequestDTO request) {

        return ResponseEntity.ok(userService.doSocialLogin(request));
    }

   /*  키를 통해 유저 정보를 가져옴
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                userService.getUser(id)
        );
    }*/
}
