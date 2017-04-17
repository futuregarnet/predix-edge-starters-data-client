package com.ge.predix.solsvc.springconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@EnableAutoConfiguration
@PropertySource("classpath:application-default.properties")
@ComponentScan("com.ge.predix.solsvc.*")
@PropertySource("classpath:application-default.properties")
@ImportResource(
{
    "classpath*:META-INF/spring/predix-rest-client-scan-context.xml"
})
public class PredixDataClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(PredixDataClientApplication.class, args);
	}
}
