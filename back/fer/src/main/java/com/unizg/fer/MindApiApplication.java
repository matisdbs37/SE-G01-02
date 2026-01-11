package com.unizg.fer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MindApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MindApiApplication.class, args);
	}

}
