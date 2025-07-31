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
    /**
     * 프론트에다 유저 정보를 전해주는 곳.
     * json 형태로 전달 (프론트에서 받는 정보를 인터페이스로 표기함)
     * */
    public ResponseEntity<SocialUserResponseDTO> doSocialLogin(@RequestBody @Valid SocialLoginRequestDTO request) {

        return ResponseEntity.ok(userService.doSocialLogin(request));
    }

    // 참고했던 github 프로젝트의 코드는 위에있는 postmapping 에서 프론트에다 유저 아이디를 전달해주고
    // 프론트에서 받은 아이디는 다시 아래에 getmapping을 해서 프론트에다 유저 정보를 전달해 줌.
    // 이렇게 만든 이유를 찾아볼려하니 DB 구현이 안돼있어서 확인이 어려움. 그래서 일단 내 생각대로 고침.

   /*  키를 통해 유저 정보를 가져옴
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                userService.getUser(id)
        );
    }*/
}
