package com.blog_app_apis.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AuthResponse - DTO for successful authentication response
 * 
 * Contains JWT token and user information after successful login
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"success", "message", "token", "username", "timestamp"})
public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private String username;
    private long timestamp;
}

