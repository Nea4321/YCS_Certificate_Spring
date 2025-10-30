package kr.yuhancert.spring.domain.auth.service;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * SecretKey 변환 하는 곳.
 *
 * 환경 변수에 저장된 token_key 들은  "openssl rand -base64 32" 명령어로 생성한 base64 인코딩된 32바이트 길이의 랜덤 데이터임.
 * 근데 jwt 토큰 서명에는 SecretKey 타입의 키가 들어가야 됨.  (SecretKey = 바이트 배열(바이너리) 형태의 키 데이터)
 * 그래서 환경 변수에 저장된 key 들을 base64로 디코딩을 하고, 바이트 배열로 바꾼 뒤, secretkey에 저장.
 *
 * 참고로 서명 키는 HS256 알고리즘을 사용함
 * HS256 -> 256비트 길이로 이루어진 키로 서명,검증 함. -> 가장 단순한? 방법
 * */

@Service
public class JwtKeyService {
    @Value("${jwt_access_token_key}")
    private String access_key;
    @Value("${jwt_refresh_token_key}")
    private String refresh_key;

    private SecretKey access_secretKey;
    private SecretKey refresh_secretKey;

    // @PostConstruct -> 의존성 주입이 완료되면 자동으로 메서드를 호출해줌.
    // 즉 JwtKeyService 에 @Autowired 하면 자동으로 아래 메서드 실행됨.
    @PostConstruct
    public void init() {
        byte[] decoded_access_Key = Base64.getDecoder().decode(access_key);
        byte[] decoded_refresh_Key = Base64.getDecoder().decode(refresh_key);
        access_secretKey = Keys.hmacShaKeyFor(decoded_access_Key);
        refresh_secretKey = Keys.hmacShaKeyFor(decoded_refresh_Key);
    }

    public SecretKey getAccessSecretKey() {return access_secretKey;}
    public SecretKey getRefreshSecretKey() {return refresh_secretKey;}
}
