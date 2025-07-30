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
        SocialLoginService loginService = this.getLoginService(request.getSocialType());

        SocialTokenDTO socialTokenDTO = loginService.getAccessToken(request.getCode());

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


        return socialUserResponseDTO;
    }


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

