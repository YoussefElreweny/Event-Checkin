package com.youssef.eventcheckin.common.config;



import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eventCheckinOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Event Check-In API")
                .version("v1")
                .description("Event registration and race-safe door check-in"));
    }
}