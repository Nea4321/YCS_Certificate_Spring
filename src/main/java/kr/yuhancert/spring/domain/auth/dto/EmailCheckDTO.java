package kr.yuhancert.spring.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailCheckDTO {
        private String code;       // 인증 코드
        private long expiryTime;   // 만료 시간 (ms 단위)
}
