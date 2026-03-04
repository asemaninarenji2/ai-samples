package com.mainapp.openai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OpenaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenaiApplication.class, args);
	}

}
