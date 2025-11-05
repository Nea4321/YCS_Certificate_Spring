package kr.yuhancert.spring.infra.login.api.google;

import kr.yuhancert.spring.domain.auth.dto.GoogleRequestAccessTokenDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Service  //생성자 자동 생성을 위한 Service
public class GoogleGetToken {

    private final WebClient webClient;

    public GoogleGetToken(@Qualifier("googleAccess") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * 액세스 토큰을 받아오는 함수
     * json 문자열로 받음
     */
    public ResponseEntity<String> getAccessToken(GoogleRequestAccessTokenDTO requestDTO) {
        try{
        return this.webClient
                .post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_JSON)  //요청 본문을 JSON 으로 보냄
                .bodyValue(requestDTO)  //requestDTO 를 JSON 문자열로 바꿈 (직렬화, 보내는 값)
                .retrieve()  //응답 받는 비동기 체인 시작점
                .onStatus(HttpStatusCode::is4xxClientError, res -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Client Error: " + body)))
                .onStatus(HttpStatusCode::is5xxServerError, res -> res.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Server Error: " + body)))
                .toEntity(String.class)// 응답 전체를 ResponseEntity<String>으로 받음
                .block(); //비동기 에서 동기로 전환 -> 성능 저하 생김- -> 코드는 간단해짐 spring MVC 기반이면 사용 해도 된다고함
    }
        //네트워크 문제, URL 오타, DNS 실패, SSL 인증서 오류
        catch (WebClientResponseException e) {
            System.err.println("Google API Error: " + e.getResponseBodyAsString());
            throw e;
            // HTTP 응답 코드가 4xx 또는 5xx
        } catch (WebClientRequestException e) {
            System.err.println("Connection Error(구글 서버가 에러 반환): " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            throw e;
        }
}
}

