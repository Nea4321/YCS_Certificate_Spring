package kr.yuhancert.spring.global.config.web;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

}

// 테스트 환경 설정
@Configuration
@Profile("test")
class TestWebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(TestWebConfig.class);

    @Value("${TEST_FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    @Value("${TEST_ALLOWED_METHODS:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @PostConstruct
    public void init() {
        logger.info("Test WebConfig initialized with frontend URL: {}", frontendUrl);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Applying CORS configuration for TEST environment");
        registry.addMapping("/**")
                .allowedOrigins(frontendUrl.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

@Configuration
@Profile("development")
class DevelopWebConfig implements WebMvcConfigurer{

    private static final Logger logger = LoggerFactory.getLogger(DevelopWebConfig.class);

    @Value("${TEST_FRONTEND_URL:http://localhost:5173}")
    private String frontendUrl;

    @Value("${TEST_ALLOWED_METHODS:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @PostConstruct
    public void init() {
        logger.info("Test WebConfig initialized with frontend URL: {}", frontendUrl);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Applying CORS configuration for TEST environment");
        registry.addMapping("/**")
                .allowedOrigins(frontendUrl.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders("*")
                .allowCredentials(true);
    }

}

// 운영 환경 설정
@Configuration
@Profile("production")
class ProductionWebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(ProductionWebConfig.class);

    @Value("${PROD_FRONTEND_URLS}")
    private String frontendUrls;

    @Value("${PROD_ALLOWED_METHODS:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${PROD_MAX_AGE:3600}")
    private long maxAge;

    @PostConstruct
    public void init() {
        logger.info("Production WebConfig initialized with frontend URLs: {}", frontendUrls);
        if (frontendUrls == null || frontendUrls.isEmpty()) {
            logger.warn("No production frontend URLs configured! This may cause CORS issues.");
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Applying CORS configuration for PRODUCTION environment");
        //요청 보내는 모든 경로 URL( ex) api/user ) 허용
        registry.addMapping("/**")
                .allowedOrigins(frontendUrls.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders("*")
                .allowCredentials(true)
                //프론트 요청이 들어오면 백엔드는 확인차 물어보는 작업을 실시함.
                //아래 3600초(1시간) 동안 똑같은 도메인으로 요청이 들어오면 백엔드에서 물어보는 작업 미실시 -> 성능향상
                .maxAge(maxAge); // 브라우저가 preflight 요청 결과를 캐시하는 시간(초)
    }
}