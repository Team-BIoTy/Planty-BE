package com.BioTy.Planty;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class PlantyApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlantyApplication.class, args);
	}

	@PostConstruct
	public void started() {
		// JVM 기본 시간대를 KST로 고정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
