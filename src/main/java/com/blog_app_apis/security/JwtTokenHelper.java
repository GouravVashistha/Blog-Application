package com.blog_app_apis.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtTokenHelper Class
 * <p>
 * Purpose:
 * This utility class is responsible for handling all JWT (JSON Web Token) operations
 * including token generation, validation, and claims extraction. It provides a centralized
 * mechanism for managing stateless authentication tokens in the Blog API application.
 * <p>
 * Key Responsibilities:
 * 1. Generate secure JWT tokens for authenticated users after successful login
 * 2. Validate incoming JWT tokens from client requests
 * 3. Extract and parse claims (username, expiration, etc.) from tokens
 * 4. Handle token expiration scenarios and validation errors gracefully
 * <p>
 * Technical Details:
 * - Uses HMAC-SHA256 (HS256) algorithm for signing tokens
 * - Tokens are stateless and self-contained, eliminating the need for server-side session storage
 * - Secret key is injected from application properties for secure configuration management
 * - Token expiration time is configurable via application properties
 * <p>
 * Integration Points:
 * - Called by JwtAuthenticationFilter for token validation on each request
 * - Called by authentication controllers during login process
 * - Used in conjunction with CustomUserDetailService for user authentication
 *
 * @author Blog App Development Team
 * @version 1.0
 */
@Component
public class JwtTokenHelper {

    /**
     * JWT Secret Key
     * This secret key is used for creating HMAC-SHA256 signatures on JWT tokens.
     * It is injected from the application.properties file for secure configuration management.
     * The value should be stored securely and never exposed in source code.
     * <p>
     * Security Requirement:
     * - Must be at least 32 characters/256 bits for HS256 algorithm compliance
     * - Should be unique per environment (development, staging, production)
     * - Must be changed if there is any suspicion of compromise
     * <p>
     * Property Key: app.jwt.secret
     */
    @Value("${app.jwt.secret}")
    private String secret;

    /**
     * JWT Token Expiration Duration
     * <p>
     * Defines how long a JWT token remains valid after creation (in milliseconds).
     * This value is injected from application.properties for flexible configuration
     * across different deployment environments.
     * <p>
     * Default Configuration: 18000000 milliseconds = 5 hours
     * <p>
     * Considerations:
     * - Shorter durations (1-2 hours) provide better security but require frequent re-authentication
     * - Longer durations (24+ hours) reduce authentication overhead but increase security risk window
     * - Should be balanced based on application security requirements and user experience needs
     * <p>
     * Property Key: app.jwt.expiration.ms
     */
    @Value("${app.jwt.expiration.ms}")
    private long jwtTokenValidity;

