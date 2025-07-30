package kr.yuhancert.spring.domain.login.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/// CORS 정책 설정 -> 백엔드, 프론트엔드 포트가 달라서 요청이 차단됨.
@Configuration("loginWebConfig")
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        //요청 보내는 모든 경로 URL( ex) api/user ) 허용
        corsRegistry.addMapping("/**")
                //해당 도메인 요청 허용
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE")
                //프론트 요청이 들어오면 백엔드는 확인차 물어보는 작업을 실시함.
                //아래 3600초(1시간) 동안 똑같은 도메인으로 요청이 들어오면 백엔드에서 물어보는 작업 미실시 -> 성능향상
                .maxAge(3600);
    }
}
