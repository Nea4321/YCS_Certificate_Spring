package kr.yuhancert.spring.domain.login.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/// 구글에서 가져오는 유저 정보
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GooGleLoginResponseDTO {
    private String name;
    private String email;
}
