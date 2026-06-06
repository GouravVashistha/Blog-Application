package com.blog_app_apis.exceptions;

/**
 * UsernameNotFoundException - Custom exception for user not found scenarios
 * 
 * Purpose: Thrown when a user cannot be found by username/email in the database
 * Extends RuntimeException: Makes it an unchecked exception
 * 
 * Scenarios:
 * - User email not found during authentication
 * - User ID not found during profile lookup
 * - User email not found during update operations
 * 
 * Usage:
 * throw new UsernameNotFoundException("User", "email: user@example.com", 0);
 */
public class UsernameNotFoundException extends RuntimeException {

    /**
     * Resource name that was not found (e.g., "User")
     */
    private String resourceName;

    /**
     * Field name and value that was searched (e.g., "email: user@example.com")
     */
    private String fieldName;

    /**
     * Identifier/value used for search
     */
    private Object fieldValue;

    /**
     * Constructor: Create exception with resource name, field info, and value
     * 
     * @param resourceName - Name of the resource not found (e.g., "User")
     * @param fieldName - Name of the field searched (e.g., "email")
     * @param fieldValue - Value that was searched for
     */
    public UsernameNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s = '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructor: Create exception with custom message
     * 
     * @param message - Custom error message
     */
    public UsernameNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor: Create exception with message and cause
     * 
     * @param message - Error message
     * @param cause - Root cause exception
     */
    public UsernameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Getters
    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
