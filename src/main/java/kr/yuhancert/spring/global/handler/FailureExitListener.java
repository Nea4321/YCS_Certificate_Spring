package kr.yuhancert.spring.global.handler;

import kr.yuhancert.spring.infra.discord.DiscordNotifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.core.NestedExceptionUtils;

public class FailureExitListener implements SpringApplicationRunListener {

    public FailureExitListener(SpringApplication application, String[] args) {
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        Dotenv dotenv = Dotenv.load();
        String webhookUrl = dotenv.get("DISCORD_WEBHOOK"); // 환경변수로 읽기

        if (webhookUrl == null) {
            System.err.println("❌ DISCORD_WEBHOOK 환경변수가 설정되지 않음");
            return;
        }

        Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(exception);
        String message = rootCause != null ? rootCause.getMessage() : null;

        if (message != null && (message.contains("Max client") || message.contains("Unable to determine Dialect without JDBC metadata"))) {

            // Max Client 에러가 발생했을 때 실행할 코드
            System.err.println("🚨 Max Client Exceeded Error Detected! Initiating specific cleanup...");
            DiscordNotifier notifier = new DiscordNotifier(webhookUrl);
            notifier.sendMessage("🚨 **DB 연결 실패 발생!**\n" +
                    "주인님, 빨리 오셔야겠는데요..?");
        }
    }

}