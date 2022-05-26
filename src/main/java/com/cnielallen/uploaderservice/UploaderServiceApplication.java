package com.cnielallen.uploaderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class UploaderServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(UploaderServiceApplication.class, args);
	}
}
