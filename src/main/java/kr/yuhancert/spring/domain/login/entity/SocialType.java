package kr.yuhancert.spring.domain.login.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

//생각중인 소셜 로그인 목록들
public enum SocialType {
    KAKAO,
    NAVER,
    GOOGLE,
    GITHUB,
    NORMAL;

    // 프론트에서 socialtype을 보내면 'google' 소문자로 요청이 옴
    // 백엔드는 대문자로만 처리해서 오류가 생겼음
    // 아래 함수를 통해서 소문자로 들어오면 대문자로 알아서 처리해줌.
    @JsonCreator
    public static SocialType from(String value) {
        return SocialType.valueOf(value.toUpperCase());
    }
}

