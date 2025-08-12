package kr.yuhancert.spring.domain.login.dto;

import jakarta.validation.constraints.NotNull;
import kr.yuhancert.spring.domain.login.entity.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/// 기본 로그인 하면 백엔드로 넘어오는 정보들
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResponseDTO {
    @NotNull
    private String userName;
    @NotNull
    private String userEmail;
    @NotNull
    private String userPassword;
    @NotNull
    private SocialType socialType;
}
