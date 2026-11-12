package com.tecsup.producto_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Microservicio de productos. Se registra en Eureka como "producto-service"
 * y consume categoria-service mediante OpenFeign.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ProductoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductoServiceApplication.class, args);
	}

}
