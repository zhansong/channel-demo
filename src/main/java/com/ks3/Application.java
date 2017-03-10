package com.ks3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ks3.*"})
@EnableAsync
public class Application {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);
		ConfigurableApplicationContext context = application.run(Application.class, args);
		
	}
}
