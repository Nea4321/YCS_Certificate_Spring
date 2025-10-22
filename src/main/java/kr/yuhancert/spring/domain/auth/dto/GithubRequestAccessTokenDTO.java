package kr.yuhancert.spring.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/// 액세스 토큰을 얻기 위해 깃허브에 보내는 정보
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GithubRequestAccessTokenDTO {
    private String code;
    private String client_id;
    private String clientSecret;
    private String redirect_uri;
    private String scope;
}
