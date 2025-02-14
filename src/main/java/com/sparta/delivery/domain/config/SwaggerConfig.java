package com.sparta.delivery.domain.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("2NE1 Delivery API")
                        .version("0.0.1")
                        .description("2NE1 Delivery API 명세서")
                        .contact(new Contact().name("Support").email("support@delivery.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public ApiResponses globalResponses() {
        return new ApiResponses()
                .addApiResponse("400", new ApiResponse().description("Bad Request"))
                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                .addApiResponse("404", new ApiResponse().description("Not Found"))
                .addApiResponse("500", new ApiResponse().description("Internal Server Error"));
    }
}
