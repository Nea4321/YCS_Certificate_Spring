package kr.yuhancert.spring.domain.login.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.yuhancert.spring.domain.login.dto.*;
import kr.yuhancert.spring.domain.login.entity.User;
import kr.yuhancert.spring.domain.login.entity.SocialType;
import kr.yuhancert.spring.domain.login.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {
    private final List<SocialLoginService> loginServices;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtKeyService jwtKeyService;

    public AuthService(UserRepository userRepository, JwtService jwtService, JwtKeyService jwtKeyService, List<SocialLoginService> loginServices) {
        this.loginServices = loginServices;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.jwtKeyService = jwtKeyService;
    }

    // 소셜 로그인(구글,카카오..) 처리 로직
    public SocialUserResponseDTO doSocialLogin(SocialLoginRequestDTO request) {
        // 소셜 타입을 읽어서 어떤 서비스를 적용할건지 정함.
        SocialLoginService loginService = this.getLoginService(request.getSocialType());
        // 위에서 적용된 서비스를 기반으로 액세스 토큰을 받아옴.
        SocialTokenDTO socialTokenDTO = loginService.getAccessToken(request.getCode());
        // 얻은 액세스 토큰으로 유저 정보를 받아옴.
        SocialUserResponseDTO socialUserResponseDTO = loginService.getUserInfo(socialTokenDTO.getAccess_token());
        log.info("socialUserResponse {} ", socialUserResponseDTO.toString());

        Optional<User> checkUser = userRepository.findByUserEmail(socialUserResponseDTO.getEmail());

        if(checkUser.isPresent()) {
            throw new IllegalStateException("이미 가입된  소셜 사용자입니다.");
        }
        else{
            userRepository.save(
                    User.builder()
                            .userId(socialUserResponseDTO.getId())
                            .userEmail(socialUserResponseDTO.getEmail())
                            .userName(socialUserResponseDTO.getName())
                            .socialType(socialUserResponseDTO.getSocialType())
                            .userRole("normal")
                            .build()
                );
        }

        return socialUserResponseDTO;
    }


    /**
     * 로그인 처리 관련 로직
     *
     * 이메일이 DB에 없거나 비번이 틀리면 에러 메시지를 리턴함.
     * 오류 걸리는게 없으면 jwt 토큰으로 저장 후 넘김.
     * */
    public ResponseEntity<?> doLogin(LoginResponseDTO request, HttpServletResponse response) {

        Optional<User> userOpt = userRepository.findByUserEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("이메일 또는 비밀번호가 틀렸습니다.");
        }
        // 로그인한 유저 정보를 DB에서 불러옴.
        User user = userOpt.get();

        if (user.getSocialType() != SocialType.NORMAL) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("소셜 로그인 계정은 아래 소셜 로그인 버튼을 이용해 주세요.");
        }

        if (user.getUserPassword()==null || !user.getUserPassword().equals(request.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("이메일 또는 비밀번호가 틀렸습니다.");
        }

        // 유저 정보를 DB에서 가져온 후 jwt토큰 으로 저장함.
        Map<String, String> token = Jwt_Token_Create(user.getUserId(),user.getUserName(),user.getUserEmail(),user.getSocialType(),user.getUserRole(),response);
        return ResponseEntity.ok(token);


    }

    // 회원가입 처리 관련 로직
    public ResponseEntity<?> doSingUp(UserResponseDTO userResponseDTO) {
        if (userRepository.findByUserEmail((userResponseDTO.getUserEmail())).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // 409 - 리소스 충돌 (db 중복 데이터)
                    .body("이미 가입된 이메일 입니다.");
        }
       else {
            User user = userRepository.save(
                    User.builder()
                            .userId(userResponseDTO.getUserId())
                            .userEmail(userResponseDTO.getUserEmail())
                            .userName(userResponseDTO.getUserName())
                            .userPassword(userResponseDTO.getUserPassword())
                            .socialType(userResponseDTO.getSocialType())
                            .userRole("normal")
                            .build()
            );
            return ResponseEntity.status(HttpStatus.OK).body("회원 등록 되었습니다.");
        }
    }

    //리프레시 토큰 삭제 ( 그냥 토큰 만료시간 0으로 만드는거임 )
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)                      // true - https 환경만 전송 / false - http도 전달 가능
                .sameSite("Lax")                // 또는 cross-site 필요시 "None"
                .path("/")                      // 모든 경로에 쿠키 전송 가능
                .maxAge(0)  // 초 단위
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("리프레시 토큰 삭제 완료");
    }

    // 회원 삭제 처리 관련 로직
    public void DeleteUser(){}


    // jwt 토큰 생성 관련 로직 - 토큰을 생성하는 서비스 사용, 리프레시 토큰을 쿠키로 변환하는 곳.
    public Map<String, String> Jwt_Token_Create(Long id, String name, String email, SocialType socialType, String role,HttpServletResponse response) {
        // jwt 액세스,리프레시 토큰 생성
        String accessToken = jwtService.createAccessToken(id, name, email, socialType, role);
        String refreshToken = jwtService.createRefreshToken(email);

        // 리프레시 토큰 쿠키로 변환
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)                      // true - https 환경만 전송 / false - http도 전달 가능
                .sameSite("Lax")                // 또는 cross-site 필요시 "None"
                .path("/")                      // 모든 경로에 쿠키 전송 가능
                .maxAge(jwtService.get_refreshExp()/1000)  // 초 단위
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return (Map.of("accessToken", accessToken));
    }


    /**
     * 리프레시 토큰 체크하는 로직
     *
     * 현재 생각하는 문제점
     * 1. 이 로직은 액세스 토큰이 만료될 때 마다 실행되는 로직임(10분)
     * 2. 즉 10분 마다 쿠키를 읽고 DB를 조회함 ( DB 조회하는게 부담이 클 듯 )
     * 3. 여러명이 10분마다 유저 DB를 조회 한 다면 서버쪽 과부화가 생길 문제 가 있음..
     * 4. 물론 졸작이고 사용하는 사람은 거의 없겠지만 교수님이 지적하거나 사용하는 유저가 많아지면...
     * 5. Redis 쿠키를 사용해서 DB 조회 부담을 줄이는게 대응책일듯 (Redis 1도 모름)
     * */
    public ResponseEntity<?> checkRefreshToken(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("refresh_token")) {
                        String refreshToken = cookie.getValue();

                        Claims claims = jwtService.parseClaims(refreshToken, jwtKeyService.getRefreshSecretKey());
                        String email = claims.getSubject();

                        User user = userRepository.findByUserEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                        Long id = user.getUserId();
                        String name = user.getUserName();
                        SocialType socialType = user.getSocialType();
                        String role = user.getUserRole();
                        String accessToken = jwtService.createAccessToken(id, name, email, socialType, role);

                        return ResponseEntity.ok(Map.of("accessToken", accessToken));
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰을 찾기 못했습니다.");
        }
        catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("리프레시 토큰 만료시간이 지났습니다.");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않습니다");
        }
    }



    // 소셜 타입을 이용해 소셜 타입을 지원하는 서비스를 찾음.
    // 소셜 타입이 없거나 이상한게 날라오면 빈 껍데기 서비스로 이동
    private SocialLoginService getLoginService(SocialType socialType) {
        for (SocialLoginService loginService : loginServices) {
            if (socialType.equals(loginService.getServiceName())) {
                log.info("login service name: {}", loginService.getServiceName());
                return loginService;
            }
        }
        return new LoginService();
    }





