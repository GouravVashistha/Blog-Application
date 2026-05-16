package com.blog_app_apis.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JwtAuthResponce - Enhanced JWT Authentication Response DTO
 * 
 * Purpose: Define the structure of JWT token response sent to client
 * Contains: token details, token type, expiration, timestamp, and metadata
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthResponce {
    
    /**
     * success: Boolean indicating if authentication was successful
     * true: Authentication succeeded, token generated
     * false: Authentication failed
     */
    private Boolean success;
    
    /**
     * message: Human-readable message describing the authentication result
     * Example: "Authentication successful. JWT token generated."
     */
    private String message;
    
    /**
     * tokenType: The type of token returned
     * Value: "Bearer" (standard for JWT tokens)
     * Used in: Authorization header as "Bearer {token}"
     */
    private String tokenType;
    
    /**
     * token: The actual JWT token string
     * Format: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * Use this token in Authorization header: Authorization: Bearer {token}
     */
    private String token;
    
    /**
     * bearerToken: Pre-formatted Bearer token for client convenience
     * Format: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * Client can copy-paste directly into Authorization header
     */
    private String bearerToken;
    
    /**
     * username: The authenticated user's email/username
     * Example: "gvashistha4@gmail.com"
     */
    private String username;
    
    /**
     * expiresIn: Token expiration time in milliseconds
     * Default: 18000000 (5 hours)
     * Value: Duration in ms = current_time + expiresIn
     */
    private Long expiresIn;
    
    /**
     * timestamp: Server timestamp when token was generated
     * Format: Unix timestamp in milliseconds
     * Example: 1716086850093
     * Use: Client can verify response freshness
     */
    private Long timestamp;
}
