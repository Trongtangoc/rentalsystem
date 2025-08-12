// SwaggerConfig.java - Simplified version without external dependencies
package com.codegym.projectmodule5.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // This class is prepared for future Swagger integration
    // To enable Swagger documentation, add these dependencies to build.gradle:
    // implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0'

    // Then uncomment the code below:

    /*
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rental System API")
                        .description("A comprehensive rental system API similar to Airbnb")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Rental System Team")
                                .email("support@rentalsystem.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
    */
}