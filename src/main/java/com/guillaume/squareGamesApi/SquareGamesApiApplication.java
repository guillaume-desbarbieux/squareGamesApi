package com.guillaume.squareGamesApi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Square Games API",
                version = "1.1",
                description = "API documentation for managing Games in Square Games"
        )
)
public class SquareGamesApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SquareGamesApiApplication.class, args);
	}

}
