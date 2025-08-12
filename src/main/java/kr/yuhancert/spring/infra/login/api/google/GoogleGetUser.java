package kr.yuhancert.spring.infra.login.api.google;

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

        /**
         * WebClient 설정 메서드
         *  .baseUrl - 기본 요청 URL 설정
         *  → .url("/abc") → https://www.googleapis.com/abc 로 요청됨
         *
         *  .defaultHeader() - 요청 헤더에 본문 데이터 타입(JSON)을 명시
         *  → "내가 보내는 본문은 JSON 형식이야"라고 서버에 알려줌
         */
        this.webClient = WebClient.builder()
                .baseUrl("https://www.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
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
