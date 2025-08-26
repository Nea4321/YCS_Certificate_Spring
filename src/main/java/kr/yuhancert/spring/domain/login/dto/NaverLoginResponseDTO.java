package kr.yuhancert.spring.domain.login.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverLoginResponseDTO {

    private String resultcode;
    private String message;
    private NaverResponse response; // response 객체 매핑

    @Getter
    @Setter
    public static class NaverResponse {
        private String id;
        private String nickname;
        private String name;
        private String email;
        private String age;
        private String gender;
        private String birthday;
        private String birthyear;
    }
}