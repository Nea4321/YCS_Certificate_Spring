package kr.yuhancert.spring.domain.auth.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.yuhancert.spring.domain.auth.dto.*;
import kr.yuhancert.spring.domain.auth.entity.SocialType;
import kr.yuhancert.spring.global.config.gson.GsonLocalDateTimeAdapter;
import kr.yuhancert.spring.infra.login.api.kakao.KakaoGetToken;
import kr.yuhancert.spring.infra.login.api.kakao.KakaoGetUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class KakaoLoginService implements SocialLoginService{
    private final KakaoGetToken kakaoGetToken;
    private final KakaoGetUser kakaoGetUser;

    public KakaoLoginService (KakaoGetToken kakaoGetToken, KakaoGetUser kakaoGetUser) {
        this.kakaoGetToken = kakaoGetToken;
        this.kakaoGetUser = kakaoGetUser;
    }

    @Value("${kakao_api_key}")
    private String kakaoAppKey;
    @Value("${kakao_client_password}")
    private String kakaoAppSecret;
    @Value("${kakao_redirect_url}")
    private String kakakoRedirectUrl;

    @Override
    public SocialType getServiceName() {
        return SocialType.KAKAO;
    }

    /// 구글 서버 에서 엑세스 토큰을 받아오는 메서드
    @Override
    public SocialTokenDTO getAccessToken(String authorizationCode) {
        ResponseEntity<?> response = kakaoGetToken.getAccessToken(
                KakaoRequestAccessTokenDTO.builder()
                        .grant_type("authorization_code")
                        .code(authorizationCode)
                        .client_id(kakaoAppKey)
                        .clientSecret(kakaoAppSecret)
                        .redirect_uri(kakakoRedirectUrl)
                        .build()
        );
        log.info("kakao auth info");
        log.info(response.toString());

        return new Gson()
                .fromJson(
                        response.getBody().toString(),
                        SocialTokenDTO.class
                );
    }

    @Override
    public SocialUserResponseDTO getUserInfo(String accessToken) {
        ResponseEntity<?> response = kakaoGetUser.getUserInfo(accessToken);

        log.info("kakao user response");
        log.info(response.toString());


        String jsonString = response.getBody().toString();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()  //Json 출력 보기 이쁘게 해주는거
                .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter()) //LocalDateTime 가능하게
                .create(); //gson 생성 -> 이 gson은 위 설정으로 재탄생함.

        KakaoResponseUserDTO kakaoResponse = gson.fromJson(jsonString, KakaoResponseUserDTO.class);

        String nickname = kakaoResponse.getProperties().getNickname();
        String email = kakaoResponse.getKakao_account().getEmail();


        return SocialUserResponseDTO.builder()
                .name(nickname)
                .email(email)
                .socialType(SocialType.KAKAO)
                .build();
    }

}