package kr.yuhancert.spring.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoRequestAccessTokenDTO {
    private String code;
    private String client_id;
    private String clientSecret;
    private String redirect_uri;
    private String grant_type;
}
