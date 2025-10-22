package kr.yuhancert.spring.infra.login.api.github;

import kr.yuhancert.spring.domain.auth.dto.GithubRequestAccessTokenDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;


@Service  //생성자 자동 생성을 위한 Service
public class GithubGetToken {

    private final WebClient webClient;

    public GithubGetToken(@Qualifier("githubAccess") WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * 액세스 토큰을 받아오는 함수
     * json 문자열로 받음
     */
    public ResponseEntity<String> getAccessToken(GithubRequestAccessTokenDTO requestDTO) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", requestDTO.getClient_id());
        formData.add("client_secret", requestDTO.getClientSecret());
        formData.add("code", requestDTO.getCode());
        formData.add("redirect_uri", requestDTO.getRedirect_uri());

        return this.webClient
                .post()
                .uri("/access_token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED) // JSON 대신 form
                .accept(MediaType.APPLICATION_JSON)               // JSON으로 응답 받기
                .bodyValue(formData)
                .retrieve()
                .toEntity(String.class)
                .block();
    }
}

