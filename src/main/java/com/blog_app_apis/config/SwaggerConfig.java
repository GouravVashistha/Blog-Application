package com.blog_app_apis.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfig - OpenAPI 3.0 Configuration (SpringDoc)
 * 
 * Purpose: Configure Swagger UI and OpenAPI 3.0 documentation
 * Documentation will be available at: http://localhost:8080/swagger-ui.html
 * API docs in JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure OpenAPI 3.0 documentation
     * 
     * @return Customized OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blog App REST APIs")
                        .version("v1.0.0")
                        .description("Complete Blog Management API with JWT Authentication\n\n" +
                                "Features:\n" +
                                "- User Registration & Authentication (JWT)\n" +
                                "- Blog Post Management (CRUD Operations)\n" +
                                "- Comments Management\n" +
                                "- Category Management\n" +
                                "- Role-Based Access Control (Admin & User)\n" +
                                "- File Upload for Post Images\n\n" +
                                "Technology Stack:\n" +
                                "- Spring Boot 3.3.5\n" +
                                "- Spring Security with JWT\n" +
                                "- PostgreSQL Database\n" +
                                "- JPA/Hibernate ORM")
                        .contact(new Contact()
                                .name("Gourav Vashistha")
                                .email("gourav.vashistha@example.com")
                                .url("https://www.gouravvashistha.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
