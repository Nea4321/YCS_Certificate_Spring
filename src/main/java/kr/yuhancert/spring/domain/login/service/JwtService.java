package kr.yuhancert.spring.domain.login.service;


import io.jsonwebtoken.*;
import kr.yuhancert.spring.domain.login.entity.SocialType;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;


@Service
public class JwtService {
    private final SecretKey jwt_access_key;
    private final SecretKey jwt_refresh_key;

    // 생성자에 바로 키 값 집어넣게 함. (노란색줄 오류 수정 할려고 이렇게 만듬)
    public JwtService(JwtKeyService jwtKeyService) {
        this.jwt_access_key = jwtKeyService.getAccessSecretKey();
        this.jwt_refresh_key = jwtKeyService.getRefreshSecretKey();
    }

    private final static long accessExpireTime = 1000 *  10 ;   // 액세스 토큰 만료시간 : 10분으로 설정할건데 gi테스트 용으로 10초로 설정함.
    private final static long refreshExpireTime = 1000L * 60 * 60 * 24 * 30;   // 리프레시 토큰 만료시간 : 30일
    public long get_accessExp(){return accessExpireTime;}
    public long get_refreshExp(){return refreshExpireTime;}

    // jwt 액세스 토큰 생성
    public String createAccessToken(String userName, String email, SocialType socialType) {
        try {
            Claims claims = Jwts.claims().build();
            long now = System.currentTimeMillis();

            return Jwts.builder()
                    .subject(userName)
                    .issuedAt(new Date(now))
                    .claim("userName", userName)
                    .claim("email", email)
                    .claim("socialType", socialType)
                    .expiration(new Date(now + accessExpireTime))
                    .signWith(jwt_access_key)
                    .compact();

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT 생성 실패: 잘못된 입력값", e);
        } catch (JwtException e) {
            throw new RuntimeException("JWT 생성 중 오류 발생", e);
        }
    }

    // jwt 리프레시 토큰 생성
    public String createRefreshToken(String email) {
        try {
            long now = System.currentTimeMillis();

            return Jwts.builder()
                    .subject(email) // 주체
                    .issuedAt(new Date(now)) // 발행 시간
                    .expiration(new Date(now + refreshExpireTime)) // 만료 시간
                    .signWith(jwt_refresh_key) // 서명
                    .compact();

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("리프레시 토큰 생성 실패: 잘못된 입력값", e);
        } catch (io.jsonwebtoken.JwtException e) {
            throw new RuntimeException("리프레시 토큰 생성 중 오류 발생", e);
        }
    }


    /**
     * jwt 토큰 검증하는 메서드.
     * 토큰을 받아서 서명 검증하고 Claims(페이로드 == 내용 본문) 를 반환 함
     *
     * @param token, key
     * @return Claims(토큰 본문 내용 - 키:값 형태)
     * */
    public Claims parseClaims(String token, SecretKey key) {
        try {
            return Jwts.parser()
                    .verifyWith(key)    // 서명 키
                    .build()
                    .parseSignedClaims(token)   // 서명 검증 + 파싱
                    .getPayload();  // Claims 반환
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("토큰이 만료되었습니다.",e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("토큰 형식이 이상합니다.", e);
        } catch (SecurityException e) {
            throw new RuntimeException("토큰 서명에 실패했습니다.", e);
        } catch (JwtException e) {      // 위 오류 다 아닐 경우 뜨는 오류
            throw new RuntimeException("토큰이 유효하지 않습니다.", e);
        }
    }


    /** 리프레시 코드 검증하는 메서드 */
    public void validateRefreshToken(String refreshToken) {
            parseClaims(refreshToken, jwt_refresh_key);
    }

}
