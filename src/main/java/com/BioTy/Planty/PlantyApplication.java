package com.BioTy.Planty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PlantyApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlantyApplication.class, args);
	}

}