    /**
     * Generate HMAC-SHA256 Signing Key
     * <p>
     * Purpose:
     * Creates a cryptographic key object from the secret string for use in token
     * signing and verification operations. This is required by the JJWT library
     * to perform HMAC-based digital signature operations.
     * <p>
     * Technical Implementation:
     * Uses Keys.hmacShaKeyFor() from JJWT 0.12+ which is the modern approach
     * for secure key generation. This method ensures proper key derivation
     * and compliance with HMAC-SHA256 standards.
     * <p>
     * Security Implications:
     * The generated key must be kept secure and should never be exposed to clients.
     * This method is private to ensure the key creation logic is encapsulated.
     *
     * @return Key object suitable for HMAC-SHA256 operations
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Extract Username from JWT Token
     * <p>
     * Purpose:
     * Retrieves the username (subject claim) from a JWT token. This is typically
     * the user's email address and is used to identify which user the token
     * represents for authorization purposes.
     * <p>
     * Usage Context:
     * Invoked during token validation to ensure the token belongs to the
     * authenticated user making the request.
     * <p>
     * Operation Flow:
     * 1. Delegates to getClaimFromToken() with Claims::getSubject function
     * 2. Parses and validates token signature
     * 3. Extracts subject claim from validated token payload
     * 4. Returns the subject string (typically user email)
     *
     * @param token JWT token string from request header
     * @return Username/email address stored as subject claim
     * @throws ExpiredJwtException if token has expired
     * @throws Exception           if token signature is invalid or token is malformed
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extract Expiration Date from JWT Token
     * <p>
     * Purpose:
     * Retrieves the expiration timestamp from a JWT token to determine if the
     * token is still valid or has exceeded its allowed validity period.
     * <p>
     * Usage Context:
     * Used during token validation and in isTokenExpired() method to check
     * if the token has surpassed its expiration time.
     * <p>
     * Return Value:
     * A Date object representing the timestamp when the token expires.
     * Comparison with current time determines token validity.
     *
     * @param token JWT token string from request header
     * @return Date object representing token expiration timestamp
     * @throws ExpiredJwtException if token has already expired
     * @throws Exception           if token signature is invalid or token is malformed
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extract Any Claim from JWT Token
     * <p>
     * Purpose:
     * This is a generic method that provides flexible claim extraction from JWT tokens.
     * It accepts a function parameter that determines which specific claim to extract,
     * allowing for extensible and reusable claim extraction logic.
     * <p>
     * Design Pattern:
     * Uses Java functional programming (Function interface) to support different
     * claim extraction operations without requiring multiple methods.
     * <p>
     * Example Usage:
     * - getClaimFromToken(token, Claims::getSubject) - extract username
     * - getClaimFromToken(token, Claims::getExpiration) - extract expiration date
     * - getClaimFromToken(token, Claims::getId) - extract token ID
     * <p>
     * Method Implementation Flow:
     * 1. Calls getAllClaimsFromToken() to parse and validate token
     * 2. Applies the provided claimsResolver function to extracted claims
     * 3. Returns the result of the applied function
     *
     * @param token          JWT token string from request header
     * @param claimsResolver Functional interface that specifies which claim to extract
     * @param <T>            Generic return type determined by the claims resolver function
     * @return Extracted claim value of type T
     * @throws ExpiredJwtException if token has expired
     * @throws Exception           if token validation fails
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parse and Extract All Claims from JWT Token
     * <p>
     * Purpose:
     * This is the core method for JWT token parsing and validation. It deserializes
     * the JWT token, verifies the digital signature using the secret key, and extracts
     * all claims from the token payload if validation succeeds.
     * <p>
     * Security Operations Performed:
     * 1. Signature Verification: Ensures token has not been tampered with
     * 2. Token Format Validation: Verifies token follows proper JWT structure
     * 3. Algorithm Verification: Confirms HS256 algorithm was used for signing
     * <p>
     * Error Handling Strategy:
     * Implements comprehensive exception handling to catch various token validation
     * failure scenarios. Each specific exception is caught and logged for debugging
     * and auditing purposes.
     * <p>
     * Exceptions That May Occur:
     * - ExpiredJwtException: Token expiration timestamp has passed
     * - SignatureException: Token signature verification failed (tampering detected)
     * - MalformedJwtException: Token structure does not conform to JWT standard
     * - IllegalArgumentException: Token string is empty or null
     * - General Exception: Any other JWT processing errors
     * <p>
     * Implementation Notes:
     * This method is private as it should only be called internally through
     * the public getClaimFromToken() or getUsernameFromToken() methods.
     *
     * @param token JWT token string to parse and validate
     * @return Claims object containing all payload claims from the token
     * @throws ExpiredJwtException if token has exceeded expiration time
     * @throws Exception           if signature verification fails or token is malformed
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException ex) {
            System.out.println("Token has expired: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            System.out.println("Invalid token: " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * Check if JWT Token Has Expired
     * <p>
     * Purpose:
     * Determines whether a JWT token has exceeded its expiration timestamp.
     * This method is used to validate token freshness during authentication checks.
     * <p>
     * Operational Logic:
     * 1. Extracts the expiration date claim from the token
     * 2. Compares extracted expiration date with current system time
     * 3. Returns true if current time is after expiration time, false otherwise
     * <p>
     * Null Handling:
     * If the token contains no expiration claim (expiration is null), the token
     * is considered as never expiring and returns false.
     * <p>
     * Exception Handling:
     * If an ExpiredJwtException is caught, it confirms the token is expired
     * and returns true immediately without further processing.
     *
     * @param token JWT token string to check for expiration
     * @return true if token has expired, false if token is still valid
     */
    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            if (expiration == null) {
                return false;
            }
            return expiration.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        }
    }

    /**
     * Generate JWT Token for Authenticated User
     * <p>
     * Purpose:
     * Creates a new JWT token immediately after successful user authentication.
     * This token encapsulates user identity and is returned to the client for use
     * in subsequent API requests as proof of authentication.
     * <p>
     * Token Creation Process:
     * 1. Creates an empty claims map for additional token claims
     * 2. Delegates to doGenerateToken() with username and empty claims
     * 3. Returns a signed JWT token string
     * <p>
     * Token Characteristics:
     * - Subject Claim: Contains user's email/username
     * - Issued At: Timestamp of token creation
     * - Expiration: Calculated as current time plus configurable validity period
     * - Signature: HMAC-SHA256 digital signature for tamper-detection
     * <p>
     * Extensibility:
     * Custom claims can be added to the claims map before calling doGenerateToken().
     * Example: claims.put("role", userDetails.getAuthorities());
     *
     * @param userDetails Spring Security UserDetails object containing authenticated user information
     * @return Signed JWT token string ready for client transmission
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * Build and Sign JWT Token
     * <p>
     * Purpose:
     * Constructs the actual JWT token with all required and optional claims,
     * then signs it using the HMAC-SHA256 algorithm with the application's secret key.
     * This is the core token generation logic invoked by generateToken().
     * <p>
     * Token Structure:
     * JWT tokens consist of three base64-encoded components separated by dots:
     * - Header: Algorithm and token type metadata
     * - Payload: Claims including user identity, expiration, and custom data
     * - Signature: HMAC-SHA256 digital signature for verification
     * <p>
     * Claims Added to Token:
     * - Custom Claims: Application-specific claims passed as parameter
     * - Subject (sub): User's email/username for identity
     * - Issued At (iat): Timestamp when token was created
     * - Expiration (exp): Timestamp when token becomes invalid
     * <p>
     * Expiration Calculation:
     * Token validity is calculated as: current_time + jwtTokenValidity (milliseconds)
     * Default configuration: 18,000,000 ms = 5 hours
     * <p>
     * Signing Algorithm:
     * HMAC-SHA256 is used for symmetric signing. This means the same secret key
     * used to sign is also used to verify, making it suitable for internal APIs.
     *
     * @param claims  Map of custom claims to include in token payload
     * @param subject User identifier (typically email) to store as token subject
     * @return Fully constructed and signed JWT token as a string
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() + jwtTokenValidity);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validate JWT Token Against User
     * <p>
     * Purpose:
     * Performs comprehensive validation of a JWT token to ensure it is safe and
     * legitimate to use for granting access to protected resources. This is the
     * primary method called by JWT authentication filters on each request.
     * <p>
     * Validation Process:
     * The method performs three sequential validation checks:
     * <p>
     * Check 1 - Signature and Format Verification:
     * Executed implicitly through getUsernameFromToken() which parses and validates
     * the token's digital signature. Any tampering or format issues are detected here.
     * <p>
     * Check 2 - User Identity Verification:
     * Extracts the subject (username) from the token and compares it with the
     * authenticated user's username. A mismatch indicates a token intended for
     * a different user and is rejected.
     * <p>
     * Check 3 - Expiration Verification:
     * Checks if the token's expiration timestamp has been exceeded. Expired tokens
     * are rejected regardless of signature or user identity validity.
     * <p>
     * Exception Handling:
     * Implements multi-layer exception handling to catch various JWT-related errors:
     * - ExpiredJwtException: Token expiration time has passed
     * - SignatureException: Token signature verification failed
     * - MalformedJwtException: Token structure is malformed
     * - IllegalArgumentException: Token content is invalid or empty
     * - General Exception: Any other unexpected errors
     * <p>
     * All exceptions are caught and logged without propagating to prevent
     * information disclosure through error messages.
     *
     * @param token       JWT token string extracted from request Authorization header
     * @param userDetails Current authenticated user's principal with security details
     * @return true if all validation checks pass and token is valid and safe to use
     * false if any validation check fails or any exception occurs
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = getUsernameFromToken(token);

            if (!username.equals(userDetails.getUsername())) {
                System.out.println("Username mismatch: " + username + " does not match " + userDetails.getUsername());
                return false;
            }

            if (isTokenExpired(token)) {
                System.out.println("Token has exceeded its expiration time");
                return false;
            }

            System.out.println("Token validation successful for user: " + username);
            return true;

        } catch (ExpiredJwtException ex) {
            System.out.println("Token Validation Failed - Token has expired: " + ex.getMessage());
            return false;
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            System.out.println("Token Validation Failed - Invalid signature detected: " + ex.getMessage());
            return false;
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            System.out.println("Token Validation Failed - Malformed JWT structure: " + ex.getMessage());
            return false;
        } catch (IllegalArgumentException ex) {
            System.out.println("Token Validation Failed - Empty or invalid JWT claims: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.out.println("Token Validation Failed - Unexpected error: " + ex.getMessage());
            return false;
        }
    }
}
