package kr.yuhancert.spring.infra.discord;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class DiscordNotifier {

    private final String webhookUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public DiscordNotifier(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public void sendMessage(String content) {
        Map<String, Object> body = Map.of(
                "content", content,
                "username", "춘식이S"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(webhookUrl, request, String.class);
            System.out.println("✅ 디스코드 메시지 전송 완료");
        } catch (Exception e) {
            System.err.println("❌ 디스코드 전송 실패: " + e.getMessage());
        }
    }
}
