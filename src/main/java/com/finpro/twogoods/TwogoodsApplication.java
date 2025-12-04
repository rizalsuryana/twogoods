package com.finpro.twogoods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients

public class TwogoodsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwogoodsApplication.class, args);
	}

}
