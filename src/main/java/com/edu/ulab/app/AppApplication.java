package com.edu.ulab.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@SpringBootApplication
@EnableCaching
public class AppApplication {

	public static void main(String[] args) {
		log.info("---- app started");
		SpringApplication.run(AppApplication.class, args);
		log.info("---- app finished");
	}

}
