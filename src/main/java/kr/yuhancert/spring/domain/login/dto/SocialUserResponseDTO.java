package kr.yuhancert.spring.domain.login.dto;

import lombok.*;

/// 로그인 한 유저 정보 DTO
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class SocialUserResponseDTO {
    private String id;
    private String email;
    private String name;
    private String gender;
}
