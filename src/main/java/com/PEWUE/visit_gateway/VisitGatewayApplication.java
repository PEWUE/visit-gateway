package com.PEWUE.visit_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class VisitGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisitGatewayApplication.class, args);
	}

}
