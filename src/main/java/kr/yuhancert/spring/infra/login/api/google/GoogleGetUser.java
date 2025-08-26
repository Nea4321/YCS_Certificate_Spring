package kr.yuhancert.spring.infra.login.api.google;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GoogleGetUser {

    private final WebClient webClient;

       public GoogleGetUser(@Qualifier("googleAuth") WebClient.Builder webClientBuilder) {
           this.webClient = webClientBuilder.build();
       }

    /**
     * 액세스 토큰으로 유저 정보를 받아오는 함수
     * json 문자열로 받음
     */
    public ResponseEntity<String> getUserInfo(String accessToken) {
        return this.webClient
                .get()
                .uri("/userinfo/v2/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // 요청 헤더에 Authorization: Bearer accessToken 추가 (OAuth2 인증)
                .retrieve()
                .toEntity(String.class)
                .block(); // 동기적으로 처리할 경우 block()
    }
}
