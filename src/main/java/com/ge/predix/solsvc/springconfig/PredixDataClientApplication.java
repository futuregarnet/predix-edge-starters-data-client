package com.ge.predix.solsvc.springconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.ge.predix.solsvc")
@EnableAutoConfiguration
public class PredixDataClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(PredixDataClientApplication.class, args);
	}
}
