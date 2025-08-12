package kr.yuhancert.spring.domain.login.dto;

import jakarta.validation.constraints.NotNull;
import kr.yuhancert.spring.domain.login.entity.SocialType;
import lombok.Getter;
import lombok.Setter;

///소셜 로그인 하기위한 정보 ( ex) google, mfdg$0f3... )
@Getter
@Setter
public class SocialLoginRequestDTO {
    @NotNull
    private SocialType SocialType;
    @NotNull
    private String code;
}

