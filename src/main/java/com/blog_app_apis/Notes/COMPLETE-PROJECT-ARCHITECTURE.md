# 📚 Blog App APIs - Complete Architecture & Documentation

**Version:** 1.0  
**Date:** May 17, 2026  
**Tech Stack:** Spring Boot 3.3.5, PostgreSQL, JWT, Spring Security  
**Java Version:** 21

---

## 📑 TABLE OF CONTENTS

1. [Project Overview](#project-overview)
2. [High-Level Architecture](#high-level-architecture)
3. [Project Structure](#project-structure)
4. [Complete Data Model](#complete-data-model)
5. [Authentication Flow](#authentication-flow)
6. [API Endpoints](#api-endpoints)
7. [Database Schema](#database-schema)
8. [Technology Stack](#technology-stack)
9. [Key Concepts Explained](#key-concepts-explained)
10. [Common Interview Questions](#common-interview-questions)

---

## 🎯 PROJECT OVERVIEW

### What is Blog App APIs?
A **REST API** built with Spring Boot that allows users to create, read, update, and delete blog posts with:
- **User Management** - Registration, login, user profiles
- **Blog Posts** - Create, edit, delete blog posts
- **Categories** - Organize posts by categories
- **Comments** - Users can comment on blog posts
- **JWT Authentication** - Secure, stateless authentication
- **Role-Based Access Control** - Admin and Normal user roles

### Core Features:
✅ User registration and login  
✅ Password encryption with BCrypt  
✅ JWT token generation and validation  
✅ Create/Read/Update/Delete blog posts  
✅ Comment on posts  
✅ Category management  
✅ Role-based authorization  
✅ Stateless authentication (no sessions)  

---

## 🏗️ HIGH-LEVEL ARCHITECTURE

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Postman/Frontend)                 │
└─────────────────────────────────────────────────────────────────┘
                                  ↓
                    ┌─────────────────────────────┐
                    │    REST API Requests        │
                    │  (HTTP Methods: GET/POST    │
                    │   PUT/DELETE)               │
                    └─────────────────────────────┘
                                  ↓
┌─────────────────────────────────────────────────────────────────┐
│                    SPRING BOOT APPLICATION                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              SECURITY LAYER                              │   │
│  ├──────────────────────────────────────────────────────────┤   │
│  │ 1. JwtAuthenticationFilter                               │   │
│  │    - Intercepts every request                            │   │
│  │    - Extracts JWT from Authorization header              │   │
│  │    - Validates token signature & expiration              │   │
│  │    - Sets authentication in SecurityContext              │   │
│  │                                                           │   │
│  │ 2. SecurityConfig                                        │   │
│  │    - Defines which endpoints are public/protected        │   │
│  │    - Configures CSRF protection                          │   │
│  │    - Manages session policy (STATELESS)                  │   │
│  │    - Routes to JwtAuthenticationEntryPoint on error      │   │
│  │                                                           │   │
│  │ 3. CustomUserDetailService                               │   │
│  │    - Loads user from database by email                   │   │
│  │    - Returns UserDetails for Spring Security             │   │
│  │                                                           │   │
│  │ 4. BCryptPasswordEncoder                                 │   │
│  │    - Hashes passwords during registration                │   │
│  │    - Compares plain password with hash during login      │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                  ↓                               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │            CONTROLLER LAYER                              │   │
│  ├──────────────────────────────────────────────────────────┤   │
│  │ - AuthController (Login/Register)                        │   │
│  │ - UserController (User operations)                       │   │
│  │ - PostController (Blog post operations)                  │   │
│  │ - CategoryController (Category operations)               │   │
│  │ - CommentController (Comment operations)                 │   │
│  │                                                           │   │
│  │ RESPONSIBILITIES:                                        │   │
│  │ • Receive HTTP requests                                  │   │
│  │ • Validate input parameters                              │   │
│  │ • Call service layer                                     │   │
│  │ • Return HTTP responses                                  │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                  ↓                               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │           SERVICE LAYER (Business Logic)                 │   │
│  ├──────────────────────────────────────────────────────────┤   │
│  │ - UserServiceImpl (User operations)                       │   │
│  │ - PostServiceImpl (Blog operations)                       │   │
│  │ - CategoryServiceImpl (Category operations)               │   │
│  │ - CommentServiceImpl (Comment operations)                 │   │
│  │ - FileServiceImpl (File upload/download)                  │   │
│  │                                                           │   │
│  │ RESPONSIBILITIES:                                        │   │
│  │ • Apply business rules                                   │   │
│  │ • Data validation & processing                           │   │
│  │ • Exception handling                                     │   │
│  │ • Call repository layer                                  │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                  ↓                               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │         REPOSITORY LAYER (Data Access)                   │   │
│  ├──────────────────────────────────────────────────────────┤   │
│  │ - UserRepository (JpaRepository<User, Long>)             │   │
│  │ - PostRepository (JpaRepository<Post, Integer>)          │   │
│  │ - CategoryRepository (JpaRepository<Category, Integer>)  │   │
│  │ - CommentRepository (JpaRepository<Comment, Integer>)    │   │
│  │ - RoleRepository (JpaRepository<Role, Integer>)          │   │
│  │                                                           │   │
│  │ BENEFITS:                                                │   │
│  │ • Spring Data JPA auto-generates SQL queries             │   │
│  │ • No need to write manual JDBC code                      │   │
│  │ • Type-safe database operations                          │   │
│  │ • Pagination & sorting built-in                          │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                  ↓                               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │           ENTITY LAYER (Data Models)                     │   │
│  ├──────────────────────────────────────────────────────────┤   │
│  │ - User (Implements UserDetails)                          │   │
│  │ - Post (Blog post entity)                                │   │
│  │ - Category (Post category)                               │   │
│  │ - Comment (User comment on post)                         │   │
│  │ - Role (User role: ADMIN_USER, NORMAL_USER)              │   │
│  │                                                           │   │
│  │ RELATIONSHIPS:                                           │   │
│  │ • User ←→ Post (1-to-Many)                               │   │
│  │ • User ←→ Comment (1-to-Many)                            │   │
│  │ • Post ←→ Comment (1-to-Many)                            │   │
│  │ • Category ←→ Post (1-to-Many)                           │   │
│  │ • User ←→ Role (Many-to-Many)                            │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
                                  ↓
                    ┌─────────────────────────────┐
                    │   PostgreSQL Database       │
                    │  (Persists all data)        │
                    └─────────────────────────────┘
```

---

## 📁 PROJECT STRUCTURE

```
blog-app-apis/
│
├── src/main/java/com/blog_app_apis/
│   ├── BlogAppApisApplication.java          ← Main Spring Boot entry point
│   │
│   ├── config/
│   │   ├── AppConstants.java                ← Application constants
│   │   └── SecurityConfig.java              ← Spring Security configuration
│   │
│   ├── controllers/
│   │   ├── AuthController.java              ← Login/Register endpoints
│   │   ├── UserController.java              ← User CRUD endpoints
│   │   ├── PostController.java              ← Blog post CRUD endpoints
│   │   ├── CategoryController.java          ← Category CRUD endpoints
│   │   └── CommentControler.java            ← Comment CRUD endpoints
│   │
│   ├── dtos/
│   │   ├── UserDTO.java                     ← User data transfer object
│   │   ├── PostDTO.java                     ← Post data transfer object
│   │   ├── AuthResponse.java                ← Enhanced login response DTO
│   │   ├── AuthErrorResponse.java           ← Error response DTO
│   │   ├── ApiResponse.java                 ← Generic API response DTO
│   │   └── ...other DTOs...
│   │
│   ├── Entity/
│   │   ├── User.java                        ← User entity (implements UserDetails)
│   │   ├── Post.java                        ← Blog post entity
│   │   ├── Category.java                    ← Category entity
│   │   ├── Comment.java                     ← Comment entity
│   │   ├── Role.java                        ← User role entity
│   │   ├── JwtAuthRequest.java              ← Login request
│   │   ├── JwtAuthResponce.java             ← JWT response entity
│   │   └── ...other entities...
│   │
│   ├── exceptions/
│   │   ├── GlobalExceptionHandler.java      ← Centralized exception handling
│   │   ├── ResourceNotFoundException.java    ← Custom exception
│   │   ├── InvalidMailException.java         ← Custom exception
│   │   ├── UsernameNotFoundException.java    ← Custom exception
│   │   └── ApiException.java                 ← Custom exception
│   │
│   ├── repository/
│   │   ├── UserRepository.java              ← User data access
│   │   ├── PostRepository.java              ← Post data access
│   │   ├── CategoryRepository.java          ← Category data access
│   │   ├── CommentRepository.java           ← Comment data access
│   │   └── RoleRepository.java              ← Role data access
│   │
│   ├── security/
│   │   ├── JwtTokenHelper.java              ← JWT token generation/validation
│   │   ├── JwtAuthenticationFilter.java     ← JWT request filter
│   │   ├── JwtAuthenticationEntryPoint.java ← Unauthorized response handler
│   │   └── CustomUserDetailService.java     ← Load user details
│   │
│   ├── service/
│   │   ├── UserService.java                 ← User service interface
│   │   ├── PostService.java                 ← Post service interface
│   │   ├── CategoryService.java             ← Category service interface
│   │   ├── CommentService.java              ← Comment service interface
│   │   └── FileService.java                 ← File service interface
│   │
│   ├── serviceImpl/
│   │   ├── UserServiceImpl.java              ← User business logic
│   │   ├── PostServiceImpl.java              ← Post business logic
│   │   ├── CategoryServiceImpl.java          ← Category business logic
│   │   ├── CommentServiceImpl.java           ← Comment business logic
│   │   └── FileServiceImpl.java              ← File handling logic
│   │
│   └── Notes/
│       └── COMPLETE-PROJECT-ARCHITECTURE.md ← This file!
│
├── src/main/resources/
│   └── application.properties               ← Configuration file
│
├── pom.xml                                  ← Maven dependencies
└── README.md
```

---

## 🗂️ COMPLETE DATA MODEL

### Entity Relationships Diagram

```
┌──────────────────┐
│      ROLE        │
├──────────────────┤
│ id (PK)          │  Many-to-Many
│ name             │◄─────────────►
└──────────────────┘               │
                                   │
                            ┌──────────────┐
                            │     USER     │
                            ├──────────────┤
                            │ id (PK)      │
                            │ name         │
                            │ email        │ One-to-Many
                            │ password     │──────────►┌──────────┐
                            │ about        │           │   POST   │
                            │ roles        │           ├──────────┤
                            └──────────────┘           │ id (PK)  │
                                   ▲                   │ title    │
                                   │                   │ content  │
                                   │ One-to-Many       │ image    │
                                   │                   │ addDate  │
                            ┌──────────────┐           │ user(FK) │
                            │   COMMENT    │           │ cat(FK)  │
                            ├──────────────┤           └──────────┘
                            │ id (PK)      │                  ▲
                            │ content      │                  │
                            │ addDate      │           One-to-Many
                            │ user (FK)    │                  │
                            │ post (FK)    │          ┌─────────────────┐
                            └──────────────┘          │   CATEGORY      │
                                                      ├─────────────────┤
                                                      │ id (PK)         │
                                                      │ name            │
                                                      │ description     │
                                                      └─────────────────┘
```

### Entity Details

#### 1. **User Entity**
```java
User implements UserDetails {
    - id: Long (Primary Key)
    - name: String
    - email: String (Username, unique)
    - password: String (BCrypt encrypted)
    - about: String
    - posts: List<Post> (One-to-Many)
    - comments: Set<Comment> (One-to-Many)
    - roles: Set<Role> (Many-to-Many)
}
```
**Key Feature:** Implements `UserDetails` interface for Spring Security integration

#### 2. **Post Entity**
```java
Post {
    - postId: Integer (Primary Key)
    - title: String
    - content: String (max 10000 chars)
    - imageName: String
    - addDate: Date
    - category: Category (Many-to-One)
    - user: User (Many-to-One)
    - comments: Set<Comment> (One-to-Many)
}
```

#### 3. **Category Entity**
```java
Category {
    - categoryId: Integer (Primary Key)
    - title: String
    - description: String
    - posts: List<Post> (One-to-Many)
}
```

#### 4. **Comment Entity**
```java
Comment {
    - id: Integer (Primary Key)
    - content: String
    - addDate: Date
    - user: User (Many-to-One)
    - post: Post (Many-to-One)
}
```

#### 5. **Role Entity**
```java
Role {
    - id: Integer (Primary Key)
    - name: String (ADMIN_USER, NORMAL_USER)
}
```

**Junction Table:** `user_role` (Many-to-Many relationship)
```
user_role {
    - user_id (Foreign Key → users.id)
    - role_id (Foreign Key → role.id)
    - Primary Key: (user_id, role_id)
}
```

---

## 🔐 AUTHENTICATION FLOW

### Complete Authentication Process

#### **Step 1: User Registration**

```
CLIENT REQUEST
    ↓
POST /api/v1/auth/register
{
    "name": "John Doe",
    "email": "john@gmail.com",
    "password": "myPassword123",
    "about": "Software Engineer"
}
    ↓
AuthController.registerUser()
    ↓
UserServiceImpl.registerNewUser()
    ├─ Convert DTO to User entity
    ├─ ENCRYPT password: passwordEncoder.encode(password)
    │  Note: Uses BCryptPasswordEncoder
    │  Result: $2a$10$... (salted hash)
    ├─ Get NORMAL_USER role from database
    ├─ Add role to user
    └─ Save to database:
       ├─ INSERT into users table
       └─ INSERT into user_role junction table
    ↓
RESPONSE: HTTP 201 CREATED
{
    "id": 1,
    "name": "John Doe",
    "email": "john@gmail.com",
    "password": "$2a$10$...",
    "about": "Software Engineer",
    "roles": [{id: 2, name: "NORMAL_USER"}]
}
```

#### **Step 2: User Login & JWT Generation**

```
CLIENT REQUEST
    ↓
POST /api/v1/auth/login
{
    "username": "john@gmail.com",
    "password": "myPassword123"
}
    ↓
AuthController.createToken()
    ├─ STEP 1: INPUT VALIDATION
    │  └─ Check username and password not null/empty
    │
    ├─ STEP 2: AUTHENTICATE CREDENTIALS
    │  └─ AuthController.authenticate(username, password)
    │     ├─ Create UsernamePasswordAuthenticationToken
    │     ├─ Pass to AuthenticationManager
    │     ├─ AuthenticationManager delegates to CustomUserDetailService
    │     │  ├─ Load user by email: userRepo.findByEmail(username)
    │     │  ├─ SELECT * FROM users WHERE user_email = 'john@gmail.com'
    │     │  └─ Return User entity with encrypted password
    │     ├─ Compare passwords:
    │     │  ├─ Plain password from client: "myPassword123"
    │     │  ├─ Encrypted from DB: "$2a$10$..."
    │     │  ├─ Use: passwordEncoder.matches(plain, encrypted)
    │     │  └─ Result: true/false
    │     ├─ Verify account status:
    │     │  ├─ isEnabled() → true
    │     │  ├─ isAccountNonLocked() → true
    │     │  ├─ isCredentialsNonExpired() → true
    │     │  └─ isAccountNonExpired() → true
    │     └─ If all pass → Authentication successful
    │
    ├─ STEP 3: LOAD USER DETAILS
    │  └─ userDetailsService.loadUserByUsername(username)
    │     ├─ Query: SELECT * FROM users WHERE user_email = ?
    │     │         INNER JOIN user_role ON users.id = user_role.user_id
    │     │         INNER JOIN role ON role.id = user_role.role_id
    │     └─ Returns: User object with roles loaded
    │
    ├─ STEP 4: GENERATE JWT TOKEN
    │  └─ jwtTokenHelper.generateToken(userDetails)
    │     ├─ Create claims map: {}
    │     ├─ Get current time: now = 1716086850000
    │     ├─ Calculate expiration: expiration = now + 18000000 (5 hours)
    │     ├─ Build JWT:
    │     │  ├─ HEADER: {alg: "HS384", typ: "JWT"}
    │     │  ├─ PAYLOAD: {
    │     │  │    sub: "john@gmail.com",
    │     │  │    iat: 1716086850,
    │     │  │    exp: 1716104850
    │     │  │  }
    │     │  └─ SIGNATURE: HMAC-SHA384(header.payload, secret)
    │     ├─ Encode to Base64
    │     └─ Return: "eyJhbGc...eyJzdWI...PGYLOx..."
    │
    └─ STEP 5: RETURN RESPONSE
       └─ HTTP 200 OK
          {
              "success": true,
              "message": "Authentication successful.",
              "tokenType": "Bearer",
              "token": "eyJhbGc...",
              "bearerToken": "Bearer eyJhbGc...",
              "username": "john@gmail.com",
              "expiresIn": 18000000,
              "timestamp": 1716086850000
          }
```

#### **Step 3: Using JWT Token in Subsequent Requests**

```
CLIENT REQUEST (with JWT)
    ↓
GET /api/users/AllUsers
Headers:
    Authorization: Bearer eyJhbGc...
    ↓
JwtAuthenticationFilter.doFilterInternal()
    ├─ STEP 1: EXTRACT TOKEN
    │  ├─ Get header: request.getHeader("Authorization")
    │  ├─ Value: "Bearer eyJhbGc..."
    │  ├─ Validate starts with "Bearer"
    │  └─ Extract token: substring(7) → "eyJhbGc..."
    │
    ├─ STEP 2: VALIDATE TOKEN
    │  └─ jwtTokenHelper.validateToken(token, userDetails)
    │     ├─ Parse JWT (verifies HMAC-SHA384 signature)
    │     │  ├─ IF signature invalid → SignatureException
    │     │  ├─ ELSE parse successfully
    │     │
    │     ├─ Extract claims:
    │     │  ├─ username: "john@gmail.com"
    │     │  ├─ iat: 1716086850
    │     │  ├─ exp: 1716104850
    │     │
    │     ├─ Check username matches: username == userDetails.username
    │     │  ├─ MATCH → Continue
    │     │  └─ NO MATCH → Return false
    │     │
    │     ├─ Check not expired: exp > current_time
    │     │  ├─ NOT EXPIRED → Continue
    │     │  └─ EXPIRED → ExpiredJwtException
    │     │
    │     └─ Return: true (all validations passed)
    │
    ├─ STEP 3: LOAD USER DETAILS
    │  └─ userDetailsService.loadUserByUsername(username)
    │     ├─ Load user from database
    │     └─ Get user's roles/authorities
    │
    ├─ STEP 4: SET AUTHENTICATION
    │  └─ Create UsernamePasswordAuthenticationToken
    │     ├─ principal: userDetails
    │     ├─ credentials: null (already validated)
    │     ├─ authorities: userDetails.getAuthorities()
    │     └─ Set in SecurityContextHolder
    │
    └─ STEP 5: CONTINUE FILTER CHAIN
       └─ filterChain.doFilter(request, response)
          ├─ Request proceeds to dispatcherServlet
          ├─ Routes to appropriate controller method
          ├─ User is authenticated in SecurityContext
          └─ Controller executes business logic
                ↓
              RESPONSE: HTTP 200 OK
```

### JWT Token Structure

```
eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huQGdtYWlsLmNvbSIsImlhdCI6MTcxNjA4Njg1MCwiZXhwIjoxNzE2MTA0ODUwfQ.tYgbI18M35iNh6xjorBkME3DGnVDRNBv0wfiOjdPKSkSbJ0cZjEpKos1cYJ02nar

┌──────────────────────────────────────────────────────────────────────────────────────────┐
│ PART 1: HEADER (Base64 encoded JSON)                                                    │
├──────────────────────────────────────────────────────────────────────────────────────────┤
│ eyJhbGciOiJIUzM4NCJ9                                                                    │
│ Decoded: {"alg": "HS384", "typ": "JWT"}                                                 │
│                                                                                          │
│ PART 2: PAYLOAD (Base64 encoded JSON)                                                   │
├──────────────────────────────────────────────────────────────────────────────────────────┤
│ eyJzdWIiOiJqb2huQGdtYWlsLmNvbSIsImlhdCI6MTcxNjA4Njg1MCwiZXhwIjoxNzE2MTA0ODUwfQ       │
│ Decoded: {                                                                               │
│     "sub": "john@gmail.com",        ← Subject (username/email)                           │
│     "iat": 1716086850,               ← Issued At (token creation time)                   │
│     "exp": 1716104850                ← Expiration (5 hours later)                        │
│ }                                                                                        │
│                                                                                          │
│ PART 3: SIGNATURE (HMAC-SHA384)                                                         │
├──────────────────────────────────────────────────────────────────────────────────────────┤
│ tYgbI18M35iNh6xjorBkME3DGnVDRNBv0wfiOjdPKSkSbJ0cZjEpKos1cYJ02nar                      │
│                                                                                          │
│ Generated by: HMAC-SHA384(header.payload, secret_key)                                   │
│ Secret: "mySuperSecretKeyForJwtAuthentication123456SecretKey"                           │
│ Purpose: Proof that server created this token (can't be forged)                         │
│                                                                                          │
│ THE MAGIC: Anyone can decode header & payload (they're just Base64)                     │
│           But only server knows the secret key needed to generate signature!            │
│           If tampered, signature won't match → Token rejected!                          │
└──────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔌 API ENDPOINTS

### 1. Authentication Endpoints

#### **POST /api/v1/auth/register**
```
Purpose: Register a new user
Access: Public (no authentication required)

Request:
POST /api/v1/auth/register
Content-Type: application/json

{
    "name": "John Doe",
    "email": "john@gmail.com",
    "password": "Admin123",
    "about": "Software Engineer"
}

Response (HTTP 201 CREATED):
{
    "id": 1,
    "name": "John Doe",
    "email": "john@gmail.com",
    "password": "$2a$10$...",  // Encrypted
    "about": "Software Engineer",
    "roles": [{
        "id": 2,
        "name": "NORMAL_USER"
    }]
}

Status Codes:
- 201 CREATED: User registered successfully
- 400 BAD_REQUEST: Invalid input data
- 409 CONFLICT: Email already exists
- 500 INTERNAL_SERVER_ERROR: Server error
```

#### **POST /api/v1/auth/login**
```
Purpose: Authenticate user and get JWT token
Access: Public

Request:
POST /api/v1/auth/login
Content-Type: application/json

{
    "username": "john@gmail.com",
    "password": "Admin123"
}

Response (HTTP 200 OK):
{
    "success": true,
    "message": "Authentication successful. JWT token generated.",
    "tokenType": "Bearer",
    "token": "eyJhbGciOiJIUzM4NCJ9.eyJz...",
    "bearerToken": "Bearer eyJhbGciOiJIUzM4NCJ9.eyJz...",
    "username": "john@gmail.com",
    "expiresIn": 18000000,  // 5 hours in milliseconds
    "timestamp": 1716086850000
}

Status Codes:
- 200 OK: Login successful
- 400 BAD_REQUEST: Username/password not provided
- 401 UNAUTHORIZED: Invalid credentials
- 403 FORBIDDEN: Account disabled
- 404 NOT_FOUND: User not found
- 500 INTERNAL_SERVER_ERROR: Server error
```

### 2. User Endpoints

#### **GET /api/users/AllUsers**
```
Purpose: Get all users
Access: Protected (requires JWT)

Request:
GET /api/users/AllUsers
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...

Response (HTTP 200 OK):
[
    {
        "id": 1,
        "name": "John Doe",
        "email": "john@gmail.com",
        "password": "$2a$10$...",
        "about": "Software Engineer",
        "roles": [...]
    },
    ...
]

Status Codes:
- 200 OK: Users retrieved
- 401 UNAUTHORIZED: Invalid/missing token
- 500 INTERNAL_SERVER_ERROR: Server error
```

#### **GET /api/users/{userId}**
```
Purpose: Get single user by ID
Access: Protected

Request:
GET /api/users/1
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...

Response (HTTP 200 OK):
{
    "id": 1,
    "name": "John Doe",
    "email": "john@gmail.com",
    "about": "Software Engineer"
}

Status Codes:
- 200 OK: User found
- 401 UNAUTHORIZED: Invalid token
- 404 NOT_FOUND: User not found
```

#### **POST /api/users/createUser**
```
Purpose: Create a new user (different from registration)
Access: Public (no auth required, no password encryption!)

⚠️ WARNING: This endpoint is INSECURE!
- Password is NOT encrypted
- No role is assigned
Should be replaced with proper registration endpoint!

Request:
POST /api/users/createUser
{
    "name": "Jane Doe",
    "email": "jane@gmail.com",
    "password": "plaintext123",
    "about": "..."
}
```

#### **PUT /api/users/updateUser/{userId}**
```
Purpose: Update user details
Access: Protected

Request:
PUT /api/users/updateUser/1
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
{
    "name": "John Updated",
    "email": "john.updated@gmail.com",
    "about": "Updated bio"
}

Response (HTTP 200 OK):
{
    "id": 1,
    "name": "John Updated",
    "email": "john.updated@gmail.com"
}
```

#### **DELETE /api/users/deleteUser/{userId}**
```
Purpose: Delete user
Access: Protected (should restrict to ADMIN!)

Request:
DELETE /api/users/deleteUser/1
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...

Response (HTTP 200 OK):
{
    "message": "User Deleted Successfully",
    "success": true
}

⚠️ SECURITY ISSUE: Currently accessible to all authenticated users!
Should add: @PreAuthorize("hasRole('ADMIN_USER')")
```

### 3. Post Endpoints

#### **POST /api/posts/create**
```
Purpose: Create new blog post
Access: Protected

Request:
POST /api/posts/create
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
{
    "title": "Spring Boot Guide",
    "content": "Complete guide to Spring Boot...",
    "imageName": "spring-boot.jpg",
    "categoryId": 1
}

Response (HTTP 201 CREATED):
{
    "postId": 1,
    "title": "Spring Boot Guide",
    "content": "...",
    "imageName": "spring-boot.jpg",
    "addDate": "2026-05-17T12:34:56"
}
```

#### **GET /api/posts/getAllPost**
```
Purpose: Get all blog posts
Access: Protected

Request:
GET /api/posts/getAllPost
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...

Response (HTTP 200 OK):
[
    {
        "postId": 1,
        "title": "Spring Boot Guide",
        "content": "...",
        "category": {...},
        "user": {...}
    },
    ...
]
```

#### **GET /api/posts/{postId}**
```
Purpose: Get single post
Access: Protected
```

#### **PUT /api/posts/update/{postId}**
```
Purpose: Update blog post
Access: Protected (only author should be allowed!)
```

#### **DELETE /api/posts/delete/{postId}**
```
Purpose: Delete blog post
Access: Protected
```

### 4. Category Endpoints

#### **POST /api/category/create**
```
Purpose: Create new category
Access: Protected (ADMIN only)

Request:
POST /api/category/create
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
{
    "title": "Technology",
    "description": "Tech-related posts"
}
```

#### **GET /api/category/getAll**
```
Purpose: Get all categories
Access: Public or Protected
```

#### **PUT /api/category/update/{id}**
```
Purpose: Update category
```

#### **DELETE /api/category/delete/{id}**
```
Purpose: Delete category
```

### 5. Comment Endpoints

#### **POST /api/comments/create**
```
Purpose: Create comment on post
Access: Protected

Request:
POST /api/comments/create
{
    "content": "Great post!",
    "postId": 1
}
```

#### **GET /api/comments/post/{postId}**
```
Purpose: Get all comments on a post
```

#### **DELETE /api/comments/delete/{id}**
```
Purpose: Delete comment
```

---

## 🗄️ DATABASE SCHEMA

### PostgreSQL Tables

#### **users table**
```sql
CREATE TABLE public.users (
    id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    user_email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    about TEXT,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

-- Indexes for performance
CREATE UNIQUE INDEX users_user_email_key ON public.users(user_email);
```

#### **role table**
```sql
CREATE TABLE public.role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT role_pkey PRIMARY KEY (id)
);

-- Pre-populated data
INSERT INTO public.role (id, name) VALUES 
    (1, 'ADMIN_USER'),
    (2, 'NORMAL_USER');
```

#### **user_role (Junction Table - Many-to-Many)**
```sql
CREATE TABLE public.user_role (
    user_id BIGINT NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES public.role(id)
);
```

#### **Categories table**
```sql
CREATE TABLE public.categories (
    category_id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    CONSTRAINT categories_pkey PRIMARY KEY (category_id)
);
```

#### **posts table**
```sql
CREATE TABLE public.posts (
    post_id SERIAL PRIMARY KEY,
    post_title VARCHAR(255) NOT NULL,
    post_content TEXT NOT NULL,
    image_name VARCHAR(255),
    add_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    category_id INTEGER NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (category_id) REFERENCES public.categories(category_id),
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT posts_pkey PRIMARY KEY (post_id)
);
```

#### **comment table**
```sql
CREATE TABLE public.comment (
    id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    add_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    post_post_id INTEGER NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (post_post_id) REFERENCES public.posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT comment_pkey PRIMARY KEY (id)
);
```

### ER Diagram
```
┌─────────────┐         ┌──────────────┐
│   users     │─────────│   user_role  │─────────│   role        │
│             │ PK-FK   │              │   PK-FK │               │
│ id (PK)     │         │ user_id      │         │ id (PK)       │
│ user_name   │         │ role_id      │         │ name          │
│ user_email  │         └──────────────┘         └───────────────┘
│ password    │
│ about       │
└─────────────┘
      │ ←─ One-to-Many
      ├────────► posts (user_id FK)
      │           ├──► categories (category_id FK)
      │           └──► comments (post_id FK, user_id FK)
      │
      └────────► comments (user_id FK)
```

---

## 🛠️ TECHNOLOGY STACK

### Backend Technologies

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 21 | Programming language |
| **Spring Boot** | 3.3.5 | Framework for REST APIs |
| **Spring Security** | Latest | Authentication & Authorization |
| **Spring Data JPA** | Latest | ORM & Database access |
| **PostgreSQL** | 12+ | Relational database |
| **JWT (jjwt-api)** | Latest | Token generation/validation |
| **BCrypt** | Built-in | Password hashing |
| **ModelMapper** | 3.1.0 | DTO ↔ Entity mapping |
| **Lombok** | Latest | Reduce boilerplate (getters/setters) |
| **Swagger/SpringDoc** | 2.5.0 | API documentation |

### Testing & Build

| Tool | Purpose |
|------|---------|
| **Maven** | Build & dependency management |
| **JUnit** | Unit testing |
| **Mockito** | Mocking frameworks |

### Dependencies in pom.xml

```xml
<!-- Web -->
<spring-boot-starter-web>

<!-- Data Access -->
<spring-boot-starter-data-jpa>
<postgresql>

<!-- Security -->
<spring-boot-starter-security>
<jjwt-api>
<jjwt-impl>
<jjwt-jackson>

<!-- Utilities -->
<modelmapper>
<lombok>

<!-- Validation -->
<spring-boot-starter-validation>

<!-- Documentation -->
<springdoc-openapi-starter-webmvc-ui> (Swagger)

<!-- Development -->
<spring-boot-devtools>
```

### Configuration Files

**application.properties**
```properties
# Server Config
spring.application.name=blog-app-apis
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/Blog-App
spring.datasource.username=postgres
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update  # auto-create/update schema
spring.jpa.show-sql=true

# JWT Configuration
app.jwt.secret=mySuperSecretKeyForJwtAuthentication123456SecretKey
app.jwt.expiration.ms=18000000  # 5 hours

# File uploads
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.org.springframework.security=DEBUG
```

---

## 💡 KEY CONCEPTS EXPLAINED

### 1. **JWT (JSON Web Token)**

**What is it?**  
A stateless, self-contained token that carries user information without server storing sessions.

**Structure:** `header.payload.signature`

**Benefits:**
- ✅ Stateless (no server session storage)
- ✅ Scalable (works across multiple servers)
- ✅ Secure (signed with secret key)
- ✅ Mobile-friendly
- ✅ Prevents CSRF attacks

**Workflow:**
```
1. User logs in → Server generates JWT
2. Client stored JWT locally
3. Each request includes JWT in Authorization header
4. Server validates JWT signature
5. Server no longer needs to check database for session
```

### 2. **BCrypt Password Hashing**

**What is it?**  
Algorithm that hashes passwords with a random salt for security.

**Why not store plain passwords?**
- ❌ If database hacked → All passwords exposed
- ❌ Attackers don't need to try each password individually

**How BCrypt works:**
```
Plain Password: "MyPassword123"
    ↓
BCrypt(with random salt)
    ↓
Hash: "$2a$10$N9qo8uLOickgx2ZMRZoMe..."

Each encryption is different (due to salt):
- BCrypt("Password") = "$2a$10$abc..."
- BCrypt("Password") = "$2a$10$xyz..."  ← Different!

Verification:
- bcrypt.matches("Password", "$2a$10$abc...") → true
- bcrypt.matches("WrongPass", "$2a$10$abc...") → false
```

**Benefits:**
- ✅ Salted (prevents rainbow table attacks)
- ✅ Slow (prevents brute force)
- ✅ Industry standard

### 3. **Spring Security Filter Chain**

**What is it?**  
Series of filters that intercept every HTTP request for security checks.

**Filter Order:**
```
Request comes in
    ↓
JwtAuthenticationFilter (our custom filter)
    ├─ Extract JWT token
    ├─ Validate signature & expiration
    ├─ Load user details
    └─ Set authentication in SecurityContext
    ↓
UsernamePasswordAuthenticationFilter
    ↓
Authorization Filter
    ├─ Check user has required roles
    ├─ ACCEPT or DENY
    ↓
Request reaches controller (if authorized)
    ↓
CommandInterpreter processes request
    ↓
Response sent back
```

### 4. **Entity Relationships (JPA)**

**One-to-Many**
```
User ──── Post
 ↓
One user can create many posts

@OneToMany(mappedBy = "user")
List<Post> posts;

@ManyToOne
@JoinColumn(name = "user_id")
User user;
```

**Many-to-Many**
```
User ──── Role
 ↓
One user can have many roles
One role can be assigned to many users

@ManyToMany
@JoinTable(name = "user_role")
Set<Role> roles;
```

### 5. **DTOs (Data Transfer Objects)**

**What are DTOs?**  
Classes that transfer data between layers without exposing database entity structure.

**Why use DTOs?**
- ✅ Hide sensitive fields (passwords)
- ✅ Decouple API from database schema
- ✅ Transform data selectively
- ✅ Prevent over-fetching

**Example:**
```
User Entity:            UserDTO:
- id                    - id
- name                  - name
- email                 - email
- password ← Hidden!    
- about                 - about
- posts
- comments
- roles
```

### 6. **ModelMapper**

**What is it?**  
Automatically maps between entities and DTOs.

```
User entity → ModelMapper → UserDTO
  ↓                              ↓
(All fields)              (Selected fields)

Usage:
UserDTO dto = modelMapper.map(user, UserDTO.class);
User entity = modelMapper.map(userDTO, User.class);
```

### 7. **Stateless vs Stateful Authentication**

| Aspect | Stateless (JWT) | Stateful (Session) |
|--------|---------------|--------------------|
| **Storage** | Client stores token | Server stores session |
| **Scalability** | ✅ Highly scalable | ❌ Requires sticky sessions |
| **Database** | ❌ Need to load user on each request | ✅ Cache session object |
| **Mobile** | ✅ Perfect | ❌ Cookie-based issues |
| **Microservices** | ✅ Works cross-service | ❌ Complex |
| **Example** | JWT, OAuth2 | PHP sessions, Cookies |

---

## 🎓 COMMON INTERVIEW QUESTIONS

### Core Concepts

#### **1. What is Java and why is it used?**

**Answer:**
- Java is a class-based, object-oriented programming language
- Used for building robust, scalable enterprise applications
- Features:
  - Platform independent ("Write Once, Run Anywhere")
  - Strong memory management (Garbage Collection)
  - Compiled & interpreted (bytecode compiled to machine code by JVM)
  - Multithreading & concurrency support
  - Rich standard library

#### **2. What is Spring Boot and its advantages?**

**Answer:**
Spring Boot is a framework that simplifies Spring application development.

**Advantages:**
- Auto-configuration (detects libraries on classpath)
- Embedded servers (Tomcat, Jetty built-in)
- Production-ready (metrics, health checks)
- Convention over configuration
- Microservices-friendly
- Rapid development

#### **3. Explain the difference between REST and SOAP.**

**Answer:**

| REST | SOAP |
|------|------|
| **Architecture Style** | **Protocol** |
| Lightweight | Heavy |
| Uses HTTP methods (GET, POST, PUT, DELETE) | Uses XML over HTTP/TCP |
| Stateless | Complex state management |
| Easy to implement | Complex to implement |
| JSON/XML | XML only |
| Used in: APIs, web services | Used in: Enterprise systems |

#### **4. What are REST API principles (REST constraints)?**

**Answer:**
**SAFE-T:**
- **S**tandardized URLs (resources identified by URIs)
- **A**nalogous HTTP methods (GET, POST, PUT, DELETE)
- **F**eatureless URIs (stateless)
- **E**xpressible (multiple representations: JSON, XML)
- **T**ransformational (HATEOAS - links for navigation)

Or the 6 main constraints:
1. **Client-Server** - Separation of concerns
2. **Stateless** - No server-side sessions
3. **Cacheable** - Responses should be cacheable
4. **Uniform Interface** - Consistent API design
5. **Layered System** - Can add middleware
6. **Code on Demand** - Optional: server can send executable code

---

### Authentication & Security

#### **5. What is JWT? How does it work?**

**Answer:**
JWT (JSON Web Token) is a stateless authentication token consisting of 3 parts:

**Header:** Contains algorithm info
```json
{"alg": "HS256", "typ": "JWT"}
```

**Payload:** Contains user claims
```json
{"sub": "user@gmail.com", "iat": 1234567890, "exp": 1234651290}
```

**Signature:** HMAC using secret key
```
HMAC-SHA256(header.payload, secret_key)
```

**Full Token:**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGdtYWlsLmNvbSJ9.SIGNATURE
```

**How it works:**
1. User logs in → Server generates JWT
2. Client stores JWT
3. Client sends JWT in Authorization header
4. Server validates JWT signature
5. If valid → Request processed
6. If invalid → Return 401 Unauthorized

#### **6. What are the advantages of JWT over sessions?**

**Answer:**
- ✅ **Stateless:** No server-side storage needed
- ✅ **Scalable:** Works across multiple servers/microservices
- ✅ **Mobile-friendly:** Can be sent via headers or query params
- ✅ **CORS-friendly:** No cookie issues
- ✅ **Self-contained:** All info in token itself
- ✅ **Performance:** No database lookup for session
- ✅ **Microservices:** Can be verified by multiple services

#### **7. What is BCrypt and why use it?**

**Answer:**
BCrypt is a password hashing algorithm that:

**What it does:**
1. Takes plain password
2. Generates random salt
3. Applies hash function multiple rounds
4. Stores as `$2a$rounds$salt$hash`

**Why use it:**
- ✅ **Salted:** Prevents rainbow table attacks
- ✅ **Slow:** Each hash takes milliseconds → makes brute force impractical
- ✅ **Adaptive:** Can increase rounds as computers get faster
- ✅ **Industry standard:** Used in many frameworks
- ✅ **Irreversible:** Can't decrypt hash back to password

**Example:**
```
Password: "Admin123"
BCrypt($10 rounds) = "$2a$10$N9qo8uLOickgx2ZMRZoMovePMHhV5Y8abj0R4R3wNwghFnN4rISpa"

Each time: Different hash (due to random salt)
Verification: bcrypt.matches("Admin123", "$2a$10$...") → true
```

#### **8. Explain Spring Security Configuration in this project.**

**Answer:**
The SecurityConfig class:

1. **CSRF Disabled:** JWT sent via headers → No CSRF risk
2. **Session Management:** STATELESS → No HttpSession
3. **Exception Handling:** Unauthorized → JwtAuthenticationEntryPoint
4. **Authorization Rules:**
   - `/api/v1/auth/**` → Public (login/register)
   - `/v3/api-docs` → Public (Swagger)
   - All other endpoints → Require authentication
5. **JWT Filter:** Added before UsernamePasswordAuthenticationFilter
6. **Password Encoder:** BCryptPasswordEncoder

#### **9. What is @PreAuthorize and how to use it?**

**Answer:**
@PreAuthorize controls access based on roles/permissions.

**Example:**
```java
@PreAuthorize("hasRole('ADMIN_USER')")
@DeleteMapping("/deleteUser/{userId}")
public ResponseEntity deleteUser(@PathVariable Integer userId) {
    // Only users with ADMIN_USER role can access
}

@PreAuthorize("hasRole('ADMIN_USER') or hasRole('NORMAL_USER')")
@GetMapping("/getLikes/{postId}")
public ResponseEntity getLikes(@PathVariable Integer postId) {
    // Both roles allowed
}

@PreAuthorize("@userService.isPostOwner(#postId, principal.email)")
@PutMapping("/updatePost/{postId}")
public ResponseEntity updatePost(@PathVariable Integer postId) {
    // Only post owner can update
}
```

---

### Database & Persistence

#### **10. What is ORM (Object-Relational Mapping)? How does JPA work?**

**Answer:**
**ORM** maps Java objects to database tables automatically.

**JPA Benefits:**
- ✅ Write Java code, not SQL
- ✅ Database agnostic (change DB anytime)
- ✅ Type-safe queries
- ✅ Automatic relationship management

**How it works:**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(unique = true)
    private String email;
}

// JPA generates:
// CREATE TABLE users (id BIGINT PRIMARY KEY, email VARCHAR UNIQUE);
// INSERT, UPDATE, DELETE queries automatically!
```

#### **11. Explain the @ManyToMany relationship with user_role example.**

**Answer:**
Many-to-Many allows users to have multiple roles and roles to be assigned to multiple users.

**Implementation:**
```java
// User Entity
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "user_role",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles;

// Creates junction table:
// user_role (user_id FK, role_id FK)
```

**Database:**
```sql
users table: id, name, email, password
role table: id, name
user_role: user_id (FK), role_id (FK) ← Junction table
```

**Usage:**
```java
// Loading user with all roles:
User user = userRepo.findById(1);
Set<Role> roles = user.getRoles();  // Already loaded (EAGER)

// Getting role names for Spring Security:
user.getRoles()
    .stream()
    .map(Role::getName)
    .collect(toSet());  // ["ADMIN_USER", ...]
```

#### **12. What is Cascade delete? Why important?**

**Answer:**
Cascade delete automatically deletes related records when parent is deleted.

**Example:**
```java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
private List<Post> posts;  // If user deleted → all posts deleted
```

**Why important:**
- ✅ Maintains data integrity
- ✅ Prevents orphaned records
- ✅ Simplifies code (no manual deletion)
- ✅ Prevents foreign key violations

#### **13. What is Lazy vs Eager loading?**

**Answer:**

| Lazy | Eager |
|------|-------|
| Load related data only when accessed | Load all related data immediately |
| Better performance (less data loaded) | More data loaded upfront |
| Can cause N+1 query problem | One query loads everything |
| Example: `fetch = FetchType.LAZY` | Example: `fetch = FetchType.EAGER` |

**Example:**
```java
// Lazy
@OneToMany(fetch = FetchType.LAZY)
List<Post> posts;

User user = userRepo.find(1);
// At this point, posts NOT loaded

List<Post> posts = user.getPosts();  // NOW queries loaded!
// Extra query issued here

// Eager
@OneToMany(fetch = FetchType.EAGER)
List<Post> posts;

User user = userRepo.find(1);
// All posts already loaded in first query!
```

---

### Project-Specific

#### **14. What is the flow for registering a new user?**

**Answer:**

```
1. Client sends: POST /api/v1/auth/register
   {name, email, password, about}

2. AuthController.registerUser()
   ├─ Calls UserService.registerNewUser()

3. UserServiceImpl.registerNewUser()
   ├─ Map UserDTO → User entity
   ├─ Encrypt password: passwordEncoder.encode(password)
   ├─ Get NORMAL_USER role from DB
   ├─ Add role to user
   └─ Save to DB

4. Database Operations:
   ├─ INSERT into users table
   └─ INSERT into user_role junction table

5. Return HTTP 201 with registered user DTO
```

#### **15. What is the flow for login?**

**Answer:**

```
1. Client sends: POST /api/v1/auth/login
   {username, password}

2. AuthController.createToken()
   ├─ Validate inputs
   ├─ Call authenticate()

3. authenticate()
   ├─ Create UsernamePasswordAuthenticationToken
   ├─ Pass to AuthenticationManager

4. AuthenticationManager
   ├─ Loads user: CustomUserDetailService.loadUserByUsername()
   ├─ Query: SELECT * FROM users WHERE email = ?
   ├─ Compare passwords: passwordEncoder.matches(plain, hashed)
   ├─ Verify account status
   └─ If all pass → authentication success

5. Load user authorities/roles:
   ├─ User roles from user_role table
   └─ Convert to GrantedAuthority

6. Generate JWT Token:
   ├─ Create claims
   ├─ Build JWT with HMAC-SHA384
   └─ Return token

7. Return HTTP 200 with:
   {
       "token": "eyJhbGc...",
       "bearerToken": "Bearer eyJhbGc...",
       "username": "user@gmail.com",
       "expiresIn": 18000000
   }
```

#### **16. Explain How JWT token is validated on protected endpoint access.**

**Answer:**

```
1. Client sends request with JWT:
   GET /api/users/AllUsers
   Authorization: Bearer eyJhbGc...

2. JwtAuthenticationFilter.doFilterInternal()
   ├─ Extract Authorization header
   ├─ Check if starts with "Bearer"
   └─ Extract token: substring(7)

3. jwtTokenHelper.validateToken()
   ├─ Parse JWT (verifies HMAC-SHA384 signature)
   │  ├─ If signature invalid → Reject
   │  └─ If valid → Continue
   ├─ Extract claims (username, iat, exp)
   ├─ Check expiration: exp > current_time
   │  ├─ If expired → Reject
   │  └─ If valid → Continue
   ├─ Verify username matches
   └─ Return true/false

4. If valid:
   ├─ Load user details
   ├─ Create Authentication token
   ├─ Set in SecurityContextHolder
   └─ Proceed to controller

5. If invalid:
   ├─ Clear SecurityContext
   ├─ Set to Anonymous
   └─ Return 401 Unauthorized
```

#### **17. What are the security issues in this application?**

**Answer:**

**Current Issues:**
1. ❌ `/api/users/createUser` - Password NOT encrypted
2. ❌ DELETE endpoints - No @PreAuthorize (any authenticated user can delete)
3. ❌ JWT secret stored in properties (should be env variable)
4. ❌ No rate limiting (vulnerable to brute force)
5. ❌ No HTTPS mentioned (should be enforced in production)
6. ❌ POST/PUT endpoints - No input validation for SQL injection
7. ❌ File upload endpoint - Vulnerable to malicious files
8. ❌ Passwords returned in responses (should be excluded)

**Fixes:**
```java
// 1. Add @PreAuthorize
@PreAuthorize("hasRole('ADMIN_USER')")
@DeleteMapping("/deleteUser/{userId}")
public ResponseEntity deleteUser(@PathVariable Integer userId) {}

// 2. Validate inputs
@DeleteMapping("/deletePost/{postId}")
@PreAuthorize("hasRole('ADMIN_USER') or @postService.isAuthor(#postId, principal.email)")
public ResponseEntity deletePost(@PathVariable Integer postId) {}

// 3. Exclude password from responses
@JsonIgnore
private String password;

// 4. Use environment variables
app.jwt.secret=${JWT_SECRET}

// 5. Use HTTPS in production
// 6. Add input validation (@Valid, custom validators)
```

#### **18. How would you implement pagination?**

**Answer:**

**Spring Data provides PaginationPageable interface:**

```java
// Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAll(Pageable pageable);
}

// Service
public List<UserDTO> getAllUsers(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
    Page<User> pageResult = userRepository.findAll(pageable);
    return pageResult.map(user -> modelMapper.map(user, UserDTO.class))
                     .toList();
}

// Controller
@GetMapping("/getAllUsers")
public ResponseEntity<Map<String, Object>> getAllUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    
    Pageable pageable = PageRequest.of(page, size);
    Page<UserDTO> users = userService.getAllUsers(pageable);
    
    return ResponseEntity.ok(Map.of(
        "content", users.getContent(),
        "totalPages", users.getTotalPages(),
        "totalElements", users.getTotalElements(),
        "currentPage", page
    ));
}
```

#### **19. How to handle exceptions globally?**

**Answer:**

Use `@ControllerAdvice` for centralized exception handling:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity errorHandler(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
            Instant.now(),
            ex.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(errorDetails);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity globalErrorHandler(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("message", "Internal Server Error"));
    }
}
```

#### **20. What is ModelMapper and why use it?**

**Answer:**
Maps entities to DTOs automatically without writing boilerplate code.

**Without ModelMapper:**
```java
public UserDTO entityToDTO(User user) {
    UserDTO dto = new UserDTO();
    dto.setId(user.getId());
    dto.setName(user.getName());
    dto.setEmail(user.getEmail());
    dto.setAbout(user.getAbout());
    return dto;
}
// Tedious! Imagine 50 fields!
```

**With ModelMapper:**
```java
UserDTO dto = modelMapper.map(user, UserDTO.class);
// One line! Matches fields by name automatically
```

**Benefits:**
- ✅ Reduces boilerplate
- ✅ Automatic field mapping
- ✅ Type-safe
- ✅ Supports nested objects

---

## 📚 ADDITIONAL RESOURCES

### Important Annotations

```java
// Entity & JPA
@Entity               // Mark as database entity
@Table(name = "")    // Maps to database table
@Id                  // Primary key
@GeneratedValue      // Auto-increment
@Column              // Maps to database column
@OneToMany           // 1-to-Many relationship
@ManyToOne           // Many-to-1 relationship
@ManyToMany          // Many-to-Many relationship
@JoinTable           // Junction table for Many-to-Many
@JoinColumn          // Foreign key mapping
@ForeignKey          // Foreign key constraint
@Transient           // Don't persist this field

// Spring
@SpringBootApplication
@Component
@Service
@Repository
@Controller
@RestController
@RequestMapping
@GetMapping/@PostMapping/@PutMapping/@DeleteMapping
@RequestBody
@PathVariable
@RequestParam

// Security
@PreAuthorize        // Check roles before execution
@Secured             // Alternative to @PreAuthorize
@RolesAllowed        // JSR-250 annotation

// Validation
@Valid               // Trigger validation
@NotNull
@NotEmpty
@Email
@Size(min=, max=)

// Lombok
@Data                // Generates getter, setter, toString, equals, hashCode
@Getter/@Setter
@NoArgsConstructor   // Generate default constructor
@AllArgsConstructor  // Generate constructor with all fields

// JSON
@JsonIgnore          // Don't serialize this field
@JsonProperty        // Custom JSON property name
```

### Common Commands

```bash
# Build project
mvn clean install

# Run tests
mvn test

# Start application
mvn spring-boot:run

# Generate JAR
mvn package

# View dependencies
mvn dependency:tree

# Format code
mvn spotless:apply
```

### SQL Queries Used

```sql
-- User authentication
SELECT * FROM users WHERE user_email = 'user@gmail.com';

-- Load roles
SELECT u.*, r.* FROM users u
LEFT JOIN user_role ur ON u.id = ur.user_id
LEFT JOIN role r ON ur.role_id = r.id
WHERE u.id = ?;

-- Get user posts
SELECT * FROM posts WHERE user_id = ? ORDER BY add_date DESC;

-- Get post comments
SELECT * FROM comment WHERE post_post_id = ? ORDER BY add_date DESC;

-- Delete user and cascade
DELETE FROM users WHERE id = ?;  -- Cascades to posts and comments
```

---

## 🎯 SUMMARY

This Blog App APIs project demonstrates:

1. ✅ **RESTful API design** - Proper HTTP methods and status codes
2. ✅ **Spring Security** - JWT authentication & authorization
3. ✅ **Layered architecture** - Controller → Service → Repository → Entity
4. ✅ **Database design** - Proper relationships and constraints
5. ✅ **Password security** - BCrypt hashing
6. ✅ **Exception handling** - Global exception handler
7. ✅ **DTO pattern** - Data transformation between layers
8. ✅ **Role-based access** - Admin and Normal user roles
9. ✅ **Entity relationships** - 1-to-Many, Many-to-Many
10. ✅ **Best practices** - Lombok, ModelMapper, JPA

**Interview Preparation Tips:**
- Understand every layer of the architecture
- Know JWT internals (how signature prevents tampering)
- Understand password hashing (why BCrypt)
- Know database relationships and SQL queries
- Be able to explain security flow end-to-end
- Understand Spring Security filter chain
- Know when to use Lazy vs Eager loading
- Be familiar with JPA/Hibernate

---

**Last Updated:** May 17, 2026  
**Author:** AI Assistant  
**Status:** Complete & Production-Ready Documentation

