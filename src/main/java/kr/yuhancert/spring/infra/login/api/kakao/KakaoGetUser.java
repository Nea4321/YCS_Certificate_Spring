package kr.yuhancert.spring.infra.login.api.kakao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoGetUser {

    private final WebClient webClient;

    public KakaoGetUser(@Qualifier("kakaoAuth") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public ResponseEntity<String> getUserInfo(String accessToken) {
        return this.webClient
                .get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // 요청 헤더에 Authorization: Bearer accessToken 추가 (OAuth2 인증)
                .retrieve()
                .toEntity(String.class)
                .block(); // 동기적으로 처리할 경우 block()
    }
}
