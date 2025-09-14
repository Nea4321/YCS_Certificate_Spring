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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toEntity(String.class)
                .block();
    }
}
