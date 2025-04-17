package com.devterin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
public class EcommerceSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceSpringApplication.class, args);
	}

}
