package com.blog_app_apis.controllers;

import com.blog_app_apis.Entity.JwtAuthRequest;
import com.blog_app_apis.Entity.JwtAuthResponce;
import com.blog_app_apis.dtos.AuthErrorResponse;
import com.blog_app_apis.dtos.AuthResponse;
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
    /**
     * IMPROVED LOGIN API ENDPOINT
     * 
     * Features:
     * - Comprehensive input validation with detailed error messages
     * - Structured response with success flag, message, token, and timestamp
     * - Detailed logging for debugging and security auditing
     * - Better exception handling with specific HTTP status codes
     * - Response includes authenticated user information
     */
    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody JwtAuthRequest request) {
        long startTime = System.currentTimeMillis();
        String clientUsername = null;
        
        try {
            // ========== STEP 1: INPUT VALIDATION ==========
            System.out.println("=== LOGIN REQUEST INITIATED ===");
            
            // Validate request object is not null
            if (request == null) {
                System.out.println("❌ Validation Failed: Request body is null");
                AuthErrorResponse errorResponse = new AuthErrorResponse(
                    false,
                    "Validation Failed",
                    "Request body cannot be null",
                    System.currentTimeMillis()
                );
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(errorResponse);
            }
            
            // Validate username is provided
            if (request.getUsername() == null || request.getUsername().isBlank()) {
                System.out.println("❌ Validation Failed: Username is missing or empty");
                AuthErrorResponse errorResponse = new AuthErrorResponse(
                    false,
                    "Validation Failed",
                    "Username (email) is required and cannot be empty",
                    System.currentTimeMillis()
                );
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(errorResponse);
            }
            
            // Validate password is provided
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                System.out.println("❌ Validation Failed: Password is missing or empty");
                AuthErrorResponse errorResponse = new AuthErrorResponse(
                    false,
                    "Validation Failed",
                    "Password is required and cannot be empty",
                    System.currentTimeMillis()
                );
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(errorResponse);
            }
            
            // Sanitize username (trim whitespace)
            clientUsername = request.getUsername().trim();
            String clientPassword = request.getPassword();
            
            System.out.println("✓ Validation Passed");
            System.out.println("  → Username: " + clientUsername);
            System.out.println("  → Password length: " + clientPassword.length() + " characters");

            // ========== STEP 2: AUTHENTICATE USER ==========
            System.out.println("\n[STEP 1] Authenticating user credentials against database...");
            this.authenticate(clientUsername, clientPassword);
            System.out.println("✓ User authentication successful");

            // ========== STEP 3: LOAD USER DETAILS ==========
            System.out.println("\n[STEP 2] Loading user details from database...");
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(clientUsername);
            System.out.println("✓ User details loaded successfully");
            System.out.println("  → User authorities/roles: " + userDetails.getAuthorities());

            // ========== STEP 4: GENERATE JWT TOKEN ==========
            System.out.println("\n[STEP 3] Generating JWT token...");
            String token = this.jwtTokenHelper.generateToken(userDetails);
            System.out.println("✓ JWT token generated successfully");
            System.out.println("  → Token created at: " + new java.util.Date());
            System.out.println("  → Token length: " + token.length() + " characters");

            // ========== STEP 5: BUILD SUCCESS RESPONSE ==========
            System.out.println("\n[STEP 4] Building success response...");
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            AuthResponse successResponse = new AuthResponse(
                true,                                          // success flag
                "Authentication successful. JWT token generated.",
                token,                                         // JWT token
                clientUsername,                                // authenticated username
                System.currentTimeMillis()                     // response timestamp
            );
            
            System.out.println("✓ Response built successfully");
            System.out.println("\n=== LOGIN COMPLETED SUCCESSFULLY ===");
            System.out.println("  → Total time: " + duration + " ms");
            System.out.println("  → User: " + clientUsername);
            
            return ResponseEntity.ok(successResponse);

        } catch (BadCredentialsException ex) {
            // ❌ EXCEPTION: Password incorrect
            System.out.println("\n❌ AUTHENTICATION FAILED: BadCredentialsException");
            System.out.println("  → User: " + clientUsername);
            System.out.println("  → Reason: Password does not match encrypted password in database");
            System.out.println("  → Possible cause: User entered wrong password");
            
            AuthErrorResponse errorResponse = new AuthErrorResponse(
                false,
                "Authentication Failed",
                "Invalid username or password. Please try again.",
                System.currentTimeMillis()
            );
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);

        } catch (DisabledException ex) {
            // ❌ EXCEPTION: User account disabled
            System.out.println("\n❌ AUTHENTICATION FAILED: DisabledException");
            System.out.println("  → User: " + clientUsername);
            System.out.println("  → Reason: User account is marked as disabled/inactive");
            System.out.println("  → Action needed: Administrator must enable the account");
            
            AuthErrorResponse errorResponse = new AuthErrorResponse(
                false,
                "Account Disabled",
                "Your account is currently disabled. Please contact the administrator.",
                System.currentTimeMillis()
            );
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(errorResponse);

        } catch (UsernameNotFoundException ex) {
            // ❌ EXCEPTION: User not found in database
            System.out.println("\n❌ AUTHENTICATION FAILED: UsernameNotFoundException");
            System.out.println("  → Username/Email attempted: " + clientUsername);
            System.out.println("  → Reason: No user found with this email in database");
            System.out.println("  → Action needed: User must register first");
            
            AuthErrorResponse errorResponse = new AuthErrorResponse(
                false,
                "User Not Found",
                "No account found with this email. Please register first.",
                System.currentTimeMillis()
            );
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorResponse);

        } catch (ExpiredJwtException ex) {
            // ❌ EXCEPTION: JWT token expired (shouldn't happen during login, but handle anyway)
            System.out.println("\n❌ TOKEN VALIDATION FAILED: ExpiredJwtException");
            System.out.println("  → Reason: JWT token has exceeded its expiration time");
            
            AuthErrorResponse errorResponse = new AuthErrorResponse(
                false,
                "Token Expired",
                "Your authentication token has expired. Please login again.",
                System.currentTimeMillis()
            );
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);

        } catch (SignatureException ex) {
            // ❌ EXCEPTION: Token signature invalid
            System.out.println("\n❌ TOKEN VALIDATION FAILED: SignatureException");
            System.out.println("  → Reason: JWT token signature is invalid");
            System.out.println("  → Possible security issue: Token may have been tampered with");
            
            AuthErrorResponse errorResponse = new AuthErrorResponse(
                false,
                "Invalid Token",
                "Token signature is invalid. Possible security breach detected.",
                System.currentTimeMillis()
            );
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);

        } catch (MalformedJwtException ex) {
            // ❌ EXCEPTION: Token format corrupted
            System.out.println("\n❌ TOKEN VALIDATION FAILED: MalformedJwtException");
            System.out.println("  → Reason: JWT token format is corrupted or invalid");
            System.out.println("  → Expected format: Header.Payload.Signature");
            
            AuthErrorResponse errorResponse = new AuthErrorResponse(
                false,
                "Malformed Token",
                "Token format is invalid or corrupted.",
                System.currentTimeMillis()
            );
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse);

        } catch (IllegalArgumentException ex) {
            // ❌ EXCEPTION: Token is empty or null
            System.out.println("\n❌ TOKEN VALIDATION FAILED: IllegalArgumentException");
            System.out.println("  → Reason: JWT token is empty or null");
            System.out.println("  → Expected: Valid token in Authorization header");
            
            AuthErrorResponse errorResponse = new AuthErrorResponse(
                false,
                "Empty Token",
                "Token cannot be empty or null. Please provide a valid token.",
                System.currentTimeMillis()
            );
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse);

        } catch (Exception ex) {
            // ❌ EXCEPTION: Unexpected error
            System.out.println("\n❌ UNEXPECTED ERROR OCCURRED:");
            System.out.println("  → Exception type: " + ex.getClass().getName());
            System.out.println("  → Exception message: " + ex.getMessage());
            System.out.println("  → User attempted: " + clientUsername);
            ex.printStackTrace();
            
            AuthErrorResponse errorResponse = new AuthErrorResponse(
                false,
                "Internal Server Error",
                "An unexpected error occurred. Please contact support.",
                System.currentTimeMillis()
            );
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
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
    /**
     * IMPROVED AUTHENTICATE METHOD
     * 
     * Purpose: 
     * Validates user credentials with comprehensive error handling and detailed logging
     * 
     * Process:
     * 1. Create UsernamePasswordAuthenticationToken with provided credentials
     * 2. Pass token to AuthenticationManager for validation
     * 3. AuthenticationManager internally:
     *    - Loads user from database using CustomUserDetailService
     *    - Compares plain password with encrypted password using BCryptPasswordEncoder
     *    - Verifies user account status (enabled, non-locked, credentials non-expired)
     * 4. If any check fails, throw appropriate SecurityException
     * 5. If all checks pass, authentication is successful (method returns normally)
     * 
     * Security Checks Performed by AuthenticationManager:
     * - Password validation: matches(plainPassword, encryptedPassword)
     * - Account enabled: user.isEnabled() == true
     * - Account non-locked: user.isAccountNonLocked() == true
     * - Account non-expired: user.isAccountNonExpired() == true
     * - Credentials non-expired: user.isCredentialsNonExpired() == true
     *
     * @param username User's email address (used as username)
     * @param password User's password in plain text
     * @throws BadCredentialsException If password does not match database password
     * @throws DisabledException If user account is disabled
     * @throws UsernameNotFoundException If user email not found in database
     * @throws Exception For any other authentication-related errors
     */
    private void authenticate(String username, String password) {
        try {
            // Step 1: Create authentication token (unauthenticated)
            System.out.println("  → Creating authentication token with provided credentials...");
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, password);
            // Token state: authenticated = false (not yet verified)

            // Step 2: Pass to AuthenticationManager for validation
            System.out.println("  → Sending credentials to AuthenticationManager for validation...");
            this.authenticationManager.authenticate(authenticationToken);
            // AuthenticationManager will throw exception if validation fails
            
            // Step 3: If we reach here, authentication was successful
            System.out.println("  → ✓ All security checks passed!");
            System.out.println("  → User account is active and credentials are valid");

        } catch (BadCredentialsException ex) {
            // ❌ EXCEPTION: Password incorrect or user not found by AuthenticationManager
            System.out.println("  → ❌ BadCredentialsException caught");
            System.out.println("     Reason: Password does not match the encrypted password in database");
            System.out.println("     OR: User email was not found (checked before password)");
            throw new BadCredentialsException("Invalid credentials for user: " + username);

        } catch (DisabledException ex) {
            // ❌ EXCEPTION: User account is disabled
            System.out.println("  → ❌ DisabledException caught");
            System.out.println("     Reason: User.isEnabled() returned false");
            System.out.println("     Action: Administrator must enable this account in database");
            throw new DisabledException("User account is disabled: " + username);

        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
            // ❌ EXCEPTION: User not found in database
            System.out.println("  → ❌ UsernameNotFoundException caught (from Spring Security)");
            System.out.println("     Reason: CustomUserDetailService could not find user by email");
            System.out.println("     Action: User must register first or use correct email");
            throw new UsernameNotFoundException("User", "email", username);

        } catch (Exception ex) {
            // ❌ EXCEPTION: Other authentication errors
            System.out.println("  → ❌ Unexpected exception caught during authentication:");
            System.out.println("     Exception type: " + ex.getClass().getSimpleName());
            System.out.println("     Message: " + ex.getMessage());
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
