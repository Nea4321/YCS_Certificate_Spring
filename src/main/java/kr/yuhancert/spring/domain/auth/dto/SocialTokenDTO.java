package kr.yuhancert.spring.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/// 액세스 토큰 정보
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SocialTokenDTO {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in; /// 액세스 토큰 만료 시간
    private String scope;  /// 권한 범위
    private String refresh_token_expires_in;  /// 리프레시 토큰 만료 시간
}
