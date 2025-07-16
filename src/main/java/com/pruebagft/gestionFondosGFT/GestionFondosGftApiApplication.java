package com.pruebagft.gestionFondosGFT;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "GFT Fund Management API", // Your API Title
				version = "1.0",                  // Your API Version
				description = "API for managing funds, clients, and transactions within the GFT financial system."
		)
)
public class GestionFondosGftApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionFondosGftApiApplication.class, args);
	}

}
