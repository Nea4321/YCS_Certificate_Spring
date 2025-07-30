package kr.yuhancert.spring.domain.login.api.google;

import kr.yuhancert.spring.domain.login.dto.GoogleRequestAccessTokenDTO;
import kr.yuhancert.spring.domain.login.dto.SocialTokenDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GoogleGetUser {

    private final WebClient webClient;

    public GoogleGetUser(WebClient.Builder webClientBuilder) {
        this.webClient = WebClient.builder()
                .baseUrl("https://www.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * 액세스 토큰으로 유저 정보를 받아오는 함수
     */
    public ResponseEntity<String> getUserInfo(String accessToken) {
        return this.webClient
                .get()
                .uri("/userinfo/v2/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .toEntity(String.class)
                .block(); // 동기적으로 처리할 경우 block()
    }
}
