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
        try {
            ResponseEntity<?> response = kakaoGetToken.getAccessToken(
                    KakaoRequestAccessTokenDTO.builder()
                            .grant_type("authorization_code")
                            .code(authorizationCode)
                            .client_id(kakaoAppKey)
                            .clientSecret(kakaoAppSecret)
                            .redirect_uri(kakakoRedirectUrl)
                            .build()
            );

            log.debug("Kakao auth response: {}", response);

            // HTTP 상태코드 확인
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("카카오 인증 서버 응답 실패 (code=" + response.getStatusCodeValue() + ")");
            }

            Object body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("카카오 인증 응답 Body가 비어있습니다.");
            }

            return new Gson().fromJson(body.toString(), SocialTokenDTO.class);

        } catch (Exception e) {
            log.error("카카오 액세스 토큰 요청 중 오류 발생", e);
            throw new RuntimeException("카카오 액세스 토큰 발급 실패: " + e.getMessage());
        }
    }

    @Override
    public SocialUserResponseDTO getUserInfo(String accessToken) {
        try {
            ResponseEntity<?> response = kakaoGetUser.getUserInfo(accessToken);

            log.debug("Kakao user response: {}", response);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("카카오 사용자 정보 요청 실패 (code=" + response + ")");
            }

            Object body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("카카오 사용자 정보 응답 Body가 비어있습니다.");
            }

            String jsonString = body.toString();

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
                    .create();

            KakaoResponseUserDTO kakaoResponse = gson.fromJson(jsonString, KakaoResponseUserDTO.class);

            if (kakaoResponse == null || kakaoResponse.getKakao_account() == null) {
                throw new IllegalStateException("카카오 사용자 정보 파싱 실패: 응답 구조가 예상과 다릅니다.");
            }

            String nickname = kakaoResponse.getProperties() != null
                    ? kakaoResponse.getProperties().getNickname()
                    : null;

            String email = kakaoResponse.getKakao_account().getEmail();

            if (email == null || email.isBlank()) {
                throw new IllegalStateException("카카오 계정에 이메일 정보가 없습니다. 이메일 제공 동의가 필요합니다.");
            }

            return SocialUserResponseDTO.builder()
                    .name(nickname != null ? nickname : "카카오사용자")
                    .email(email)
                    .socialType(SocialType.KAKAO)
                    .role("normal")
                    .build();

        } catch (Exception e) {
            log.error("카카오 사용자 정보 요청 중 오류 발생", e);
            throw new RuntimeException("카카오 사용자 정보 요청 실패: " + e.getMessage());
        }
    }

}