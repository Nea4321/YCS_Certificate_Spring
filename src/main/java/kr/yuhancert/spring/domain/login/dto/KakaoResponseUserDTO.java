package kr.yuhancert.spring.domain.login.dto;

import lombok.Data;

@Data
public class KakaoResponseUserDTO {
    private Long id;
    private String connected_at;
    private Properties properties;
    private KakaoAccount kakao_account;

    @Data
    public static class Properties {
        private String nickname;
        private String profile_image;
        private String thumbnail_image;
    }

    @Data
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Data
        public static class Profile {
            private String nickname;
            private String profile_image_url;
            private String thumbnail_image_url;
        }
    }
}