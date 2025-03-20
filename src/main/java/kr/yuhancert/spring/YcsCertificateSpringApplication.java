package kr.yuhancert.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class YcsCertificateSpringApplication {

	public static void main(String[] args) {

		SpringApplication app = new SpringApplication(YcsCertificateSpringApplication.class);

		app.run(args);
	}

}
