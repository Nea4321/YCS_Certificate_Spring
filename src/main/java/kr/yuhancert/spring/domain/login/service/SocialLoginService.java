package kr.yuhancert.spring.domain.login.service;

import kr.yuhancert.spring.domain.login.dto.SocialTokenDTO;
import kr.yuhancert.spring.domain.login.dto.SocialUserResponseDTO;
import kr.yuhancert.spring.domain.login.type.SocialType;
import org.springframework.stereotype.Service;

@Service
public interface SocialLoginService {
    SocialType getServiceName();
    SocialTokenDTO getAccessToken(String authorizationCode);
    SocialUserResponseDTO getUserInfo(String accessToken);
}