/** [JWT 토큰 공부한거 적어놓는 주석]
 *
 *  JWT 토큰 - 헤더,페이로드(본문),서명 으로 구성되어 있음
 *  헤더 구성은 자동으로 해준다고함 ( 타입과 알고리즘을 기재함 )
 *  서명 부분에는 SecretKey 가 들어가고 이 SecretKey 는 바이트(바이너리)배열 의 데이터가 들어감
 *  액세스 토큰에는 유저 정보를 페이로드에 담아서 보내고 리프레시 토큰은 페이로드에 아무것도 안 넣어도됨.
 *  리프레시 토큰은 그저 액세스 토큰을 다시 생성하기 위해 확인하는 용도니까
 *
 *  액세스 토큰은 바로 리턴함 ( 토큰 값을 그대로 전달한다는 뜻 )
 *  리프레시 토큰은 httponly cookie로 변환후 리턴함 ( withCredentials: true -> 이 명령어로 프론트에서 쿠키를 자동으로 받을 수 있음 )
 *  근데 쿠키로 변환 할떄 오류가 발생함 (.secure() .sameSite() )
 *
 *  1번째 문제점. 프론트,백엔드 주소(origin)이 다르다 ( 5173, 8080 ) [ origin = 프로토콜(http) + 도메인 + 포트 ]
 *  이유 : 쿠키는 다른 사이트 에서 기존 사이트로 이동이 불가능함 ( ex) 네이버 에서 로그인 한 후 구글 사이트 이동 하면 구글 사이트 에서는 네이버 로그인 정보를 알 수 없는 것. )
 *  해결 : 리액트에 있는 vite_config.ts 에서 프록시 설정을 해놔서 프론트에서 백엔드 요청은 전부 8080에서 요청 한걸로 바뀜.
 *
 *  2번쨰 문제점. 현재 환경은 개발 환경이다 ( https 가 아닌 http를 사용중 )
 *  이유 : .secure(true) -> https 만 사용가능,  .secure(false) -> http 도 사용가능
 *        .sameSite(None) -> 이 쿠키는 모든 요청 허용 -> 해당 옵션은 .secure(true) 가 필수임  -> 현재 개발 환경은 사용 불가
 *        .sameSite(Strict) -> origin 다르면 다 차단 더 엄격 / .sameSite(Lax) -> origin 달라도 Get요청 만 쿠키 요청 허용 덜 엄격
 *        ["Lax" 가 Get 요청만 허용하는 이유 -> 대부분 백엔드 처리에서 Get은 단순 정보 얻기, Post는 중요한 정보 처리 이기 때문에]
 *  해결 : 프록시 설정 해놔서 프론트,백엔드 요청 주소가 같음. -> .secure(false), .sameSite(Lax) 사용 -> Strict 썼다가 오류 생길까바
 *
 *  헷갈렸던거 : 현재 프론트 에서 f12 하면 쿠키 값으로 리프레시 토큰 값이 보임 -> 월래 httponly cookie는 f12에서 보이면 안 됨 ( 서버에서 관리하는 쿠키니까 )
 *  -> 내가 설정했던 .secure(false) 이건 개발환경 이기 때문에 f12 눌러도 쿠키 값이 보임
 *  -> 해당 쿠키 에 있는 리프레시 토큰 값을 js로 사용하려 하면 차단됨 ( .document.cookie() 로 사용하면 차단 된다는 뜻 )
 *  -> 아마 배포 상태 (완성 하고 https 사용할 떄 ) 는 f12 눌러도 쿠키 값에 리프레시 토큰 값은 안 보일거임.
 *
 * */
}

