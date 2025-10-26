package kr.yuhancert.spring.global.handler;

import kr.yuhancert.spring.infra.discord.DiscordNotifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import io.github.cdimascio.dotenv.Dotenv;

public class FailureExitListener implements SpringApplicationRunListener {

    public FailureExitListener(SpringApplication application, String[] args) {
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        System.err.println("❌ Spring Boot Application Failed to Start or Run! ❌");
        Dotenv dotenv = Dotenv.load();
        String webhookUrl = dotenv.get("DISCORD_WEBHOOK"); // 환경변수로 읽기
        if (webhookUrl == null) {
            System.err.println("❌ DISCORD_WEBHOOK 환경변수가 설정되지 않음");
            return;
        }
        DiscordNotifier notifier = new DiscordNotifier(webhookUrl);
        notifier.sendMessage("🚨 **스프링 부팅 실패 발생!**\n" +
                "주인님, 빨리 오셔야겠는데요..?");
    }

}