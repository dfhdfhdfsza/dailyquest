package com.dailyquest.dailyquest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DailyquestApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailyquestApplication.class, args);
	}

}
