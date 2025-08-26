package kr.yuhancert.spring.domain.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/// 소셜 에서 가져오는 유저 정보
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginResponseDTO {
    private String name;
    private String login;   //github 이색이 왜 name 안쓰는거임????
    private String email;
}
