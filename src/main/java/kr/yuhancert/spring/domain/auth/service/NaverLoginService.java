package kr.yuhancert.spring.domain.auth.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.yuhancert.spring.domain.auth.dto.*;
import kr.yuhancert.spring.domain.auth.entity.SocialType;
import kr.yuhancert.spring.global.config.gson.GsonLocalDateTimeAdapter;
import kr.yuhancert.spring.infra.login.api.naver.NaverGetToken;
import kr.yuhancert.spring.infra.login.api.naver.NaverGetUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class NaverLoginService implements SocialLoginService{
    private final NaverGetToken naverGetToken;
    private final NaverGetUser naverGetUser;

    public NaverLoginService (NaverGetToken naverGetToken, NaverGetUser naverGetUser) {
        this.naverGetToken = naverGetToken;
        this.naverGetUser = naverGetUser;
    }

    @Value("${naver_client_id}")
    private String naverAppKey;
    @Value("${naver_client_password}")
    private String naverAppSecret;
    @Value("${naver_grant_type}")
    private String naverGrantType;

    @Override
    public SocialType getServiceName() {
        return SocialType.NAVER;
    }

    /// 구글 서버 에서 엑세스 토큰을 받아오는 메서드
    @Override
    public SocialTokenDTO getAccessToken(String authorizationCode) {
        ResponseEntity<?> response = naverGetToken.getAccessToken(
                NaverRequestAccessTokenDTO.builder()
                        .code(authorizationCode)
                        .client_id(naverAppKey)
                        .clientSecret(naverAppSecret)
                        .grant_type(naverGrantType)
                        .state("state")
                        .build()
        );
        log.info("naver access info");
        log.info(response.toString());

        return new Gson()
                .fromJson(
                        response.getBody().toString(),
                        SocialTokenDTO.class
                );
    }

    @Override
    public SocialUserResponseDTO getUserInfo(String accessToken) {
        ResponseEntity<?> response = naverGetUser.getUserInfo(accessToken);

        log.info("naver user response");
        log.info(response.toString());


        String jsonString = response.getBody().toString();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()  //Json 출력 보기 이쁘게 해주는거
                .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter()) //LocalDateTime 가능하게
                .create(); //gson 생성 -> 이 gson은 위 설정으로 재탄생함.

        NaveResponseUserDTO naverLoginResponse = gson.fromJson(jsonString, NaveResponseUserDTO.class);

        NaveResponseUserDTO.NaverResponse res = naverLoginResponse.getResponse();

        return SocialUserResponseDTO.builder()
                .name(res.getName())
                .email(res.getEmail())
                .socialType(SocialType.NAVER)
                .role("normal")
                .build();
    }

}