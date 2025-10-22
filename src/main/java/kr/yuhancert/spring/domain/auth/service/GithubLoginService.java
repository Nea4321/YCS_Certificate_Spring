package kr.yuhancert.spring.domain.auth.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.yuhancert.spring.domain.auth.dto.*;
import kr.yuhancert.spring.infra.login.api.github.GithubGetToken;
import kr.yuhancert.spring.domain.auth.entity.SocialType;
import kr.yuhancert.spring.global.config.gson.GsonLocalDateTimeAdapter;
import kr.yuhancert.spring.infra.login.api.github.GithubGetUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Service
public class GithubLoginService implements SocialLoginService {
    private final GithubGetToken githubGetToken;
    private final GithubGetUser githubGetUser;

    public GithubLoginService(GithubGetToken githubGetToken, GithubGetUser githubGetUser) {
        this.githubGetToken = githubGetToken;
        this.githubGetUser = githubGetUser;
    }

    @Value("${github_client_id}")
    private String githubAppKey;
    @Value("${github_client_password}")
    private String githubAppSecret;
    @Value("${github_redirect_url}")
    private String githubRedirectUri;
    @Value("${github_scope}")
    private String githubScope;

    @Override
    public SocialType getServiceName() {
        return SocialType.GITHUB;
    }

    @Override
    public SocialTokenDTO getAccessToken(String authorizationCode) {
        ResponseEntity<?> response = githubGetToken.getAccessToken(
                GithubRequestAccessTokenDTO.builder()
                        .code(authorizationCode)
                        .client_id(githubAppKey)
                        .clientSecret(githubAppSecret)
                        .redirect_uri(githubRedirectUri)
                        .scope(githubScope)
                        .build()
        );
        log.info("github auth info");
        log.info(response.toString());

        return new Gson()
                .fromJson(
                        response.getBody().toString(),
                        SocialTokenDTO.class
                );
    }


    ///  액세스 토큰으로 유저 정보를 받아오는 메서드
    @Override
    public SocialUserResponseDTO getUserInfo(String accessToken) {
        ResponseEntity<?> response = githubGetUser.getUserInfo(accessToken);

        log.info("github user response");
        log.info(response.toString());

        String jsonString = response.getBody().toString();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter())
                .create();

        SocialLoginResponseDTO githubLoginResponse = gson.fromJson(jsonString, SocialLoginResponseDTO.class);

        String email = githubLoginResponse.getEmail();

        if (email == null) {
            ResponseEntity<String> emailsResponse = githubGetUser.getEmailInfo(accessToken);
            log.info("github email response");
            log.info(emailsResponse.toString());

            GithubResponseEmailDTO[] emails = gson.fromJson(emailsResponse.getBody(), GithubResponseEmailDTO[].class);

            email = Arrays.stream(Objects.requireNonNull(emails))
                    .filter(GithubResponseEmailDTO::isPrimary)
                    .map(GithubResponseEmailDTO::getEmail)
                    .findFirst()
                    .orElse(null);
        }

        return SocialUserResponseDTO.builder()
                .name(githubLoginResponse.getLogin())
                .email(email)
                .socialType(SocialType.GITHUB)
                .build();
    }

}

