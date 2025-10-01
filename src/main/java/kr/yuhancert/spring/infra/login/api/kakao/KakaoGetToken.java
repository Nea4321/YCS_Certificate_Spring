package kr.yuhancert.spring.infra.login.api.kakao;

import kr.yuhancert.spring.domain.login.dto.KakaoRequestAccessTokenDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoGetToken {
    private final WebClient webClient;

    public KakaoGetToken(@Qualifier("kakaoAccess") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public ResponseEntity<String> getAccessToken(KakaoRequestAccessTokenDTO requestDTO) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", requestDTO.getGrant_type());
        formData.add("client_id", requestDTO.getClient_id());
        formData.add("client_secret", requestDTO.getClientSecret());
        formData.add("redirect_uri", requestDTO.getRedirect_uri());
        formData.add("code", requestDTO.getCode());

        return this.webClient
                .post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .toEntity(String.class)
                .block();
    }

}