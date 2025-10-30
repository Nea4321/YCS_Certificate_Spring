package kr.yuhancert.spring.infra.login.api.naver;

import kr.yuhancert.spring.domain.auth.dto.NaverRequestAccessTokenDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NaverGetToken {
    private final WebClient webClient;

    public NaverGetToken(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://nid.naver.com/oauth2.0").build();
    }

    public ResponseEntity<String> getAccessToken(NaverRequestAccessTokenDTO requestDTO) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", requestDTO.getGrant_type());
        formData.add("client_id", requestDTO.getClient_id());
        formData.add("client_secret", requestDTO.getClientSecret());
        formData.add("code", requestDTO.getCode());
        formData.add("state", requestDTO.getState());

        return this.webClient
                .post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .toEntity(String.class)
                .block();
    }

}
