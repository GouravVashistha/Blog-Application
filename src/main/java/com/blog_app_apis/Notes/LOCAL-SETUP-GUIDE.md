# 🚀 LOCAL SETUP GUIDE - Blog App APIs

**Complete Step-by-Step Instructions**  
**From Installation to First API Call**

---

## 📋 TABLE OF CONTENTS

1. [Prerequisites & Installation](#prerequisites--installation)
2. [Database Setup](#database-setup)
3. [Project Configuration](#project-configuration)
4. [Run Application](#run-application)
5. [Register First User](#register-first-user)
6. [Generate JWT Token](#generate-jwt-token)
7. [Use API with Token](#use-api-with-token)
8. [Troubleshooting](#troubleshooting)

---

## 🔧 PREREQUISITES & INSTALLATION

### Step 1: Install Java Development Kit (JDK)

**For Windows:**

1. Download JDK 21 from: https://www.oracle.com/java/technologies/downloads/#java21
2. Run the installer
3. Follow installation wizard
4. Click "Next" → "Next" → "Install"

**Verify Installation:**
```bash
# Open Command Prompt/PowerShell and run:
java -version

# Expected Output:
# java version "21" 2024-09-17
# Java(TM) SE Runtime Environment (build 21+35-2513)
```

**If not recognized:**
- Add Java to PATH:
  - Right-click "This PC" → Properties
  - Click "Advanced system settings"
  - Click "Environment Variables"
  - Under "System variables", click "New"
  - Variable name: `JAVA_HOME`
  - Variable value: `C:\Program Files\Java\jdk-21` (adjust path if different)
  - Click OK → OK → OK
  - Restart Command Prompt

---

### Step 2: Install Apache Maven

**For Windows:**

1. Download Maven from: https://maven.apache.org/download.cgi
   - Look for "apache-maven-3.9.x-bin.zip"
2. Extract to: `C:\Program Files\Maven` (create folder if needed)
3. Add Maven to PATH:
   - Environment Variables
   - New System Variable:
     - Name: `MAVEN_HOME`
     - Value: `C:\Program Files\Maven\apache-maven-3.9.x`
   - Edit `PATH` variable, add: `%MAVEN_HOME%\bin`

**Verify Installation:**
```bash
mvn --version

# Expected Output:
# Apache Maven 3.9.x
# Maven home: C:\Program Files\Maven\apache-maven-3.9.x
```

---

### Step 3: Install PostgreSQL Database

**For Windows:**

1. Download from: https://www.postgresql.org/download/windows/
2. Run installer (postgresql-15.x-x64-exe)
3. Installation wizard:
   - **Installation Directory:** `C:\Program Files\PostgreSQL\15` (default)
   - **Password:** `root` (remember this!)
   - **Port:** `5432` (default)
   - **Locale:** English
4. Complete installation
5. Launch "pgAdmin 4" (admin tool)

**Verify Installation:**
```bash
# Open Command Prompt
psql --version

# Expected Output:
# psql (PostgreSQL) 15.x
```

---

### Step 4: Install Git (Optional but Recommended)

Download from: https://git-scm.com/download/win

---

### Step 5: Install IDE (IntelliJ IDEA or VS Code)

**Option 1: IntelliJ IDEA (Recommended for Java)**
- Download: https://www.jetbrains.com/idea/download/
- Choose Community Edition (free)
- Install and run

**Option 2: VS Code**
- Download: https://code.visualstudio.com/
- Install Extensions:
  - Extension Pack for Java
  - Spring Boot Extension Pack

---

## 🗄️ DATABASE SETUP

### Step 1: Create Database

**Using pgAdmin 4 (Graphical):**

1. Open pgAdmin 4 (should be in Start Menu)
2. Login with password you set during PostgreSQL installation
3. Expand "Servers" → Right-click "PostgreSQL 15"
4. Click "Create" → "Database"
5. Fill details:
   - **Database name:** `Blog-App`
   - **Owner:** postgres
6. Click "Create"

**OR Using Command Line:**

```bash
# Open Command Prompt and run:
psql -U postgres

# Enter password: root

# Then type:
CREATE DATABASE "Blog-App";

# Verify:
\l

# Exit:
\q
```

**Expected Output:**
```
postgres=# CREATE DATABASE "Blog-App";
CREATE DATABASE

postgres=# \l
                                      List of databases
   Name    |  Owner   | Encoding |   Collate   |    Ctype    | Access privileges
-----------+----------+----------+-------------+-------------+-------------------
 Blog-App  | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 |
 postgres  | postgres | UTF8     | en_US.UTF-8 | en_US.UTF-8 |
```

---

### Step 2: Verify Database Connection

Open pgAdmin 4 and:
1. Expand "Servers" → "PostgreSQL 15"
2. Expand "Databases"
3. You should see "Blog-App" in the list

✅ Database created successfully!

---

## ⚙️ PROJECT CONFIGURATION

### Step 1: Download/Clone Project

**Option 1: Clone from Git (if available)**
```bash
git clone <repository-url>
cd blog-app-apis
```

**Option 2: Manual Extract**
- Extract `blog-app-apis` folder to: `D:\New_Learning_2026\blog-app-apis`

---

### Step 2: Open in IDE

**For IntelliJ IDEA:**
1. Launch IntelliJ IDEA
2. Click "Open"
3. Navigate to `D:\New_Learning_2026\blog-app-apis\blog-app-apis`
4. Click "Open"
5. Wait for indexing to complete
6. Right-click `pom.xml` → "Run Maven" → "clean install"

**For VS Code:**
1. Launch VS Code
2. `File` → `Open Folder`
3. Select `blog-app-apis` folder
4. Open terminal: `Ctrl + `` (backtick)
5. Run: `mvn clean install`

---

### Step 3: Verify application.properties

**File Location:** `src/main/resources/application.properties`

**Check these values match your setup:**

```properties
# Server
spring.application.name=blog-app-apis
server.port=8080

# Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/Blog-App
spring.datasource.username=postgres
spring.datasource.password=root
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update  # ← Auto-creates tables!
spring.jpa.show-sql=true

# JWT
app.jwt.secret=mySuperSecretKeyForJwtAuthentication123456SecretKey
app.jwt.expiration.ms=18000000

# File uploads
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging
logging.level.org.springframework.security=DEBUG
```

**If password is different:**
- Change `spring.datasource.password=root` to your PostgreSQL password

---

### Step 4: Install Dependencies

Open terminal in project root and run:

```bash
mvn clean install

# This will:
# - Download all dependencies (first time takes 2-3 minutes)
# - Compile code
# - Run tests
# - Build project

# Expected output at end:
# BUILD SUCCESS
```

---

## ▶️ RUN APPLICATION

### Step 1: Start Application

**Using Maven:**
```bash
mvn spring-boot:run

# OR from IDE: Right-click BlogAppApisApplication.java → Run
```

**Watch for output:**
```
. . .   (Spring Boot ASCII art)

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/

2026-05-17 12:30:00.000  INFO 8888 --- [main] c.b.BlogAppApisApplication : Starting BlogAppApisApplication
...
2026-05-17 12:30:05.000  INFO 8888 --- [main] c.b.BlogAppApisApplication : Started BlogAppApisApplication in 5.123 seconds

=== LOGIN REQUEST INITIATED ===
...
```

**Wait for:**
```
Tomcat started on port(s): 8080 (http)
```

✅ **Application is now running!**

---

### Step 2: Verify Application is Running

Open browser and go to:
```
http://localhost:8080/swagger-ui/index.html
```

You should see Swagger API documentation!

---

## 👤 REGISTER FIRST USER

### Step 1: Open Postman

1. Download Postman from: https://www.postman.com/downloads/
2. Launch Postman
3. Create new request

---

### Step 2: Create User Registration Request

**Configure Request:**
```
Method: POST
URL: http://localhost:8080/api/v1/auth/register
Headers:
  - Content-Type: application/json

Body (JSON):
{
    "name": "John Doe",
    "email": "john@gmail.com",
    "password": "Admin123",
    "about": "Software Engineer"
}
```

**Step by step:**
1. Click "+" to create new tab
2. Select "POST" from dropdown
3. Paste URL: `http://localhost:8080/api/v1/auth/register`
4. Click "Headers" tab
5. Add:
   - Key: `Content-Type`
   - Value: `application/json`
6. Click "Body" tab
7. Select "raw"
8. Select "JSON" from dropdown
9. Paste JSON (above)
10. Click "Send"

---

### Step 3: Check Response

**Expected Response (HTTP 201 CREATED):**

```json
{
    "id": 1,
    "name": "John Doe",
    "email": "john@gmail.com",
    "password": "$2a$10$N9qo8uLOickgx2ZMRZoMe...",
    "about": "Software Engineer",
    "roles": [
        {
            "id": 2,
            "name": "NORMAL_USER"
        }
    ]
}
```

**Status Code:** `201 Created` ✅

**Note the fields:**
- `id`: 1 (your user ID)
- `email`: "john@gmail.com" (use for login)
- `password`: Encrypted with BCrypt (not plain text!)
- `roles`: NORMAL_USER (default role)

---

### Step 4: Register Second User (Admin)

Create another request with:

```json
{
    "name": "Admin User",
    "email": "admin@gmail.com",
    "password": "Admin123",
    "about": "System Administrator"
}
```

Send and note the response (id should be 2)

---

## 🔐 GENERATE JWT TOKEN

### Step 1: Create Login Request

**Configure new Postman request:**
```
Method: POST
URL: http://localhost:8080/api/v1/auth/login
Headers:
  - Content-Type: application/json

Body (JSON):
{
    "username": "john@gmail.com",
    "password": "Admin123"
}
```

---

### Step 2: Send Login Request

1. Select "POST"
2. Paste URL: `http://localhost:8080/api/v1/auth/login`
3. Add `Content-Type: application/json` header
4. Paste JSON body (above, use your registered email)
5. Click "Send"

---

### Step 3: Check Response

**Expected Response (HTTP 200 OK):**

```json
{
    "success": true,
    "message": "Authentication successful. JWT token generated.",
    "tokenType": "Bearer",
    "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huQGdtYWlsLmNvbSIsImlhdCI6MTcxNjAwMDAwMCwiZXhwIjoxNzE2MDE4MDAwfQ.tYgbI18M35iNh6xjorBkME3DGnVDRNBv0wfiOjdPKSkSbJ0cZjEpKos1cYJ02nar",
    "bearerToken": "Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqb2huQGdtYWlsLmNvbSIsImlhdCI6MTcxNjAwMDAwMCwiZXhwIjoxNzE2MDE4MDAwfQ.tYgbI18M35iNh6xjorBkME3DGnVDRNBv0wfiOjdPKSkSbJ0cZjEpKos1cYJ02nar",
    "username": "john@gmail.com",
    "expiresIn": 18000000,
    "timestamp": 1716000000000
}
```

**Key fields:**
- `token`: Raw JWT (without Bearer prefix)
- `bearerToken`: Ready-to-use with "Bearer " prefix
- `expiresIn`: 18000000 ms = 5 hours
- `timestamp`: When token was created

---

### Step 4: Save Token for Reuse

**In Postman - Copy token:**

1. Copy the entire `bearerToken` value:
   ```
   Bearer eyJhbGciOiJIUzM4NCJ9.eyJzdWIi...
   ```

2. OR use Postman environment variables:
   - Click "Environment" dropdown
   - Create new environment "Blog-App-Dev"
   - Add variable:
     - Name: `token`
     - Value: (paste bearerToken)
   - Save

---

## 🌐 USE API WITH TOKEN

### Step 1: Get All Users (Protected Endpoint)

**Configure Request:**
```
Method: GET
URL: http://localhost:8080/api/users/AllUsers
Headers:
  - Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
  - Accept: application/json
```

**Postman Steps:**
1. Create new request
2. Select "GET"
3. Paste URL: `http://localhost:8080/api/users/AllUsers`
4. Go to "Headers" tab
5. Add header:
   - Key: `Authorization`
   - Value: `Bearer eyJhbGciOiJIUzM4NCJ9...` (paste your token)
6. Click "Send"

---

### Step 2: Check Response

**Expected Response (HTTP 200 OK):**

```json
[
    {
        "id": 1,
        "name": "John Doe",
        "email": "john@gmail.com",
        "password": "$2a$10$...",
        "about": "Software Engineer",
        "roles": [
            {
                "id": 2,
                "name": "NORMAL_USER"
            }
        ]
    },
    {
        "id": 2,
        "name": "Admin User",
        "email": "admin@gmail.com",
        "password": "$2a$10$...",
        "about": "System Administrator",
        "roles": []
    }
]
```

✅ Got list of all users!

---

### Step 3: Create Blog Post

**Configure Request:**
```
Method: POST
URL: http://localhost:8080/api/posts/create
Headers:
  - Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
  - Content-Type: application/json

Body:
{
    "title": "My First Blog Post",
    "content": "This is my first blog post using the Blog App API!",
    "imageName": "post1.jpg",
    "categoryId": 1
}
```

⚠️ **Note:** categoryId 1 may not exist yet. Create a category first or use existing.

---

### Step 4: Get Single User

**Configure Request:**
```
Method: GET
URL: http://localhost:8080/api/users/1
Headers:
  - Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

**Response:**
```json
{
    "id": 1,
    "name": "John Doe",
    "email": "john@gmail.com",
    "about": "Software Engineer"
}
```

---

### Step 5: Update User

**Configure Request:**
```
Method: PUT
URL: http://localhost:8080/api/users/updateUser/1
Headers:
  - Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
  - Content-Type: application/json

Body:
{
    "name": "John Doe Updated",
    "email": "john.updated@gmail.com",
    "about": "Updated bio - Senior Engineer"
}
```

---

### Step 6: Delete User

**Configure Request:**
```
Method: DELETE
URL: http://localhost:8080/api/users/deleteUser/2
Headers:
  - Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

**Response:**
```json
{
    "message": "User Deleted Successfully",
    "success": true
}
```

---

## 🐛 TROUBLESHOOTING

### Issue 1: "Connection refused" Error

**Problem:**
```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```

**Solution:**
1. Check PostgreSQL is running:
   - Windows: Services → PostgreSQL should be "Running"
   - Or: Start → Search "Services" → Find PostgreSQL → Right-click → Start
2. Verify database exists:
   - Open pgAdmin 4
   - Look for "Blog-App" in Databases
3. Check connection details in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/Blog-App
   spring.datasource.username=postgres
   spring.datasource.password=root
   ```

---

### Issue 2: "401 Unauthorized" Error

**Problem:**
```
{
    "error": "Unauthorized",
    "status": 401
}
```

**Solution:**
1. Check token is included in Authorization header
2. Check token not expired (5 hour expiration)
3. Generate new token:
   ```
   POST /api/v1/auth/login
   ```
4. Copy entire `bearerToken` value including "Bearer "
5. Make sure format is correct:
   ```
   Authorization: Bearer eyJhbGci...
   ```
   NOT:
   ```
   Authorization: eyJhbGci...  ← Missing "Bearer"
   ```

---

### Issue 3: "400 Bad Request" Error

**Problem:**
```
{
    "error": "Bad Request",
    "message": "Required request body is missing"
}
```

**Solution:**
1. Check request body is JSON
2. In Postman:
   - Click "Body" tab
   - Select "raw"
   - Select "JSON" from dropdown
   - Paste JSON
3. Check JSON syntax is valid:
   - Use: https://jsonlint.com/
4. Verify Content-Type header:
   ```
   Content-Type: application/json
   ```

---

### Issue 4: "500 Internal Server Error"

**Problem:**
```
{
    "error": "Internal Server Error",
    "message": "..."
}
```

**Solution:**
1. Check application logs in IDE console
2. Look for specific error message
3. Common causes:
   - Database not running
   - Wrong database URL
   - Missing dependencies (run `mvn clean install`)
   - Port 8080 already in use

**To find what's using port 8080:**
```bash
# PowerShell
netstat -ano | findstr :8080

# Kill process (if needed)
taskkill /PID <PID> /F
```

---

### Issue 5: "Table doesn't exist"

**Problem:**
```
org.postgresql.util.PSQLException: ERROR: relation "users" does not exist
```

**Solution:**
1. Check `spring.jpa.hibernate.ddl-auto=update` in properties
2. Restart application (Hibernate will create tables)
3. Or manually create tables (see database setup section)

---

### Issue 6: Port 8080 Already in Use

**Problem:**
```
Address already in use: bind failed
```

**Solution:**
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process
taskkill /PID <process_id> /F

# Or change port in application.properties
server.port=8081
```

---

### Issue 7: Maven Build Fails

**Problem:**
```
[ERROR] BUILD FAILURE
```

**Solutions:**
1. **Clear Maven cache:**
   ```bash
   mvn clean install
   ```

2. **Delete .m2 folder:**
   ```
   C:\Users\<YourUsername>\.m2\repository
   ```
   Then run: `mvn clean install`

3. **Check internet connection** (downloading dependencies)

4. **Try building without tests:**
   ```bash
   mvn clean install -DskipTests
   ```

---

### Issue 8: "Cannot find symbol" Error

**Problem:**
```
cannot find symbol
  symbol:   class User
```

**Solution:**
1. Run: `mvn clean compile`
2. In IDE:
   - Right-click project → Maven → Reload
   - Or: Build → Rebuild Project

---

## ✅ VERIFICATION CHECKLIST

After setup, verify everything works:

- [ ] Java installed: `java -version` shows Java 21
- [ ] Maven installed: `mvn --version` shows 3.9.x
- [ ] PostgreSQL running: Can connect in pgAdmin
- [ ] Database created: "Blog-App" visible in pgAdmin
- [ ] Application starts: No errors on startup
- [ ] Application accessible: http://localhost:8080/swagger-ui/index.html loads
- [ ] User registered: POST /api/v1/auth/register returns 201
- [ ] User can login: POST /api/v1/auth/login returns token
- [ ] Token works: GET /api/users/AllUsers with token returns 200

---

## 🚀 QUICK START SUMMARY

**One-time setup (30 minutes):**
```bash
# 1. Install Java, Maven, PostgreSQL (see steps above)

# 2. Create database
createdb -U postgres Blog-App

# 3. Navigate to project
cd D:\New_Learning_2026\blog-app-apis\blog-app-apis

# 4. Install dependencies
mvn clean install

# 5. Start application
mvn spring-boot:run

# 6. Application running at http://localhost:8080
```

**Then in Postman:**
```bash
# 1. Register user
POST /api/v1/auth/register
{
    "name": "John",
    "email": "john@gmail.com",
    "password": "Admin123",
    "about": "Bio"
}

# 2. Login and get token
POST /api/v1/auth/login
{
    "username": "john@gmail.com",
    "password": "Admin123"
}
# Copy: bearerToken

# 3. Use token in requests
GET /api/users/AllUsers
Header: Authorization: <bearerToken>
```

---

## 📞 COMMON QUESTIONS

### Q: How often do I need to restart the application?

**A:** Use Spring Boot Dev Tools:
- Automatic restart on file changes
- Reload dependencies if needed
- Just save file and changes apply!

---

### Q: Can I change the port from 8080?

**A:** Yes, in `application.properties`:
```properties
server.port=9090
# Access at: http://localhost:9090
```

---

### Q: Where are logs stored?

**A:** In IDE console when running with `mvn spring-boot:run`
- Or create `logs` folder in project root for file logging

---

### Q: How do I reset the database?

**A:** 

```bash
# Drop and recreate:
# In application.properties, temporarily set:
spring.jpa.hibernate.ddl-auto=create-drop

# Restart application (drops all tables, recreates empty ones)

# Then change back to:
spring.jpa.hibernate.ddl-auto=update
```

---

### Q: Token expired, how to get new one?

**A:** Call login endpoint again:
```
POST /api/v1/auth/login
{
    "username": "john@gmail.com",
    "password": "Admin123"
}
```

Get new token and use in Authorization header.

---

## 📚 NEXT STEPS

After successful setup:
1. Explore all API endpoints using Swagger
2. Read COMPLETE-PROJECT-ARCHITECTURE.md for deep understanding
3. Study security implementation
4. Try modifying and adding new features
5. Practice authentication/authorization flows

---

## 🎓 LEARNING RESOURCES

- Spring Boot Docs: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- JWT Tutorial: https://jwt.io/
- PostgreSQL Docs: https://www.postgresql.org/docs/
- JPA/Hibernate: https://hibernate.org/orm/

---

**Setup Date:** May 17, 2026  
**Last Updated:** Today  
**Status:** Ready for Local Development ✅  
**Estimated Setup Time:** 30-45 minutes

---

## 📞 NEED HELP?

If stuck:
1. Check console logs (details about errors)
2. Review Troubleshooting section
3. Verify each prerequisite is installed
4. Check application.properties has correct values
5. Make sure database is running
6. Restart application

**Remember:** First time setup takes time, but subsequent runs are much faster! 🚀

