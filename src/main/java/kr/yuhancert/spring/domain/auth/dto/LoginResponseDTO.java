package kr.yuhancert.spring.domain.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
///  로그인 했을 떄 넘어오는 정보
public class LoginResponseDTO {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
