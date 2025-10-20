package kr.yuhancert.spring.domain.login.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.yuhancert.spring.infra.login.api.google.GoogleGetToken;
import kr.yuhancert.spring.infra.login.api.google.GoogleGetUser;
import kr.yuhancert.spring.domain.login.dto.SocialLoginResponseDTO;
import kr.yuhancert.spring.domain.login.dto.GoogleRequestAccessTokenDTO;
import kr.yuhancert.spring.domain.login.dto.SocialTokenDTO;
import kr.yuhancert.spring.domain.login.dto.SocialUserResponseDTO;
import kr.yuhancert.spring.domain.login.entity.SocialType;
import kr.yuhancert.spring.global.config.gson.GsonLocalDateTimeAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class GoogleLoginService implements SocialLoginService {
    private final GoogleGetToken googleGetToken;
    private final GoogleGetUser googleGetUser;

    public GoogleLoginService(GoogleGetToken googleGetToken, GoogleGetUser googleGetUser) {
        this.googleGetToken = googleGetToken;
        this.googleGetUser = googleGetUser;
    }
    @Value("${google_client_id}")
    private String googleAppKey;
    @Value("${google_client_password}")
    private String googleAppSecret;
    @Value("${google_redirect_url}")
    private String googleRedirectUri;
    @Value("${google_grant_type}")
    private String googleGrantType;

    @Override
    public SocialType getServiceName() {
        return SocialType.GOOGLE;
    }

    /// 구글 서버 에서 엑세스 토큰을 받아오는 메서드
    @Override
    public SocialTokenDTO getAccessToken(String authorizationCode) {
        ResponseEntity<?> response = googleGetToken.getAccessToken(
                GoogleRequestAccessTokenDTO.builder()
                        .code(authorizationCode)
                        .client_id(googleAppKey)
                        .clientSecret(googleAppSecret)
                        .redirect_uri(googleRedirectUri)
                        .grant_type(googleGrantType)
                        .build()
        );
        log.info("google auth info");
        log.info(response.toString());

        ///  Gson() - 자바용 JSON 처리 라이브러리 -> Json 문자열을 자바 객체로 쉽게 변환 해줌
        ///  .fromJson() - Json 을 객체로 변환(파싱,역직렬화)
        ///  .toJson() - 객체를 Json 으로 변환(직렬화)
        ///  google 에서 받아온 액세스 토큰을(Json 형태) 객체로 변환
        return new Gson()
                .fromJson(
                        response.getBody().toString(),
                        SocialTokenDTO.class
                );
    }


    ///  액세스 토큰으로 유저 정보를 받아오는 메서드
    @Override
    public SocialUserResponseDTO getUserInfo(String accessToken) {
        ResponseEntity<?> response = googleGetUser.getUserInfo(accessToken);

        log.info("google user response");
        log.info(response.toString());

        /// 구글에서 받은 유저정보 json 형태임
        String jsonString = response.getBody().toString();

        /// GsonBuilder -> Gson 객체를 커스텀 함
        ///  .setPretty~~ -> 줄 바꿈 자동
        ///  .register~~ -> LocalDateTime을 JSON으로 직렬화/역직렬화 할 수 있도록 커스텀 어댑터 등록. (Gson은 Localdata를 지원하지 않기 때문)
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()  //Json 출력 보기 이쁘게 해주는거
                .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter()) //LocalDateTime 가능하게
                .create(); //gson 생성 -> 이 gson은 위 설정으로 재탄생함.

        /// 구글에서 받아 온 json 형태의 유저 정보를 GoogleLogin~~ 객체로 변환함
        SocialLoginResponseDTO googleLoginResponse = gson.fromJson(jsonString, SocialLoginResponseDTO.class);

        /// 객체로 변환 한 유저정보를 저장함.
        return SocialUserResponseDTO.builder()
                .name(googleLoginResponse.getName())
                .email(googleLoginResponse.getEmail())
                .socialType(SocialType.GOOGLE)
                .role("normal")
                .build();
    }

}

