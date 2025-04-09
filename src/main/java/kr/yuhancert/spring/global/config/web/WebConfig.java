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
        registry.addMapping("/**")
                .allowedOrigins(frontendUrls.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(maxAge); // 브라우저가 preflight 요청 결과를 캐시하는 시간(초)
    }
}