package kr.yuhancert.spring.domain.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/// 네이버 액세스 토큰 얻기 위해 필요한 정보
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NaverRequestAccessTokenDTO {
    private String code;
    private String client_id;
    private String clientSecret;
    private String grant_type;
    private String state;
}
