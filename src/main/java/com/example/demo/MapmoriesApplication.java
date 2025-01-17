package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class MapmoriesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MapmoriesApplication.class, args);
	}

}
