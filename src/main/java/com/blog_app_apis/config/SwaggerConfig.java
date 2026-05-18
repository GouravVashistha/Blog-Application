package com.blog_app_apis.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfig - OpenAPI 3.0 Configuration (SpringDoc)
 * 
 * Purpose: Configure Swagger UI and OpenAPI 3.0 documentation with JWT Authentication
 * Features:
 * - Custom API documentation with detailed information
 * - JWT Bearer token security scheme
 * - "Authorize" button in Swagger UI to input JWT token
 * - Automatic token passing to protected endpoints
 * 
 * Documentation URLs:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - API Docs (JSON): http://localhost:8080/v3/api-docs
 * - API Docs (YAML): http://localhost:8080/v3/api-docs.yaml
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure OpenAPI 3.0 documentation with JWT Security
     * 
     * This bean:
     * 1. Sets API title, version, description
     * 2. Defines JWT Bearer token security scheme
     * 3. Adds "Authorize" button in Swagger UI
     * 4. Specifies security requirements for protected endpoints
     * 
     * @return Customized OpenAPI configuration with security
     */
    @Bean
    public OpenAPI customOpenAPI() {
        
        // Beautiful description with HTML formatting
        String apiDescription = 
                "<div style='font-family: Arial, sans-serif;'>" +
                
                "<h2 style='color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px;'>📱 Blog Management Platform</h2>" +
                "<p style='color: #555; font-size: 15px; line-height: 1.6;'>" +
                "A comprehensive REST API for managing blogs, posts, comments, and user authentication with " +
                "<strong>JWT-based security</strong> and role-based access control." +
                "</p>" +
                
                "<hr style='border: 1px solid #ecf0f1;'>" +
                
                "<h3 style='color: #2c3e50; margin-top: 20px;'>✨ Key Features</h3>" +
                "<ul style='color: #555; font-size: 14px; line-height: 1.8;'>" +
                "<li><strong>🔐 User Authentication</strong> - Register and login with JWT token generation</li>" +
                "<li><strong>📝 Blog Post Management</strong> - Create, read, update, delete posts with categories</li>" +
                "<li><strong>💬 Comments System</strong> - Add and manage comments on posts</li>" +
                "<li><strong>🏷️ Categories</strong> - Organize posts by categories</li>" +
                "<li><strong>👥 Role-Based Access</strong> - Admin and User roles with different permissions</li>" +
                "<li><strong>🖼️ Image Upload</strong> - Upload images for blog posts</li>" +
                "</ul>" +
                
                "<h3 style='color: #2c3e50; margin-top: 20px;'>🛠️ Technology Stack</h3>" +
                "<table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>" +
                "<tr style='background-color: #ecf0f1;'>" +
                "<td style='padding: 10px; border: 1px solid #bdc3c7;'><strong>Backend</strong></td>" +
                "<td style='padding: 10px; border: 1px solid #bdc3c7;'>Spring Boot 3.3.5, Java 21</td>" +
                "</tr>" +
                "<tr>" +
                "<td style='padding: 10px; border: 1px solid #bdc3c7;'><strong>Security</strong></td>" +
                "<td style='padding: 10px; border: 1px solid #bdc3c7;'>Spring Security, JWT (JJWT 0.12.3), BCrypt</td>" +
                "</tr>" +
                "<tr style='background-color: #ecf0f1;'>" +
                "<td style='padding: 10px; border: 1px solid #bdc3c7;'><strong>Database</strong></td>" +
                "<td style='padding: 10px; border: 1px solid #bdc3c7;'>PostgreSQL with JPA/Hibernate ORM</td>" +
                "</tr>" +
                "<tr>" +
                "<td style='padding: 10px; border: 1px solid #bdc3c7;'><strong>API Documentation</strong></td>" +
                "<td style='padding: 10px; border: 1px solid #bdc3c7;'>OpenAPI 3.0, SpringDoc, Swagger UI</td>" +
                "</tr>" +
                "</table>" +
                
                "<h3 style='color: #2c3e50; margin-top: 20px;'>🔑 Authentication Guide</h3>" +
                "<div style='background-color: #f8f9fa; padding: 15px; border-left: 4px solid #3498db; border-radius: 4px; margin-top: 10px;'>" +
                "<p style='color: #555; font-size: 14px; margin: 0;'>" +
                "<strong>Step 1:</strong> Register a new user or login with existing credentials<br>" +
                "<strong>Step 2:</strong> Copy the JWT token from the login response<br>" +
                "<strong>Step 3:</strong> Click the <span style='background-color: #27ae60; color: white; padding: 2px 8px; border-radius: 3px;'>Authorize</span> button (🔒)<br>" +
                "<strong>Step 4:</strong> Paste: <code style='background-color: #ecf0f1; padding: 2px 6px;'>Bearer eyJhbGciOiJIUzM4NCJ9...</code><br>" +
                "<strong>Step 5:</strong> All protected endpoints will automatically include the token!<br>" +
                "</p>" +
                "</div>" +
                
                "<h3 style='color: #2c3e50; margin-top: 20px;'>🌐 Base URL</h3>" +
                "<p style='color: #e74c3c; font-size: 14px; background-color: #fef5e7; padding: 10px; border-radius: 4px;'>" +
                "<strong>http://localhost:8080</strong>" +
                "</p>" +
                
                "<h3 style='color: #2c3e50; margin-top: 20px;'>📊 API Endpoints</h3>" +
                "<p style='color: #555; font-size: 14px;'>" +
                "<strong>Auth Endpoints (Public)</strong> - No token required<br>" +
                "<strong>Get Endpoints (Public)</strong> - Read data without authentication<br>" +
                "<strong>Other Endpoints (Protected)</strong> - Require valid JWT token" +
                "</p>" +
                
                "</div>";
        
        return new OpenAPI()
                // Step 1: Basic API Information with beautiful description
                .info(new Info()
                        .title("Blog App REST APIs")
                        .version("v1.0.0")
                        .description(apiDescription)
                        .contact(new Contact()
                                .name("Gourav Vashistha")
                                .email("gourav.vashistha@example.com")
                                .url("https://www.gouravvashistha.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                
                // Step 2: Define JWT Bearer Token Security Scheme
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer JWT"))
                
                // Step 3: Define the actual security scheme (JWT Bearer)
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer JWT", new SecurityScheme()
                                .type(Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description(
                                        "<div style='font-family: Arial, sans-serif;'>" +
                                        "<h4 style='color: #2c3e50; margin-bottom: 10px;'>🔐 JWT Authentication</h4>" +
                                        "<p style='color: #555; font-size: 13px; margin: 0 0 10px 0;'>" +
                                        "Enter your JWT token below to authorize requests to protected endpoints." +
                                        "</p>" +
                                        "<div style='background-color: #fff3cd; padding: 10px; border-radius: 4px; margin-bottom: 10px;'>" +
                                        "<strong>Format:</strong> <code>Bearer {your_jwt_token}</code>" +
                                        "</div>" +
                                        "<p style='color: #555; font-size: 13px; margin: 0;'>" +
                                        "<strong>Example:</strong><br>" +
                                        "<code style='font-size: 11px; color: #c0392b;'>" +
                                        "Bearer eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWI..." +
                                        "</code>" +
                                        "</p>" +
                                        "</div>"
                                )
                                .name("Authorization")));
    }
}
