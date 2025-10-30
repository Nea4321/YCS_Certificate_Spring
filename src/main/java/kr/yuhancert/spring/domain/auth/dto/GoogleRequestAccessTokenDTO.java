package kr.yuhancert.spring.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/// 액세스 토큰을 얻기 위해 구글에다가 보내야 하는 정보
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleRequestAccessTokenDTO {
    private String code;
    private String client_id;
    private String clientSecret;
    private String redirect_uri;
    private String grant_type;
}
