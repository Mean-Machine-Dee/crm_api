package com.crm.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.SpringServletContainerInitializer;

@SpringBootApplication
@ComponentScan({"com.crm.api"})
//public class ApiApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(ApiApplication.class, args);
//	}
//
//}

public class ApiApplication extends SpringServletContainerInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}

