package com.blog_app_apis.controllers;

import com.blog_app_apis.Entity.JwtAuthRequest;
import com.blog_app_apis.Entity.JwtAuthResponce;
import com.blog_app_apis.dtos.UserDTO;
import com.blog_app_apis.exceptions.UsernameNotFoundException;
import com.blog_app_apis.security.JwtTokenHelper;
import com.blog_app_apis.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController - Handles Authentication and Authorization endpoints
 * <p>
 * Purpose: Provides REST endpoints for user authentication (login and registration)
 * <p>
 * Key Responsibilities:
 * 1. Authenticate users with username and password
 * 2. Generate JWT tokens for authenticated users
 * 3. Handle authentication errors and exceptions
 * 4. Register new users to the system
 * <p>
 * Endpoints:
 * - POST /api/v1/auth/login - Generate JWT token for authenticated user
 * - POST /api/v1/auth/register - Register a new user to the system
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    /**
     * JwtTokenHelper: Utility class for JWT token generation and validation
     * Responsible for creating and verifying JWT tokens for stateless authentication
     */
    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    /**
     * UserDetailsService: Spring Security service for loading user details
     * Implementation: CustomUserDetailService
     * Purpose: Fetch user information from database using email/username
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * AuthenticationManager: Core Spring Security component
     * Purpose: Authenticates user credentials and manages the authentication process
     * Validates username and password against stored user details
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * UserService: Business logic service for user operations
     * Purpose: Handle user registration and user-related business logic
     */
    @Autowired
    private UserService userService;

    /**
     * LOGIN API ENDPOINT
     * <p>
     * Purpose: Authenticate user credentials and generate JWT token
     * <p>
     * Request: JwtAuthRequest containing username and password
     * Response: JwtAuthResponse containing generated JWT token
     * <p>
     * HTTP Flow:
     * 1. Client sends username and password
     * 2. Server authenticates credentials with AuthenticationManager
     * 3. If authentication successful:
     * - Load user details from database
     * - Generate JWT token using JwtTokenHelper
     * - Return token in response body
     * 4. If authentication fails:
     * - Return appropriate HTTP status and error message
     * <p>
     * HTTP Status Codes:
     * - 200 OK: Successful authentication, token generated and returned
     * - 400 BAD_REQUEST: Invalid request format or username/password missing
     * - 401 UNAUTHORIZED: Invalid username or password provided
     * - 403 FORBIDDEN: User account is disabled or not authorized
     * - 404 NOT_FOUND: User account does not exist in the system
     * - 500 INTERNAL_SERVER_ERROR: Unexpected server error or database issue
     * <p>
     * Exception Handling:
     * This endpoint handles the following exception scenarios:
     * 1. BadCredentialsException: Password is incorrect for the user
     * 2. DisabledException: User account is disabled/inactive
     * 3. UsernameNotFoundException: User email not found in database
     * 4. ExpiredJwtException: JWT token has expired (if applicable)
     * 5. SignatureException: JWT token signature is invalid
     * 6. MalformedJwtException: JWT token format is corrupted/invalid
     * 7. IllegalArgumentException: Empty or null JWT token provided
     * 8. General Exception: Any unexpected error during authentication
     *
     * @param request - JwtAuthRequest containing username and password
     * @return ResponseEntity with JWT token on success or error details on failure
     */
    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody JwtAuthRequest request) {

        try {
            // Validate input parameters
            if (request == null || request.getUsername() == null || request.getPassword() == null) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Error: Username and password are required for login");
            }

            if (request.getUsername().trim().isEmpty() || request.getPassword().trim().isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Error: Username and password cannot be empty");
            }

            // Step 1: Authenticate the user with provided credentials
            // This method validates username and password against database
            this.authenticate(request.getUsername(), request.getPassword());
            System.out.println("Successfully authenticated user: " + request.getUsername());

            // Step 2: Load user details from database using authenticated username
            // Returns UserDetails object with username, password, authorities, and account status
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getUsername());

            // Step 3: Generate JWT token for the authenticated user
            // Token contains username, issued time, expiration time, and signature
            String token = this.jwtTokenHelper.generateToken(userDetails);
            System.out.println("JWT token successfully generated for user: " + request.getUsername());

            // Step 4: Prepare JWT response object with token
            JwtAuthResponce response = new JwtAuthResponce();
            response.setToken(token);

            // Step 5: Return response with HTTP 200 OK status and token
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            // Exception: Thrown when password is incorrect for the user
            // Scenario: User exists but password does not match
            // HTTP Status: 401 UNAUTHORIZED
            // Action: Client should verify password and retry
            System.out.println("Authentication failed: Bad credentials for user " + request.getUsername());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication failed: Invalid username or password provided");

        } catch (DisabledException ex) {
            // Exception: Thrown when user account is disabled/inactive
            // Scenario: User exists in database but account is marked as inactive
            // HTTP Status: 403 FORBIDDEN
            // Action: Administrator needs to enable the account
            System.out.println("Authentication failed: User account is disabled for user " + request.getUsername());
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Authentication failed: Your account is disabled. Please contact administrator");

        } catch (UsernameNotFoundException ex) {
            // Exception: Thrown when user email is not found in database
            // Scenario: Email does not match any user record
            // HTTP Status: 404 NOT_FOUND
            // Action: User should register first or verify email address
            System.out.println("Authentication failed: User not found with username " + request.getUsername());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Authentication failed: User account not found. Please register first");

        } catch (ExpiredJwtException ex) {
            // Exception: Thrown when JWT token has expired
            // Scenario: Token timestamp is before current time
            // HTTP Status: 401 UNAUTHORIZED
            // Action: Client should perform login again to get fresh token
            System.out.println("JWT validation failed: Token has expired");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("JWT error: Token has expired. Please login again");

        } catch (SignatureException ex) {
            // Exception: Thrown when JWT token signature is invalid
            // Scenario: Token was tampered with or signed with different key
            // HTTP Status: 401 UNAUTHORIZED
            // Action: Reject request, possible security breach
            System.out.println("JWT validation failed: Invalid token signature - possible tampering detected");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("JWT error: Invalid token signature. Token may have been tampered with");

        } catch (MalformedJwtException ex) {
            // Exception: Thrown when JWT token format is corrupted
            // Scenario: Token structure is invalid, missing parts, or wrong encoding
            // HTTP Status: 400 BAD_REQUEST
            // Action: Client should provide valid token format
            System.out.println("JWT validation failed: Malformed JWT token format");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("JWT error: Invalid token format. Token structure is corrupted");

        } catch (IllegalArgumentException ex) {
            // Exception: Thrown when JWT token is empty or null
            // Scenario: Authorization header missing or empty token provided
            // HTTP Status: 400 BAD_REQUEST
            // Action: Client should provide token in Authorization header
            System.out.println("JWT validation failed: Empty or null JWT token");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("JWT error: Token cannot be empty. Please provide valid JWT token");

        } catch (Exception ex) {
            // Catch all unexpected exceptions during login process
            // HTTP Status: 500 INTERNAL_SERVER_ERROR
            // Action: Log error, notify administrator, provide generic message
            System.out.println("Unexpected error during login process: " + ex.getClass().getName());
            ex.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Unexpected error occurred during authentication. Please try again later. Details: " + ex.getMessage());
        }
    }

    /**
     * AUTHENTICATE METHOD - Validates user credentials with comprehensive error handling
     * <p>
     * Purpose: Verify that provided username and password are valid
     * Process:
     * 1. Create UsernamePasswordAuthenticationToken with provided credentials
     * 2. Pass token to AuthenticationManager for validation
     * 3. AuthenticationManager validates against user database
     * 4. If validation fails, throws appropriate exception
     * <p>
     * Exceptions Thrown and Handled:
     * - BadCredentialsException: If password is incorrect for the user
     * - DisabledException: If user account is disabled/marked as inactive
     * - UsernameNotFoundException: If user email is not found in database
     * - LockedException: If user account is locked (too many login attempts)
     * - AccountExpiredException: If user account has expired
     * <p>
     * Note: Method does NOT return any value
     * If authentication succeeds, method completes normally
     * If authentication fails, exception is automatically thrown by AuthenticationManager
     *
     * @param username - Username (email) of the user to authenticate
     * @param password - Password of the user in plain text
     * @throws BadCredentialsException if password does not match stored encrypted password
     * @throws DisabledException if user account status is disabled
     * @throws UsernameNotFoundException if user email does not exist in database
     * @throws Exception for any other authentication-related errors
     */
    private void authenticate(String username, String password) {

        try {
            // Step 1: Create authentication token with provided username and password
            // This token is used by AuthenticationManager to validate credentials
            // Token contains unauthenticated user details
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            // Step 2: Pass authentication token to AuthenticationManager
            // Manager validates credentials against database and user details service
            // Manager uses:
            // - CustomUserDetailService to load user from database
            // - PasswordEncoder (BCryptPasswordEncoder) to validate password
            // - User authorities to check permissions
            this.authenticationManager.authenticate(authenticationToken);
            
            // If we reach here, authentication was successful
            System.out.println("User authentication successful: " + username);

        } catch (BadCredentialsException ex) {
            // Caught when password does not match encrypted password in database
            // Reason: Authentication comparison failed
            // Action: Log and throw exception to be caught by login endpoint
            System.out.println("Authentication error: Bad credentials for user " + username);
            throw new BadCredentialsException("Invalid password for user: " + username);

        } catch (DisabledException ex) {
            // Caught when user account is marked as inactive/disabled
            // Reason: User.isEnabled() returns false
            // Action: Log and throw exception to be caught by login endpoint
            System.out.println("Authentication error: User account is disabled for user " + username);
            throw new DisabledException("User account is disabled: " + username);

        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
            // Caught when CustomUserDetailService cannot find user by email
            // Reason: User email does not exist in database
            // Action: Convert to our custom UsernameNotFoundException for consistency
            System.out.println("Authentication error: User not found - " + username);
            throw new UsernameNotFoundException("User", "email", username);

        } catch (Exception ex) {
            // Catch all other authentication errors
            // Scenarios: Account locked, expired, null pointer, etc.
            // Action: Log error details and throw as is
            System.out.println("Authentication error: " + ex.getClass().getName() + " - " + ex.getMessage());
            throw ex;
        }
    }

    /**
     * REGISTER ENDPOINT
     * <p>
     * Purpose: Register a new user to the system
     * <p>
     * Request: UserDTO containing user registration details
     * - name: Full name of the user
     * - email: Email address (used as username)
     * - password: User password (will be encrypted with BCrypt)
     * - about: User bio or description
     * <p>
     * Response: Registered UserDTO with HTTP 201 CREATED status
     * <p>
     * Process:
     * 1. Receive UserDTO from request body
     * 2. Call UserService.registerNewUser() to save user to database
     * 3. Return saved UserDTO with HTTP 201 CREATED status
     * <p>
     * HTTP Status Codes:
     * - 201 CREATED: User successfully registered
     * - 400 BAD_REQUEST: Invalid user data
     * - 409 CONFLICT: User with email already exists
     * - 500 INTERNAL_SERVER_ERROR: Database or server error
     *
     * @param userDTO - User registration data transfer object
     * @return ResponseEntity with registered UserDTO and HTTP 201 CREATED status
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {

        // Call UserService to register new user
        // Service handles password encryption, validation, and database persistence
        UserDTO registeredUser = this.userService.registerNewUser(userDTO);

        // Return registered user with HTTP 201 CREATED status
        // 201 CREATED indicates successful resource creation
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }
}
