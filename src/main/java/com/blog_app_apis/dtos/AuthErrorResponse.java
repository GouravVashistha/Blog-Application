package com.blog_app_apis.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AuthErrorResponse - DTO for authentication error response
 * 
 * Contains error details when authentication fails
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({"success", "message", "error", "timestamp"})
public class AuthErrorResponse {
    private boolean success;
    private String message;
    private String error;
    private long timestamp;
}

