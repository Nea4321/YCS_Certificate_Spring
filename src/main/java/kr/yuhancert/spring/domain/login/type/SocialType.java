package kr.yuhancert.spring.domain.login.type;

import com.fasterxml.jackson.annotation.JsonCreator;

//생각중인 소셜 로그인 목록들
public enum SocialType {
    KAKAO,
    NAVER,
    GOOGLE,
    GITHUB,
    NORMAL;

    @JsonCreator
    public static SocialType from(String value) {
        return SocialType.valueOf(value.toUpperCase());
    }
}

