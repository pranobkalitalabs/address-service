package com.platform.address.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI addressServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pranob Kalita Labs - UK Address & Geocoding Service API")
                        .description("High-performance microservice for UK postcode validation, geocoding coordinates, flat/premise resolution, and postcode autocompletion with Redis caching.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pranob Jyoti Kalita")
                                .email("contact@pranobkalitalabs.co.uk")
                                .url("https://pranobkalitalabs.co.uk"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("https://address.pranobkalitalabs.co.uk")
                                .description("Production Cloud Server (GCP)"),
                        new Server()
                                .url("http://localhost:8082")
                                .description("Local Development Server")
                ));
    }
}
