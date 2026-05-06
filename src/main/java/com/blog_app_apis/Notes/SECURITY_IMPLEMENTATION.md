# Blog App API - Security Implementation Guide
## Status: Complete Documentation
---

# TABLE OF CONTENTS
1. [Overview](#overview)
2. [Security Architecture](#security-architecture)
3. [Class-by-Class Explanation](#class-by-class-explanation)
4. [Complete Authentication Flow](#complete-authentication-flow)
5. [JWT Token Structure](#jwt-token-structure)
6. [Method Wise Explanation](#method-wise-explanation)
7. [Configuration Details](#configuration-details)
8. [Exception Handling](#exception-handling)

---

# OVERVIEW

## Security Architecture Kya Hai?

Hamara Blog App API mein **JWT (JSON Web Token) based stateless authentication** use ki gayi hai. Iska matlab:

- **Stateless** = Server ko user session store karne ki zaroorat nahi
- **JWT** = Ek secured token jo client ko diya jaata hai
- **Token Based** = Har request mein client token bhejta hai

### Kyu JWT Use Karte Hain?

1. **Scalability**: Ek server se multiple servers tak kam kar sakta hai
2. **Mobile Friendly**: Mobile apps, Postman, etc. sab use kar sakte hain
3. **Secure**: Token ko sign kiya jaata hai aur tamper-proof hota hai
4. **Stateless**: DB mein session store karne ki zaroorat nahi

---

# SECURITY ARCHITECTURE

## Diagrammatic Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                   CLIENT (Browser/Postman/Mobile)               │
└──────────────────────────────┬──────────────────────────────────┘
                                │
                    1. LOGIN REQUEST (Username, Password)
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    REST API SERVER                              │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │            SECURITY LAYER (Spring Security)             │   │
│  │                                                          │   │
│  │  • Requests ko intercept karta hai                       │   │
│  │  • Authentication check karta hai                        │   │
│  │  • Authorization verify karta hai                        │   │
│  └──────────────────┬──────────────────────────────────────┘   │
│                     │                                           │
│  2. AuthController  │                                           │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ @PostMapping("/auth/login")                           │    │
│  │                                                        │    │
│  │ Body: {                                               │    │
│  │   "username": "gvashistha@gmail.com",                 │    │
│  │   "password": "admin"                                 │    │
│  │ }                                                      │    │
│  └──────────┬───────────────────────────────────────────┘    │
│             │                                                 │
│             ▼                                                 │
│  3. authenticate() Method call                                │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ AuthenticationManager.authenticate()                   │   │
│  │                                                        │   │
│  │ -> UsernamePasswordAuthenticationToken banata hai      │   │
│  │ -> Credentials ko validate karta hai                  │   │
│  └──────────┬───────────────────────────────────────────┘    │
│             │                                                  │
│             ▼                                                  │
│  4. CustomUserDetailService                                    │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ loadUserByUsername(email)                              │   │
│  │                                                        │   │
│  │ -> Database mein user ko search karta hai             │   │
│  │ -> UserDetails object return karta hai                │   │
│  └──────────┬───────────────────────────────────────────┘    │
│             │                                                  │
│             ▼                                                  │
│  5. PasswordEncoder Validation                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ BCryptPasswordEncoder                                  │   │
│  │                                                        │   │
│  │ -> Client ka plain password                           │   │
│  │ -> Database ka encrypted password                     │   │
│  │ -> Dono ko compare karta hai                          │   │
│  └──────────┬───────────────────────────────────────────┘    │
│             │                                                  │
│             ├─────────► Password Match? ◄──────┐             │
│             │                                   │             │
│        YES  │                                   │ NO (Bad Credentials)
│             │                                   │             │
│             ▼                                   ▼             │
│  6. JWT Token Generate            ❌ Exception Throw         │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ JwtTokenHelper.generateToken()                         │   │
│  │                                                        │   │
│  │ -> Token banata hai:                                  │   │
│  │   • Header (alg: HS256, typ: JWT)                     │   │
│  │   • Payload (sub: email, iat, exp)                    │   │
│  │   • Signature (HMAC-SHA256)                           │   │
│  └──────────┬───────────────────────────────────────────┘    │
│             │                                                  │
│             ▼                                                  │
│  7. Response Send                                              │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ HTTP 200 OK                                            │   │
│  │ {                                                      │   │
│  │   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI..."           │   │
│  │ }                                                      │   │
│  └────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────┬──────────────────────────┘
                                      │
                    CLIENT TOKEN STORE KARTA HAI
                                      │
                    ▼
         8. NEXT REQUEST WITH TOKEN
         Authorization Header:
         Bearer eyJhbGciOiJIUzI1NiIsInR5cCI...
                                      │
                    ▼
         Springs Security Filter Chain
         Token Validate Karta Hai
```

---

# CLASS-BY-CLASS EXPLANATION

## 1. **JwtAuthRequest.java**
### Location: `com.blog_app_apis.Entity`

```java
@Data
public class JwtAuthRequest {
    private String username;    // Email address of user
    private String password;    // Plain text password
}
```

### Purpose:
- Login request ko capture karne ke liye
- Client isse username aur password send karta hai

### Use Case:
```
Postman se request:
POST /api/v1/auth/login
{
  "username": "gvashistha@gmail.com",
  "password": "admin"
}
```

### Kaise Use Hota Hai:
```java
// AuthController mein
@PostMapping("/login")
public ResponseEntity<?> createToken(@RequestBody JwtAuthRequest request) {
    // request.getUsername() -> "gvashistha@gmail.com"
    // request.getPassword() -> "admin"
}
```

---

## 2. **JwtAuthResponce.java**
### Location: `com.blog_app_apis.Entity`

```java
@Data
public class JwtAuthResponce {
    private String token;  // JWT token
}
```

### Purpose:
- Success response mein JWT token send karna

### Response Example:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJndmFzaGlzdGhhQGdtYWlsLmNvbSIsImV4cCI6MTcxNDkyMzQ3OCwiaWF0IjoxNzE0OTA1NDc4fQ.xxx"
}
```

---

## 3. **User.java (Entity)**
### Location: `com.blog_app_apis.Entity`

```java
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    private Long id;
    
    @Column(name = "user_email", unique = true)
    private String email;
    
    private String password;  // Encrypted password (BCrypt)
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role")
    private Set<Role> roles;  // User ke roles
}
```

### Why UserDetails Implement Karte Hain?

Spring Security ko UserDetails interface chahiye jo:
- `getUsername()` return kare
- `getPassword()` return kare
- `getAuthorities()` return kare
- Account status return kare

### Key Methods:

```java
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    // User ke sab roles ko authorities mein convert karte hain
    return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .toList();
    // Example: ROLE_USER, ROLE_ADMIN
}

@Override
public String getUsername() {
    return this.email;  // Email ko username ke roop mein use karte hain
}

@Override
public String getPassword() {
    return this.password;  // Encrypted password return karta hai
}

@Override
public boolean isEnabled() {
    return true;  // Account active hai ya nahi
}

@Override
public boolean isAccountNonExpired() {
    return true;  // Account expire to nahi hua
}

@Override
public boolean isAccountNonLocked() {
    return true;  // Account lock to nahi hua
}

@Override
public boolean isCredentialsNonExpired() {
    return true;  // Credentials expire to nahi huey
}
```

---

## 4. **Role.java (Entity)**
### Location: `com.blog_app_apis.Entity`

```java
@Entity
public class Role {
    @Id
    private int id;
    private String name;  // "ROLE_USER", "ROLE_ADMIN"
}
```

### Purpose:
- User ke permissions define karte hain
- Har user ke pas ek ya multiple roles ho sakte hain

### Database Table:
```sql
-- roles table
id | name
1  | ROLE_USER
2  | ROLE_ADMIN

-- user_role table (Many-to-Many)
user_id | role_id
100     | 1
101     | 2
```

---

## 5. **JwtTokenHelper.java**
### Location: `com.blog_app_apis.security`

### Ye Class Ka Kaam:
```
Token Generate Karna → ✅
Token Validate Karna → ✅
Claims Extract Karna → ✅
```

### Key Variables:

```java
@Value("${app.jwt.secret}")
private String secret;  // Secret key (application.properties se ata hai)

@Value("${app.jwt.expiration.ms}")
private long jwtTokenValidity;  // Token kitne time ke liye valid (5 hours)
```

### application.properties:
```properties
app.jwt.secret=mySuperSecretKeyForJwtAuthentication123456SecretKey
app.jwt.expiration.ms=18000000  # 5 hours in milliseconds
```

### Important Methods:

#### a) **getSigningKey()**
```java
private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
}
```

**Kya Karta Hai:**
- Secret string ko Key object mein convert karta hai
- HS256 (HMAC-SHA256) algorithm ke liye key banata hai

#### b) **generateToken(UserDetails userDetails)**
```java
public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return doGenerateToken(claims, userDetails.getUsername());
}
```

**Kya Karta Hai:**
- UserDetails se username leta hai
- Token banane ka process shuru karta hai

#### c) **doGenerateToken(Map claims, String subject)**
```java
private String doGenerateToken(Map<String, Object> claims, String subject) {
    Date now = new Date(System.currentTimeMillis());
    Date expiration = new Date(System.currentTimeMillis() + jwtTokenValidity);
    
    return Jwts.builder()
            .claims(claims)
            .subject(subject)              // Email set karta hai
            .issuedAt(now)                 // Kab banaya gya
            .expiration(expiration)        // Kab expire hoga
            .signWith(getSigningKey())     // Secret se sign karta hai
            .compact();                    // Final token string
}
```

**Token Structure:**
```
Header.Payload.Signature

Header: {
  "alg": "HS256",
  "typ": "JWT"
}

Payload: {
  "sub": "gvashistha@gmail.com",
  "iat": 1714905478,
  "exp": 1714923478
}

Signature: HMAC-SHA256(Header.Payload, secret)
```

#### d) **getUsernameFromToken(String token)**
```java
public String getUsernameFromToken(String token) {
    return getClaimFromToken(token, Claims::getSubject);
}
```

**Kya Karta Hai:**
- Token se email/username extract karta hai

#### e) **validateToken(String token, UserDetails userDetails)**
```java
public Boolean validateToken(String token, UserDetails userDetails) {
    try {
        String username = getUsernameFromToken(token);
        
        if (!username.equals(userDetails.getUsername())) {
            return false;  // Username match nahi hua
        }
        
        if (isTokenExpired(token)) {
            return false;  // Token expire ho gya
        }
        
        return true;  // Token valid hai
    } catch (Exception ex) {
        return false;  // Token tamper hua ya corrupt hai
    }
}
```

**Validation Process:**
1. Token ko signature se verify karta hai
2. Username ko check karta hai
3. Expiration time ko check karta hai

---

## 6. **CustomUserDetailService.java**
### Location: `com.blog_app_apis.security`

```java
@Service
public class CustomUserDetailService implements UserDetailsService {
    
    @Autowired
    private UserRepo userRepo;
    
    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        // Database se user ko email se find karte hain
        User user = this.userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "User not found with email: " + username
                ));
        return user;  // User entity jo UserDetails implement karta hai
    }
}
```

### Ye Service Ka Kaam:

```
Email → Database Query → User Entity → Return (as UserDetails)
```

### Kaise Use Hota Hai:

```java
// AuthenticationManager ye service call karta hai
UserDetails userDetails = userDetailsService.loadUserByUsername("gvashistha@gmail.com");

// Ab hamere paas User object hai:
// - userDetails.getUsername() -> "gvashistha@gmail.com"
// - userDetails.getPassword() -> "$2a$10$..." (encrypted)
// - userDetails.getAuthorities() -> [ROLE_USER]
```

---

## 7. **SecurityConfig.java**
### Location: `com.blog_app_apis.config`

### Overview:
Ye class pura Spring Security configure karta hai.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailService customUserDetailService;
    
    // 1. SecurityFilterChain Bean
    // 2. PasswordEncoder Bean
    // 3. AuthenticationManager Bean (automatically created)
}
```

### Main Responsible:

#### a) **securityFilterChain(HttpSecurity http)**
```java
public SecurityFilterChain securityFilterChain(HttpSecurity http) 
        throws Exception {
    
    http
        .csrf(AbstractHttpConfigurer::disable)  // CSRF disable kar diya
        .authorizeHttpRequests(auth -> auth
            .anyRequest()              // Sab requests ke liye
            .authenticated()            // Authentication required hai
        )
        .httpBasic(Customizer.withDefaults());  // HTTP Basic auth enable
    
    return http.build();
}
```

**Iska Matlab:**
- Sab requests ko authenticate karna zaruri hai
- CSRF protection disable hai (REST APIs ke liye normal hai)
- HTTP Basic auth use kare rahe hain

#### b) **configure(AuthenticationManagerBuilder auth)**
```java
protected void configure(AuthenticationManagerBuilder auth) 
        throws Exception {
    
    auth.userDetailsService(this.customUserDetailService)
        .passwordEncoder(passwordEncoder());  // BCrypt password encoder
}
```

**Ye Batata Hai:**
- UserDetails ko CustomUserDetailService se lao
- Password ko BCrypt se encrypt/decrypt karo

#### c) **passwordEncoder()**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**BCrypt Kya Hai:**
- Password ko securely encrypt karta hai
- Same password har baar different hash generate karta hai
- Brute force attack se protect karta hai

**Example:**
```
Plain Password: "admin"

BCrypt Encryption Round 1: $2a$10$abcdefghijklmnopqrstuvwxyz123
BCrypt Encryption Round 2: $2a$10$different123456789

Validation Time:
Input "admin" + Stored BCrypt Hash -> Match?
Haan -> Access dena
Nahi -> Access deny karna
```

---

## 8. **AuthController.java**
### Location: `com.blog_app_apis.controllers`

### Ye Controller Mein 3 Main Endpoints Hain:

#### Endpoint 1: **POST /api/v1/auth/login**

```java
@PostMapping("/login")
public ResponseEntity<?> createToken(@RequestBody JwtAuthRequest request) {
    
    // Step 1: Input validation
    if (request == null || request.getUsername() == null) {
        return BAD_REQUEST;
    }
    
    // Step 2: Authenticate using AuthenticationManager
    this.authenticate(request.getUsername(), request.getPassword());
    
    // Step 3: Load user details
    UserDetails userDetails = this.userDetailsService
            .loadUserByUsername(request.getUsername());
    
    // Step 4: Generate JWT token
    String token = this.jwtTokenHelper.generateToken(userDetails);
    
    // Step 5: Prepare response
    JwtAuthResponce response = new JwtAuthResponce();
    response.setToken(token);
    
    // Step 6: Return response
    return ResponseEntity.ok(response);
}
```

**Request Flow:**
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "gvashistha@gmail.com",
  "password": "admin"
}

                    ↓

1. authenticate() ko call kare:
   - UsernamePasswordAuthenticationToken banate hain
   - AuthenticationManager se validate karte hain
   - Password match ho to aage badte hain
   - Match na ho to BadCredentialsException throw hota hai

2. CustomUserDetailService call hota hai:
   - userRepo.findByEmail(username) 
   - User entity database se aata hai

3. PasswordEncoder validate karta hai:
   - Input password (plain) + DB password (encrypted)
   - Match? -> Token generate karo
   - Not Match? -> Exception throw karo

4. JwtTokenHelper ko call karte hain:
   - token = generateToken(userDetails)
   - Token mein username aur expiry time hota hai

5. Response send hota hai:
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
```

**Status Codes:**
```
200 OK -> Login successful, token generated
400 BAD_REQUEST -> Invalid input
401 UNAUTHORIZED -> Wrong password
404 NOT_FOUND -> User not found
403 FORBIDDEN -> Account disabled
500 INTERNAL_SERVER_ERROR -> Server error
```

---

## 9. **Exception Handling**

### AuthController mein 8 Exception Catch Hote Hain:

#### 1. **BadCredentialsException**
```
Scenario: Wrong password
HTTP Status: 401 UNAUTHORIZED
Response: "Authentication failed: Invalid username or password"
```

#### 2. **DisabledException**
```
Scenario: User account disabled
HTTP Status: 403 FORBIDDEN
Response: "User account is disabled. Contact administrator"
```

#### 3. **UsernameNotFoundException** (Custom)
```
Scenario: User email not found in database
HTTP Status: 404 NOT_FOUND
Response: "User account not found. Please register first"
```

#### 4. **ExpiredJwtException**
```
Scenario: JWT token ka expiry time pass ho gya
HTTP Status: 401 UNAUTHORIZED
Response: "Token has expired. Please login again"
```

#### 5. **SignatureException**
```
Scenario: Token ka signature tamper hua
HTTP Status: 401 UNAUTHORIZED
Response: "Invalid token signature. May have been tampered"
```

#### 6. **MalformedJwtException**
```
Scenario: Token ka structure galat hai
HTTP Status: 400 BAD_REQUEST
Response: "Invalid token format. Structure corrupted"
```

#### 7. **IllegalArgumentException**
```
Scenario: Token empty ya null hai
HTTP Status: 400 BAD_REQUEST
Response: "Token cannot be empty"
```

#### 8. **General Exception**
```
Scenario: Koi aur unexpected error
HTTP Status: 500 INTERNAL_SERVER_ERROR
Response: "Unexpected error occurred"
```

---

# COMPLETE AUTHENTICATION FLOW

## Step-by-Step Process (Detailed)

### **PHASE 1: USER LOGIN REQUEST**

```
User/Client
    │
    ├─> Postman/Browser/Mobile App
    │
    ├─> POST /api/v1/auth/login
    │
    ├─> Body:
    │   {
    │     "username": "gvashistha@gmail.com",
    │     "password": "admin"
    │   }
    │
    └─> Server ko request bhej deta hai
```

### **PHASE 2: REQUEST RECEIVED BY AUTHCONTROLLER**

```
AuthController.createToken() mein aata hai:

Step 1: Input Validation
        ├─ request null? -> 400 BAD_REQUEST
        ├─ username null? -> 400 BAD_REQUEST
        └─ password empty? -> 400 BAD_REQUEST

Step 2: authenticate() Method Call
        └─ UsernamePasswordAuthenticationToken create hota hai
           {
             principal: "gvashistha@gmail.com",
             credentials: "admin",
             authenticated: false
           }
```

### **PHASE 3: AUTHENTICATIONMANAGER VALIDATES**

```
AuthenticationManager.authenticate(token)
    │
    └─> DaoAuthenticationProvider
        │
        ├─ 1. CustomUserDetailService.loadUserByUsername() call
        │      └─> userRepo.findByEmail(username)
        │          ├─ Database Query:
        │          │  SELECT * FROM users WHERE user_email = ?
        │          │
        │          ├─ Response:
        │          │  User {
        │          │    id: 100,
        │          │    email: "gvashistha@gmail.com",
        │          │    name: "Gourav Vashistha",
        │          │    password: "$2a$10$...", (encrypted)
        │          │    roles: [ROLE_USER]
        │          │  }
        │          │
        │          └─> Exception? -> UsernameNotFoundException
        │
        ├─ 2. PasswordEncoder.matches() call
        │      └─> BCryptPasswordEncoder
        │          ├─ Input Password: "admin"
        │          ├─ Encrypted Password: "$2a$10$..." (database se)
        │          │
        │          ├─ bcrypt.matches(input, stored)
        │          │  Result: true/false
        │          │
        │          ├─ If true -> Continue
        │          └─ If false -> BadCredentialsException
        │
        └─ 3. Authority Check
            ├─ user.getAuthorities() -> [SimpleGrantedAuthority("ROLE_USER")]
            └─ All checks pass -> Authenticated token return
```

### **PHASE 4: TOKEN GENERATION**

```
JwtTokenHelper.generateToken(userDetails)
    │
    ├─ Step 1: Prepare Token Payload
    │   {
    │     sub: "gvashistha@gmail.com",  (subject - username)
    │     iat: 1714905478,               (issued at time)
    │     exp: 1714923478,               (expiration time)
    │     custom_claims: {}              (agar ho to)
    │   }
    │
    ├─ Step 2: Create Header
    │   {
    │     alg: "HS256",
    │     typ: "JWT"
    │   }
    │
    ├─ Step 3: Combine Header + Payload
    │   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
    │   +
    │   eyJzdWIiOiJndmFzaGlzdGhhQGdtYWlsLmNvbSIsImV4cCI6MTcxNDkyMzQ3OCwiaWF0IjoxNzE0OTA1NDc4fQ
    │
    ├─ Step 4: Create Signature
    │   Signature = HMAC-SHA256(
    │                 Header.Payload,
    │                 secret_key
    │               )
    │   = 1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s
    │
    └─ Step 5: Final Token
        eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
        eyJzdWIiOiJndmFzaGlzdGhhQGdtYWlsLmNvbSIsImV4cCI6MTcxNDkyMzQ3OCwiaWF
        1hdCI6MTcxNDkwNTQ3OH0.
        1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s
```

### **PHASE 5: RESPONSE SENT TO CLIENT**

```
HTTP 200 OK
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJndmFzaGlzdGhhQGdtYWlsLmNvbSIsImV4cCI6MTcxNDkyMzQ3OCwiaWF0IjoxNzE0OTA1NDc4fQ.1a2b3c4d..."
}

Client ye token apne paas store kar leta hai:
├─ Browser -> localStorage/sessionStorage
├─ Mobile -> SharedPreferences/Keychain
└─ Postman -> Environment variables
```

### **PHASE 6: NEXT REQUEST WITH TOKEN**

```
Client phat se ya naya request karta hai:

GET /api/v1/posts
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

                    ↓

Server ke paas request aata hai

                    ↓

Spring Security ye token check karta hai (JWT Filter):

Step 1: Authorization Header extract karo
        Authorization: Bearer TOKEN
        └─> Bearer keyword hatao, token get karo

Step 2: Token ko validate karo
        JwtTokenHelper.validateToken(token, userDetails)
        │
        ├─ Signature match karte ho?
        │  ├─ Secret key se signature verify karo
        │  ├─ Match? -> Continue
        │  └─ Not Match? -> SignatureException
        │
        ├─ Token expire to nahi hua?
        │  ├─ Current time vs Expiry time
        │  ├─ Valid? -> Continue
        │  └─ Expired? -> ExpiredJwtException
        │
        └─ Username match karte ho?
           ├─ Token mein username
           ├─ Current user username
           └─ Match? -> Token valid

Step 3: Token valid hai to request process karo
        └─> User ko access mila

Step 4: Token invalid hai to reject karo
        └─ 401 UNAUTHORIZED
        └─ Request process mat karo
```

---

# JWT TOKEN STRUCTURE

## Token Anatomy

```
JWT Token = Header.Payload.Signature

┌─────────────────────────────────────────────────────────────┐
│ HEADER (Base64 Encoded)                                     │
├─────────────────────────────────────────────────────────────┤
│ {                                                            │
│   "alg": "HS256",    // Jo algorithm use huyi token sign   │
│   "typ": "JWT"       // Token ki type                        │
│ }                                                            │
│                                                              │
│ Encoded: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ PAYLOAD (Base64 Encoded) - Claims contain karte hain        │
├─────────────────────────────────────────────────────────────┤
│ {                                                            │
│   "sub": "gvashistha@gmail.com",  // Subject (username)    │
│   "iat": 1714905478,               // Issued at (timestamp) │
│   "exp": 1714923478,               // Expiration (timestamp) │
│   "custom_field": "value"          // Custom claims (optional)
│ }                                                            │
│                                                              │
│ Encoded:                                                     │
│ eyJzdWIiOiJndmFzaGlzdGhhQGdtYWlsLmNvbSIsImV4cCI6...      │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ SIGNATURE (Base64 Encoded)                                  │
├─────────────────────────────────────────────────────────────┤
│ HMAC-SHA256(                                                 │
│   base64(header) + "." + base64(payload),                   │
│   secret_key                                                 │
│ )                                                            │
│                                                              │
│ = 1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s                   │
│                                                              │
│ Purpose:                                                     │
│ ├─ Token verify karte hain                                 │
│ ├─ Tampering detect karte hain                             │
│ └─ Authenticity ensure karte hain                          │
└─────────────────────────────────────────────────────────────┘

                              │
                              ▼
Full Token:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
.
eyJzdWIiOiJndmFzaGlzdGhhQGdtYWlsLmNvbSIsImV4cCI6MTcxNDkyMzQ3OCwiaWF0IjoxNzE0OTA1NDc4fQ
.
1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s
```

## Token Decode Kaise Hota Hai?

```
Client ke paas: eyJhbGc...

                    ↓

1. Token ko "." se split karo:
   part1: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
   part2: eyJzdWIiOiJndmFzaGlzdGhhQGdtYWlsLmNvbSIs...
   part3: 1a2b3c4d5e6f...

                    ↓

2. Part1 + Part2 ko Base64 decode karo:
   Header: {"alg": "HS256", "typ": "JWT"}
   Payload: {"sub": "gvashistha@gmail.com", "exp": 1714923478, ...}

                    ↓

3. Server se Secret key lao

                    ↓

4. HMAC-SHA256(part1.part2, secret) calculate karo

                    ↓

5. Calculated signature == part3?
   
   Haan    -> Token valid hai ✅
   Nahi    -> Token tamper hua ❌
```

---

# METHOD WISE EXPLANATION

## AuthController Methods

### 1. **createToken() - Main Login Method**

```java
@PostMapping("/login")
public ResponseEntity<?> createToken(@RequestBody JwtAuthRequest request) {
    
    try {
        // STEP 1: Validate Input
        if (request == null || request.getUsername() == null 
            || request.getPassword() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username and password required");
        }
        
        if (request.getUsername().trim().isEmpty() 
            || request.getPassword().trim().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Fields cannot be empty");
        }
        
        // STEP 2: Call authenticate method
        // Ye AuthenticationManager ko use karta hai
        // AuthenticationManager CustomUserDetailService aur 
        // PasswordEncoder dono use karta hai
        this.authenticate(request.getUsername(), request.getPassword());
        
        // Agar authenticate() successful hai to ye line execute hoga
        System.out.println("Successfully authenticated user: " + 
                          request.getUsername());
        
        // STEP 3: Load user details from database
        // CustomUserDetailService se user ko fetch karte hain
        UserDetails userDetails = this.userDetailsService
                .loadUserByUsername(request.getUsername());
        
        // Ab hamere paas User entity hai jisme:
        // - Username (email)
        // - Password (encrypted)
        // - Roles/Authorities (ROLE_USER, ROLE_ADMIN)
        // - Account status (enabled, locked, etc.)
        
        // STEP 4: Generate JWT token
        // JwtTokenHelper ko userDetails dete hain
        // Token mein username, issued time, expiry time hota hai
        String token = this.jwtTokenHelper.generateToken(userDetails);
        
        System.out.println("JWT token generated for: " + 
                          request.getUsername());
        
        // STEP 5: Create response object
        JwtAuthResponce response = new JwtAuthResponce();
        response.setToken(token);
        
        // STEP 6: Return 200 OK with token
        return ResponseEntity.ok(response);
        
    } catch (BadCredentialsException ex) {
        // Password galat hai
        System.out.println("Bad credentials: " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Invalid credentials");
                
    } catch (DisabledException ex) {
        // User account disabled hai
        System.out.println("Account disabled: " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Account disabled");
                
    } catch (UsernameNotFoundException ex) {
        // User database mein nahi hai
        System.out.println("User not found: " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("User not found");
                
    } catch (Exception ex) {
        // Koi aur error
        System.out.println("Unexpected error: " + ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + ex.getMessage());
    }
}
```

### 2. **authenticate() - Private Method**

```java
private void authenticate(String username, String password) {
    
    try {
        // STEP 1: Create authentication token
        // Ye token unverified credentials rakhta hai
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    username,     // Email address
                    password      // Plain text password
                );
        
        // STEP 2: Pass token to AuthenticationManager
        // AuthenticationManager internally:
        // a) CustomUserDetailService se user load karte hain
        // b) PasswordEncoder se password verify karte hain
        // c) Account status check karte hain
        this.authenticationManager.authenticate(authenticationToken);
        
        // Agar kuch galat nahi hua to ye line ke baad successfully
        // authenticated maana jaata hai
        System.out.println("User authentication successful: " + username);
        
    } catch (BadCredentialsException ex) {
        // Password match nahi hua
        System.out.println("Bad credentials for user: " + username);
        throw new BadCredentialsException(
            "Invalid password for user: " + username
        );
        
    } catch (DisabledException ex) {
        // Account disabled hai
        System.out.println("Account disabled: " + username);
        throw new DisabledException(
            "User account is disabled: " + username
        );
        
    } catch (org.springframework.security.core.userdetails.UsernameNotFoundException ex) {
        // User database mein nahi mila
        System.out.println("User not found: " + username);
        
        // Spring Security ka exception ko apne custom exception mein
        // convert kar diya
        throw new UsernameNotFoundException(
            "User", "email", username
        );
        
    } catch (Exception ex) {
        // Koi aur unexpected error
        System.out.println("Authentication error: " + 
                          ex.getClass().getName() + " - " + 
                          ex.getMessage());
        throw ex;
    }
}
```

### 3. **registerUser() - Registration Method**

```java
@PostMapping("/register")
public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
    
    // STEP 1: UserService ko userDTO dete hain
    // UserService mein:
    // - Input validation hota hai
    // - Email already exist check hota hai
    // - Password ko BCrypt se encrypt kiya jaata hai
    // - User database mein save hota hai
    UserDTO registeredUser = this.userService.registerNewUser(userDTO);
    
    // STEP 2: HTTP 201 CREATED status dete hain
    // 201 = Resource successfully created
    return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
}
```

---

## JwtTokenHelper Methods

### 1. **generateToken() - Token Create Karna**

```java
public String generateToken(UserDetails userDetails) {
    
    // Empty claims map banate hain
    // Isme aap custom claims add kar sakte ho
    Map<String, Object> claims = new HashMap<>();
    
    // Custom claims add kar sakte ho:
    // claims.put("role", userDetails.getAuthorities());
    // claims.put("fullName", user.getName());
    // claims.put("dept", "Engineering");
    
    // doGenerateToken ko claims aur username pass karte hain
    return doGenerateToken(claims, userDetails.getUsername());
}
```

### 2. **doGenerateToken() - Actual Token Builder**

```java
private String doGenerateToken(Map<String, Object> claims, String subject) {
    
    // Current time
    Date now = new Date(System.currentTimeMillis());
    
    // Expiration time = current time + 5 hours (18000000 ms)
    Date expiration = new Date(System.currentTimeMillis() + jwtTokenValidity);
    
    // Token builder start karte hain
    return Jwts.builder()
            .claims(claims)              // Custom claims set karte hain
            .subject(subject)            // Subject = email/username
            .issuedAt(now)               // Kab token banaya gya
            .expiration(expiration)      // Kab token expire hoga
            .signWith(getSigningKey())   // Secret key se sign karte hain
            .compact();                  // Token string mein convert karte hain
}
```

### 3. **validateToken() - Token Verify Karna**

```java
public Boolean validateToken(String token, UserDetails userDetails) {
    
    try {
        // Token se username extract karte hain
        String username = getUsernameFromToken(token);
        
        // Check 1: Username match karte ho?
        if (!username.equals(userDetails.getUsername())) {
            System.out.println("Username mismatch");
            return false;
        }
        
        // Check 2: Token expire to nahi hua?
        if (isTokenExpired(token)) {
            System.out.println("Token expired");
            return false;
        }
        
        // Sab checks pass -> Token valid hai
        System.out.println("Token valid for user: " + username);
        return true;
        
    } catch (ExpiredJwtException ex) {
        // Token ka expiry time pass ho gya
        System.out.println("Token Expired");
        return false;
        
    } catch (SignatureException ex) {
        // Token ka signature tamper hua
        System.out.println("Invalid signature");
        return false;
        
    } catch (MalformedJwtException ex) {
        // Token ka structure galat hai
        System.out.println("Malformed token");
        return false;
        
    } catch (IllegalArgumentException ex) {
        // Token empty hai
        System.out.println("Empty token");
        return false;
        
    } catch (Exception ex) {
        // Koi aur error
        System.out.println("Token validation error: " + ex.getMessage());
        return false;
    }
}
```

### 4. **getUsernameFromToken() - Email Extract Karna**

```java
public String getUsernameFromToken(String token) {
    // Function pass karte hain jo Claims se subject nikal le
    // Subject hi username/email hai ahmare case mein
    return getClaimFromToken(token, Claims::getSubject);
}
```

### 5. **getClaimFromToken() - Generic Claim Extraction**

```java
public <T> T getClaimFromToken(String token, 
                                Function<Claims, T> claimsResolver) {
    
    // Token se sab claims get karte hain
    final Claims claims = getAllClaimsFromToken(token);
    
    // claimsResolver function ko use karke specific claim nikalta hai
    // Example:
    // - getClaimFromToken(token, Claims::getSubject) -> username
    // - getClaimFromToken(token, Claims::getExpiration) -> expiry time
    return claimsResolver.apply(claims);
}
```

### 6. **getAllClaimsFromToken() - Token Parse Karna**

```java
private Claims getAllClaimsFromToken(String token) {
    
    try {
        // JWT Parser banate hain
        return Jwts.parser()
                // Secret key se verify karte hain
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                // Token ko parse karte hain aur claims nikalta hai
                .parseSignedClaims(token)
                .getPayload();  // Payload return karte hain
                
    } catch (ExpiredJwtException ex) {
        System.out.println("Token expired");
        throw ex;
        
    } catch (Exception ex) {
        System.out.println("Invalid token: " + ex.getMessage());
        throw ex;
    }
}
```

### 7. **isTokenExpired() - Expiry Check Karna**

```java
public Boolean isTokenExpired(String token) {
    
    try {
        // Token se expiration date get karte hain
        final Date expiration = getExpirationDateFromToken(token);
        
        // Agar expiration null hai (kabhi set nahi kiya to)
        if (expiration == null) {
            return false;  // Never expires
        }
        
        // Current date se compare karte hain
        // expiration.before(now) = kya expiration date past mein hai?
        return expiration.before(new Date());
        
    } catch (ExpiredJwtException ex) {
        // Token pehle se hi expire ho gya
        return true;
    }
}
```

---

## CustomUserDetailService Method

### **loadUserByUsername() - User Load Karna**

```java
@Override
public UserDetails loadUserByUsername(String username) 
        throws UsernameNotFoundException {
    
    // Username/email se database mein search karte hain
    User user = this.userRepo.findByEmail(username)
            // Agar user mil gya to return karte hain
            // Agar nahi mila to exception throw karte hain
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found with email: " + username
            ));
    
    // User entity jo UserDetails implement karta hai,
    // woh return hota hai
    return user;
    
    // Ab Spring Security ko User entity milne se:
    // - user.getUsername() -> email
    // - user.getPassword() -> encrypted password
    // - user.getAuthorities() -> roles
    // - user.isEnabled() -> account status
}
```

---

# CONFIGURATION DETAILS

## application.properties

```properties
# JWT Configuration
app.jwt.secret=mySuperSecretKeyForJwtAuthentication123456SecretKey
# 256-bit key (32 characters minimum required by HS256)

app.jwt.expiration.ms=18000000
# 18000000 milliseconds = 5 hours
# Expiration calculation: 5 * 60 * 60 * 1000 = 18000000

# Logging configuration
logging.level.org.springframework.security=DEBUG
# Spring Security ko DEBUG level mein log karo
```

## SecurityConfig Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    // 1. SecurityFilterChain Bean
    //    - CSRF disable kar diya (REST APIs ke liye)
    //    - Sab requests authenticated hain
    //    - HTTP Basic auth use kar rahe hain
    
    // 2. configure() Method
    //    - CustomUserDetailService se users load karte hain
    //    - PasswordEncoder (BCrypt) set karte hain
    
    // 3. PasswordEncoder Bean
    //    - BCryptPasswordEncoder use kar rahe hain
    //    - Password ko encrypt karta hai
    //    - Password ko verify karta hai
}
```

---

# EXCEPTION HANDLING

## Exception Hierarchy

```
Exception
    │
    ├─ Spring Security Exception
    │   ├─ BadCredentialsException
    │   │  └─ Password galat hai
    │   │
    │   ├─ DisabledException
    │   │  └─ Account disabled hai
    │   │
    │   └─ UsernameNotFoundException (Spring)
    │      └─ User nahi mila
    │
    ├─ JWT Exception
    │   ├─ ExpiredJwtException
    │   │  └─ Token expire ho gya
    │   │
    │   ├─ SignatureException
    │   │  └─ Token tamper hua
    │   │
    │   ├─ MalformedJwtException
    │   │  └─ Token corrupt hai
    │   │
    │   └─ IllegalArgumentException
    │      └─ Token empty hai
    │
    └─ Custom Exception
        └─ UsernameNotFoundException (Custom)
           └─ Hamara custom exception
              jo Spring ke exception ko wrap karta hai
```

## Exception Flow

```
AuthController.createToken()
    │
    try {
        authenticate()    <- Ye exception throw kar sakta hai
        loadUserByUsername()
        generateToken()
    }
    catch (BadCredentialsException) { }      // 401
    catch (DisabledException) { }            // 403
    catch (UsernameNotFoundException) { }    // 404
    catch (ExpiredJwtException) { }          // 401
    catch (SignatureException) { }           // 401
    catch (MalformedJwtException) { }        // 400
    catch (IllegalArgumentException) { }    // 400
    catch (Exception) { }                    // 500
```

---

# SECURITY FLOW SUMMARY

## Login Se Logout Tak

```
┌─────────────────────────────────────┐
│ 1. USER REGISTERS                   │
├─────────────────────────────────────┤
│ POST /api/v1/auth/register          │
│ Body: {name, email, password}       │
│                                     │
│ UserService:                        │
│ - Password ko BCrypt se encrypt     │
│ - User database mein save kare      │
│ - Return UserDTO                    │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ 2. USER LOGS IN                     │
├─────────────────────────────────────┤
│ POST /api/v1/auth/login             │
│ Body: {username, password}          │
│                                     │
│ Step 1: authenticate()              │
│ ├─ Email se user load karo          │
│ ├─ Password verify karo             │
│ └─ Success -> Continue              │
│                                     │
│ Step 2: generateToken()             │
│ └─ JWT token create karo            │
│                                     │
│ Response: Token send karo           │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ 3. USER REQUESTS WITH TOKEN         │
├─────────────────────────────────────┤
│ GET /api/v1/posts                   │
│ Header: Authorization: Bearer TOKEN │
│                                     │
│ Spring Security:                    │
│ - Token extract karo                │
│ - Token validate karo               │
│ - Success -> Request process karo   │
│ - Failure -> 401 UNAUTHORIZED       │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ 4. USER LOGS OUT                    │
├─────────────────────────────────────┤
│ Typically:                          │
│ - Client token ko delete kar de     │
│ - localStorage/sessionStorage clear │
│ - Next request bina token ke aaye   │
│ - Spring Security deny kar de       │
│                                     │
│ Note: Server token ko revoke nahi   │
│ karta (Stateless approach)          │
└─────────────────────────────────────┘
```

---

# KEY CONCEPTS

## Stateless Authentication

```
Stateful (Session Based):
┌─────────────┐                    ┌───────────┐
│   Client    │ Login              │  Server   │
└─────────────┘ ──────────────────> └───────────┘
                                        │
                                    Session create
                                    DB mein store
                                        │
                                        ▼
                                    Session ID
                                        │
┌─────────────┐                    ┌───────────┐
│   Client    │ Request + SessionID│  Server   │
└─────────────┘ ──────────────────> └───────────┘
                                        │
                                    DB mein search
                                    User find karo
                                        │
Drawback: DB queries, scale nahi ho sakta


Stateless (JWT Based):
┌─────────────┐                    ┌───────────┐
│   Client    │ Login              │  Server   │
└─────────────┘ ──────────────────> └───────────┘
                                        │
                                    Token generate
                                    (signature included)
                                        │
                                        ▼
                                    Token send
                                        │
┌─────────────┐                    ┌───────────┐
│   Client    │ Request + Token    │  Server   │
└─────────────┘ ──────────────────> └───────────┘
                                        │
                                    Token signature verify
                                    (No DB query needed)
                                    User info token mein
                                        │
Advantage: Fast, scalable, stateless
```

## Password Encryption (BCrypt)

```
Plain Password: "admin"

Round 1:
- Random salt generate karte hain
- Password + salt ko hash kate hain
- Result: $2a$10$abcdefghijklmnopqrstuvwxyz

Round 2:
- Dusri baar same password
- Different salt use hota hai
- Result: $2a$10$different123456789

Verification Time:
- Input: "admin"
- Stored: $2a$10$abcdefghijklmnopqrstuvwxyz
- bcrypt.matches("admin", stored_hash)
  - "admin" ko same salt ke saath hash karte hain
  - Result ko stored hash se compare karte hain
  - Match? -> Correct password
  - Not Match? -> Wrong password

Ye approach secure hai kyunke:
- Plain password kabhi store nahi hota
- Same password different hashes deta hai
- Reverse karna impossible hai (one-way function)
```

---

# TESTING GUIDE

## Test Scenarios

### 1. Successful Login
```
POST /api/v1/auth/login
{
  "username": "gvashistha4@gmail.com",
  "password": "admin"
}

Expected Response (200 OK):
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

Log:
Successfully authenticated user: gvashistha4@gmail.com
JWT token successfully generated for user: gvashistha4@gmail.com
```

### 2. Wrong Password
```
POST /api/v1/auth/login
{
  "username": "gvashistha4@gmail.com",
  "password": "wrongpassword"
}

Expected Response (401 UNAUTHORIZED):
"Authentication failed: Invalid username or password provided"

Log:
Authentication failed: Bad credentials for user gvashistha4@gmail.com
```

### 3. User Not Found
```
POST /api/v1/auth/login
{
  "username": "nonexistent@gmail.com",
  "password": "admin"
}

Expected Response (404 NOT_FOUND):
"Authentication failed: User account not found. Please register first"

Log:
Authentication failed: User not found - nonexistent@gmail.com
```

### 4. Empty Credentials
```
POST /api/v1/auth/login
{
  "username": "",
  "password": ""
}

Expected Response (400 BAD_REQUEST):
"Error: Username and password cannot be empty"
```

### 5. Using Token
```
GET /api/v1/posts
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Expected Response (200 OK):
Posts list return hoga

Log:
Token is valid for user: gvashistha4@gmail.com
```

### 6. Expired Token
```
GET /api/v1/posts
Authorization: Bearer eyJhbGciOi... (expired token)

Expected Response (401 UNAUTHORIZED):
"JWT error: Token has expired. Please login again"

Log:
JWT validation failed: Token has expired
```

---

# SECURITY BEST PRACTICES

1. **Secret Key Management**
   - Secret key ko environment variables mein rakho
   - Code mein hardcode mat karo
   - Regularly rotate karo

2. **Token Expiration**
   - Token ke validity time ko reasonable rakho
   - Zyada long = security risk
   - Zyada short = user ko inconvenience

3. **HTTPS Use Karo**
   - Token ko always HTTPS se bhejo
   - HTTP se mat bhejo (plaintext mein jaayega)

4. **Password Encryption**
   - BCrypt ya Argon2 use karo
   - Plain passwords store mat karo
   - Salting mechanism use karo

5. **Token Storage**
   - Client side secure storage use karo
   - localStorage safe nahi hai
   - HttpOnly cookies use kar sakte ho

6. **Refresh Tokens**
   - Implement kar sakte ho
   - Short-lived token + long-lived refresh token
   - Token expire hone par refresh token use karo

7. **CORS Configuration**
   - Authorized domains only allow karo
   - Wildcard (*) use mat karo production mein

---

# TROUBLESHOOTING

## Common Issues

### Issue 1: 401 UNAUTHORIZED
```
Causes:
1. Token nahi bheja
2. Token invalid signature
3. Token expire ho gya
4. Token tamper hua

Solution:
- Authorization header check karo
- Token validity check karo
- Token signature verify karo
- New token generate karo
```

### Issue 2: 403 FORBIDDEN
```
Causes:
1. Account disabled hai
2. Insufficient permissions

Solution:
- User account enable karo
- Roles check karo
```

### Issue 3: 404 NOT_FOUND
```
Causes:
1. User email galat hai
2. User database mein nahi hai

Solution:
- Email spelling check karo
- User register karao
```

### Issue 4: 400 BAD_REQUEST
```
Causes:
1. Empty username/password
2. Invalid token format
3. Malformed JSON

Solution:
- Input fields fill karo
- Token format check karo
- JSON syntax verify karo
```

---

# CONCLUSION

Hamara Blog App API mein JWT-based stateless security implementation hai jo:

1. **Scalable** - Multiple servers par efficiently work karta hai
2. **Secure** - HMAC-SHA256 signature se protected hai
3. **Fast** - Token ko verify karne mein DB query nahi chalti
4. **Flexible** - Mobile, web, desktop sabko support karta hai
5. **Standard** - JWT globally accepted standard hai

Key Points:
- User login -> Token generate
- Token use -> Protected resources access
- Token invalid/expired -> Access deny
- Stateless -> No session management needed
- Secure -> Password BCrypt se encrypted, token signed

---

**Document Created: 2026-05-07**
**Status: Complete & Ready for Reference**

