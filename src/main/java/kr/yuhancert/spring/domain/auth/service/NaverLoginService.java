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
        try {
            ResponseEntity<?> response = naverGetToken.getAccessToken(
                    NaverRequestAccessTokenDTO.builder()
                            .code(authorizationCode)
                            .client_id(naverAppKey)
                            .clientSecret(naverAppSecret)
                            .grant_type(naverGrantType)
                            .state("state")
                            .build()
            );

            if (response.getStatusCode().isError() || response.getBody() == null) {
                throw new IllegalStateException("네이버 액세스 토큰 요청 실패: " + response.getStatusCode());
            }

            log.info("naver access info: {}", response);

            return new Gson().fromJson(
                    response.getBody().toString(),
                    SocialTokenDTO.class
            );

        } catch (Exception e) {
            log.error("네이버 토큰 요청 중 오류 발생", e);
            throw new RuntimeException("네이버 로그인 중 오류가 발생했습니다. (" + e.getMessage() + ")");
        }
    }

    @Override
    public SocialUserResponseDTO getUserInfo(String accessToken) {
        try {
            ResponseEntity<?> response = naverGetUser.getUserInfo(accessToken);

            if (response.getStatusCode().isError() || response.getBody() == null) {
                throw new IllegalStateException("네이버 사용자 정보 요청 실패: " + response.getStatusCode());
            }

            log.info("naver user response: {}", response);

            String jsonString = response.getBody().toString();

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
                    .create();

            NaveResponseUserDTO naverLoginResponse = gson.fromJson(jsonString, NaveResponseUserDTO.class);

            if (naverLoginResponse == null || naverLoginResponse.getResponse() == null) {
                throw new IllegalStateException("네이버 사용자 정보 파싱 실패");
            }

            NaveResponseUserDTO.NaverResponse res = naverLoginResponse.getResponse();

            if (res.getEmail() == null || res.getName() == null) {
                throw new IllegalStateException("네이버 사용자 정보에 이메일 또는 이름이 없습니다.");
            }

            return SocialUserResponseDTO.builder()
                    .name(res.getName())
                    .email(res.getEmail())
                    .socialType(SocialType.NAVER)
                    .role("normal")
                    .build();

        } catch (Exception e) {
            log.error("네이버 사용자 정보 요청 중 오류 발생", e);
            throw new RuntimeException("네이버 사용자 정보 조회 중 오류가 발생했습니다. (" + e.getMessage() + ")");
        }
    }
}