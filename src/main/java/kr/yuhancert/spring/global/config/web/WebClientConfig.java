package kr.yuhancert.spring.global.config.web;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
/// 웹 클라이언트 사용하기 위한 설정
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    @Qualifier("githubAccess")
    public WebClient.Builder githubAccess() {
        return WebClient.builder()
                .baseUrl("https://github.com/login/oauth/");
    }

    /**
     * WebClient 설정 메서드
     *  .baseUrl - 기본 요청 URL 설정
     *  → .url("/abc") → https://www.googleapis.com/abc 로 요청됨
     *
     *  .defaultHeader() - 요청 헤더에 본문 데이터 타입(JSON)을 명시
     *  → "내가 보내는 본문은 JSON 형식이야"라고 서버에 알려줌
     */
    @Bean
    @Qualifier("githubAuth")
    public WebClient.Builder githubAuth() {
        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }


    @Bean
    @Qualifier("googleAccess")
    public WebClient.Builder googleAccess() {
        return WebClient.builder()
                .baseUrl("https://oauth2.googleapis.com");
    }

    @Bean
    @Qualifier("googleAuth")
    public WebClient.Builder googleAuth() {
        return WebClient.builder()
                .baseUrl("https://www.googleapis.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }


    @Bean
    @Qualifier("kakaoAccess")
    public WebClient.Builder kakaoAccess() {
        return WebClient.builder()
                .baseUrl("https://kauth.kakao.com");
    }

    @Bean
    @Qualifier("kakaoAuth")
    public WebClient.Builder kakaoAuth() {
        return WebClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Bean
    @Qualifier("naverAccess")
    public WebClient.Builder naverAccess() {
        return WebClient.builder()
                .baseUrl("https://nid.naver.com/oauth2.0");
    }

    @Bean
    @Qualifier("naverAuth")
    public WebClient.Builder naverAuth() {
        return WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }




}