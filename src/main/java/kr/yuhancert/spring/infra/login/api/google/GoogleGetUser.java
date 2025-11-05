package kr.yuhancert.spring.infra.login.api.google;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.springframework.http.HttpStatus.*;

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
        try {
            return this.webClient.get()
                    .uri("/oauth2/v3/userinfo")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    // 4xx 에러 (클라이언트 쪽 문제)
                    // 예: 400 Bad Request, 401 Unauthorized, 403 Forbidden 등
                    .onStatus(HttpStatusCode::is4xxClientError, res ->
                            res.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("Client Error (4xx): " + body))
                    )

                    // 5xx 에러 (서버 쪽 문제)
                    // 예: 500 Internal Server Error, 502 Bad Gateway 등
                    .onStatus(HttpStatusCode::is5xxServerError, res ->
                            res.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("Server Error (5xx): " + body))
                    )
                    .toEntity(String.class)
                    .block();

        }
        // 서버가 4xx, 5xx 상태 코드를 반환할 때 발생
        // 예: 401 Unauthorized (토큰 만료), 403 Forbidden (스코프 부족)
        catch (WebClientResponseException e) {
            System.err.println(" Google API Response Error: " + e.getStatusCode());
            System.err.println("응답 본문: " + e.getResponseBodyAsString());
            HttpStatusCode statusCode = e.getStatusCode();
            if (statusCode.equals(UNAUTHORIZED)) {
                System.err.println(" 원인: 만료되었거나 잘못된 Access Token");
            } else if (statusCode.equals(FORBIDDEN)) {
                System.err.println(" 원인: userinfo 스코프가 부족함 (scope 추가 필요)");
            } else if (statusCode.equals(BAD_REQUEST)) {
                System.err.println(" 원인: 잘못된 요청 URI 또는 잘못된 Authorization 헤더");
            } else {
                System.err.println(" 기타 클라이언트/서버 오류");
            }
            throw e;
        }
        // 요청이 아예 서버까지 도달하지 못했을 때 (네트워크 문제)
        // 예: DNS 오류, HTTPS 인증 실패, 연결 끊김
        catch (WebClientRequestException e) {
            System.err.println(" [Connection Error] 구글 서버에 연결 실패: " + e.getMessage());
            System.err.println("원인: 네트워크 문제, baseUrl 오타, SSL 인증 오류 가능");
            throw e;
        }
        // accessToken이 null이거나 webClientBuilder 주입이 안된 경우
        catch (NullPointerException e) {
            System.err.println(" [NullPointerException] accessToken 또는 webClient가 null입니다.");
            System.err.println("원인: 의존성 주입 실패 또는 accessToken이 null로 전달됨");
            throw e;
        }
        catch (Exception e) {
            System.err.println(" [Unexpected Error] 알 수 없는 예외 발생: " + e.getMessage());
            throw e;
        }
    }
}
