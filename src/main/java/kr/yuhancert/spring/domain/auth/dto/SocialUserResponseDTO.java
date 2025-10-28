package kr.yuhancert.spring.domain.auth.dto;

import kr.yuhancert.spring.domain.auth.entity.SocialType;
import lombok.*;

/// 로그인 한 유저 정보 DTO
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SocialUserResponseDTO {
    private Long id;
    private String email;
    private String name;
    private SocialType socialType;
    private String role;
}
