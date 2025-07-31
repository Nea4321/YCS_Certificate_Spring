package kr.yuhancert.spring.domain.login.service;

import kr.yuhancert.spring.domain.login.dto.*;
import kr.yuhancert.spring.domain.login.entity.User;
import kr.yuhancert.spring.domain.login.repository.UserRepository;
import kr.yuhancert.spring.domain.login.type.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor  // final 로 선언된 것 들 생성자 자동 으로 등록함.
@Slf4j
public class UserService {
    private final List<SocialLoginService> loginServices;
    private final UserRepository userRepository;
    public SocialUserResponseDTO doSocialLogin(SocialLoginRequestDTO request) {
        // 소셜 타입을 읽어서 어떤 서비스를 적용할건지 정함.
        SocialLoginService loginService = this.getLoginService(request.getSocialType());
        // 위에서 적용된 서비스를 기반으로 액세스 토큰을 받아옴.
        SocialTokenDTO socialTokenDTO = loginService.getAccessToken(request.getCode());
        // 얻은 액세스 토큰으로 유저 정보를 받아옴.
        SocialUserResponseDTO socialUserResponseDTO = loginService.getUserInfo(socialTokenDTO.getAccess_token());
        log.info("socialUserResponse {} ", socialUserResponseDTO.toString());

//        ///  유저 db에 유저 데이터 없으면 채워 넣음.
//        if (userRepository.findByUserId(socialUserResponseDTO.getId()).isEmpty()) {
//            UserResponseDTO userResponseDTO = new UserResponseDTO();
//            User user = userRepository.save(
//                    User.builder()
//                            .userId(userResponseDTO.getUserId())
//                            .socialType(userResponseDTO.getSocialType())
//                            .userEmail(userResponseDTO.getUserEmail())
//                            .userName(userResponseDTO.getUserName())
//                            .build()
//            );
//        }

        // 리턴값으로 유저 정보에 저장될 DTO를 사용해야 하는데 DB 없어서 일단 얻은 그대로를 리턴함
        return socialUserResponseDTO;
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


    public UserResponseDTO getUser(Long id) {
        Optional<User> user = userRepository.findById(id);

        return UserResponseDTO.builder()
                .id(user.get().getId())
                .userId(user.get().getUserId())
                .userEmail(user.get().getUserEmail())
                .userName(user.get().getUserName())
                .build();
    }
}

