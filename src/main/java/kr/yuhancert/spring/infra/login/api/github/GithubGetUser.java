package kr.yuhancert.spring.infra.login.api.github;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GithubGetUser {

    private final WebClient webClient;

    public GithubGetUser(@Qualifier("githubAuth") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    /**
     * 액세스 토큰으로 유저 정보를 받아오는 함수
     * json 문자열로 받음
     */
    public ResponseEntity<String> getUserInfo(String accessToken) {
        return this.webClient
                .get()
                .uri("/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // 요청 헤더에 Authorization: Bearer accessToken 추가 (OAuth2 인증)\
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block(); // 동기적으로 처리할 경우 block()
    }

    public ResponseEntity<String> getEmailInfo(String accessToken) {
        return this.webClient
                .get()
                .uri("/user/emails")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // 요청 헤더에 Authorization: Bearer accessToken 추가 (OAuth2 인증)\
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class)
                .block(); // 동기적으로 처리할 경우 block()
    }
}
