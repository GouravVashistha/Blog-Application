package com.blog_app_apis.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AuthResponse - DTO for successful authentication response
 * 
 * Contains JWT token, Bearer token format, and user information after successful login
 * 
 * Fields:
 * - success: Boolean indicating authentication success
 * - message: Human-readable success message
 * - tokenType: Always "Bearer" for JWT tokens
 * - token: Raw JWT token string (without Bearer prefix)
 * - bearerToken: Pre-formatted token with "Bearer " prefix for Authorization header
 * - username: Authenticated user's email/username
 * - expiresIn: Token expiration time in milliseconds (default 5 hours = 18000000 ms)
 * - timestamp: Server timestamp when token was generated
 * 
 * Usage in Frontend/Client:
 * 1. Option 1: Use raw token
 *    Authorization: Bearer {token}
 * 
 * 2. Option 2: Use pre-formatted bearerToken (copy-paste)
 *    Authorization: {bearerToken}
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"success", "message", "tokenType", "token", "bearerToken", "username", "expiresIn", "timestamp"})
public class AuthResponse {
    
    /**
     * success: Authentication result flag
     * true: Authentication successful, token generated
     */
    private boolean success;
    
    /**
     * message: Human-readable response message
     * Example: "Authentication successful. JWT token generated."
     */
    private String message;
    
    /**
     * tokenType: Type of token returned
     * Value: "Bearer" (standard for JWT authentication)
     */
    private String tokenType;
    
    /**
     * token: The actual JWT token string without Bearer prefix
     * Format: "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJlbWFpbCIsImlhdCI6MTcxNjA4Njg1MCwiZXhwIjoxNzE2MTA0ODUwfQ..."
     * 
     * How to use in requests:
     * Authorization: Bearer {token}
     */
    private String token;
    
    /**
     * bearerToken: Pre-formatted Bearer token for client convenience
     * Format: "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJlbWFpbCIsImlhdCI6MTcxNjA4Njg1MCwiZXhwIjoxNzE2MTA0ODUwfQ..."
     * 
     * Client can directly copy-paste into Authorization header:
     * Authorization: {bearerToken}
     * 
     * Useful for:
     * - Postman: Paste in Authorization header
     * - Frontend: No need to concatenate "Bearer " prefix manually
     */
    private String bearerToken;
    
    /**
     * username: Authenticated user's email address
     * Example: "gvashistha4@gmail.com"
     */
    private String username;
    
    /**
     * expiresIn: Token expiration duration in milliseconds
     * Default value: 18000000 (5 hours)
     * 
     * How to calculate expiration time on client side:
     * tokenExpiryTime = timestamp + expiresIn
     * OR: new Date(timestamp + expiresIn)
     */
    private long expiresIn;
    
    /**
     * timestamp: Server timestamp when token was generated
     * Format: Unix timestamp in milliseconds
     * Example: 1716086850093
     * 
     * Used for:
     * - Verifying response freshness
     * - Calculating token expiration on client
     * - Audit logging
     */
    private long timestamp;
}

