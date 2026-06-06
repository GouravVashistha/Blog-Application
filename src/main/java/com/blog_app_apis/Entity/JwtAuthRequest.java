package com.blog_app_apis.Entity;

import lombok.Data;

/**
 * JwtAuthRequest - Data Transfer Object for login request
 * 
 * Purpose: Captures user credentials (username and password) from login endpoint
 * Used by AuthController to receive and process login requests
 * 
 * Fields:
 * - username: User email address (used as username for authentication)
 * - password: User password (validated against encrypted password in database)
 * 
 * Note: Uses Lombok @Data annotation which automatically generates:
 * - Getters and setters for all fields
 * - equals(), hashCode(), toString() methods
 * - No-argument constructor
 */
@Data
public class JwtAuthRequest {
    
    /**
     * Username field (email address)
     * Used to identify the user during authentication
     * Must match email in database
     */
    private String username;
    
    /**
     * Password field
     * Must match encrypted password in database
     * Will be validated against bcrypt encrypted password
     */
    private String password;
}
