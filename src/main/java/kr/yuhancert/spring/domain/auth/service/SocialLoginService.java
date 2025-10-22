package kr.yuhancert.spring.domain.auth.service;

import kr.yuhancert.spring.domain.auth.dto.SocialTokenDTO;
import kr.yuhancert.spring.domain.auth.dto.SocialUserResponseDTO;
import kr.yuhancert.spring.domain.auth.entity.SocialType;
import org.springframework.stereotype.Service;

@Service
public interface SocialLoginService {
    SocialType getServiceName();
    SocialTokenDTO getAccessToken(String authorizationCode);
    SocialUserResponseDTO getUserInfo(String accessToken);
}
