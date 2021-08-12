package com.ibm.garage.cnb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class FeedbackBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedbackBackendApplication.class, args);
	}

}
