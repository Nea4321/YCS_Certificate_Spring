package kr.yuhancert.spring.domain.login.service;

import kr.yuhancert.spring.domain.login.dto.SocialTokenDTO;
import kr.yuhancert.spring.domain.login.dto.SocialUserResponseDTO;
import kr.yuhancert.spring.domain.login.type.SocialType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Component

///  소셜 타입이 넘어오지 않을 때 이 서비스를 호출함 -> 기본 이 되는 서비스
public class LoginService implements SocialLoginService {
    @Override
    public SocialType getServiceName() {
        return SocialType.NORMAL;
    }

    @Override
    public SocialTokenDTO getAccessToken(String authorizationCode) {
        return null;
    }

    @Override
    public SocialUserResponseDTO getUserInfo(String accessToken) {
        return null;
    }
}
