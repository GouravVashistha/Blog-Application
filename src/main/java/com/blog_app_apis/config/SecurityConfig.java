package com.blog_app_apis.config;

import com.blog_app_apis.security.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig Class
 * Purpose: Configures Spring Security settings for the Blog App API
 * Defines authentication and authorization rules for all HTTP requests
 */
@Configuration  // Indicates this class contains Spring configuration beans
@EnableWebSecurity  // Enables Spring Security web security features for this application
public class SecurityConfig {

    /**
     * SecurityFilterChain Bean
     * Purpose: Creates and returns a security filter chain that defines how HTTP requests should be secured
     *
     * @param http - HttpSecurity object used to configure security settings
     * @return - SecurityFilterChain with configured security rules
     * @throws Exception - If any error occurs during security configuration
     */

    @Autowired
    private CustomUserDetailService customUserDetailService;

    /**
     * SecurityFilterChain Bean (Spring Security 6.0+ approach)
     * Purpose: Creates and returns a security filter chain with HTTP Basic authentication
     * Uses CustomUserDetailService for loading user details from database
     *
     * @param http - HttpSecurity object used to configure security settings
     * @return - SecurityFilterChain with configured security rules
     * @throws Exception - If any error occurs during security configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection (REST APIs don't need it)
                .csrf(AbstractHttpConfigurer::disable)
                // Authorize all HTTP requests
                .authorizeHttpRequests(auth -> auth
                        .anyRequest()
                        .authenticated()  // All requests require authentication
                )
                // Enable HTTP Basic authentication
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }


//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(this.customUserDetailService).passwordEncoder(passwordEncoder());
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
// CSRF (Cross-Site Request Forgery) Protection Disabled
// WHY: REST APIs don't need CSRF protection because:
//      1. They use stateless authentication (JWT or HTTP Basic)
//      2. Clients are not browsers, so CSRF attacks are not applicable
//      3. Mobile apps and Postman requests won't have CSRF tokens
//      4. CSRF is mainly for form-based web applications, not APIs


// Authorization Configuration for HTTP Requests
// Purpose: Define which URLs require authentication and which are public
// We're using the new lambda-based configuration (Spring Security 6.0+)
// anyRequest() - Applies to ALL HTTP requests (any URL path)
// WHY: We want to secure the entire API by default

// authenticated() - Requires user to be authenticated/logged in
// WHY: Only authorized users should access the API
//      Users must provide valid username and password

// HTTP Basic Authentication Configuration
// Purpose: Enable username and password-based authentication
// HOW IT WORKS:
//   1. Client sends Authorization header: Authorization: Basic base64(username:password)
//   2. Server decodes and validates credentials
//   3. If valid → Access granted; If invalid → 401 Unauthorized response
// WHY USE IT:
//   1. Simple to implement for REST APIs
//   2. No session required (stateless)
//   3. Works well with Postman and curl commands
//   4. Good for testing and basic authentication
// Customizer.withDefaults() - Uses Spring's default HTTP Basic settings

// Build and return the configured SecurityFilterChain
// This chain will be applied to all HTTP requests in the application
