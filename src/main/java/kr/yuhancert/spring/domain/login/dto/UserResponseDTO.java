package kr.yuhancert.spring.domain.login.dto;

import kr.yuhancert.spring.domain.login.type.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/// 프론트엔드 로 보낼 유저 DTO 일단 생각중..
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResponseDTO {
    private Long id;
    private String userId;
    private String userName;
    private String userEmail;
    private SocialType socialType;
}
