package kr.yuhancert.spring.domain.login.api.google;

import kr.yuhancert.spring.domain.login.dto.GoogleRequestAccessTokenDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GoogleGetToken {

    private final WebClient webClient;

    public GoogleGetToken(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://oauth2.googleapis.com").build();
    }

    /**
     * 액세스 토큰을 받아오는 함수
     */
    public ResponseEntity<String> getAccessToken(GoogleRequestAccessTokenDTO requestDTO) {
        return this.webClient
                .post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_JSON)  //요청 본문을 JSON 으로 보냄
                .bodyValue(requestDTO)  //requestDTO 를 JSON 문자열로 바꿈 (직렬화)
                .retrieve()  //응답 받는 비동기 체인 시작점
                .toEntity(String.class)// 응답 전체를 ResponseEntity<String>으로 받음
                .block(); //비동기 에서 동기로 전환 -> 성능 저하 생김- -> 코드는 간단해짐 spring MVC 기반이면 사용 해도 된다고함
    }
}
