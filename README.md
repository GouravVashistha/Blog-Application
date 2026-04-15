# 🚀 Blog Application (Spring Boot)

## 📌 Overview

This is a **Spring Boot-based Blog Application** that allows users to create, manage, and interact with blog posts.
The project follows a layered architecture using **Controller → Service → Repository → Entity → DTO** pattern.

It is designed to demonstrate real-world backend development practices including REST APIs, DTO mapping, exception
handling, and database integration.

---

## ✨ Features

* 👤 User Management (Create, Update, Delete, View)
* 📝 Create, Update, Delete Blog Posts
* 📂 Categorize Posts
* 🔍 Get Posts by Category / User
* 💬 Comment System (basic)
* ⚡ Global Exception Handling
* 🔄 DTO ↔ Entity Mapping (ModelMapper)
* 📄 RESTful API Design

---

## 🛠️ Tech Stack

* **Backend**: Spring Boot 3.3.x
* **Language**: Java 21
* **Database**: PostgreSQL
* **ORM**: Spring Data JPA (Hibernate)
* **Build Tool**: Maven
* **Mapping**: ModelMapper
* **Validation**: Jakarta Validation
* **API Testing**: Postman / Swagger (SpringDoc)

---

## 📁 Project Structure

```
com.blog_app_apis
│
├── controllers        # REST Controllers
├── service            # Service Interfaces
├── serviceImpl        # Service Implementations
├── repository         # JPA Repositories
├── entity             # Database Entities
├── dtos               # Data Transfer Objects
├── exceptions         # Custom Exceptions & Handler
└── config             # Configuration classes
```

---

## ⚙️ Setup & Installation

### 1️⃣ Clone Repository

```bash
git clone https://github.com/GouravVashistha/Blog-Application.git
cd Blog-Application
```

### 2️⃣ Configure Database (PostgreSQL)

Update `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blog_app
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

---

### 3️⃣ Build Project

```bash
mvn clean install
```

---

### 4️⃣ Run Application

```bash
mvn spring-boot:run
```

👉 Application will start at:

```
http://localhost:8080
```

---

## 📡 API Endpoints

### 🔹 User APIs

* `POST /api/users` → Create User
* `GET /api/users` → Get All Users
* `GET /api/users/{id}` → Get User by ID
* `PUT /api/users/{id}` → Update User
* `DELETE /api/users/{id}` → Delete User

---

### 🔹 Category APIs

* `POST /api/category`
* `GET /api/category`
* `GET /api/category/{id}`
* `PUT /api/category/{id}`
* `DELETE /api/category/{id}`

---

### 🔹 Post APIs

* `POST /api/posts`
* `GET /api/posts`
* `GET /api/posts/{id}`
* `GET /api/posts/category/{categoryId}`
* `GET /api/posts/user/{userId}`
* `PUT /api/posts/{id}`
* `DELETE /api/posts/{id}`

---

## 🧠 Architecture

The application follows **Layered Architecture**:

```
Controller → Service → Repository → Database
             ↓
            DTO
```

* **Controller** → Handles HTTP requests
* **Service** → Business logic
* **Repository** → Database interaction
* **DTO** → Data transfer (no direct entity exposure)

---

## ⚠️ Important Notes

* Passwords should not be exposed in API responses (future improvement)
* Security (JWT / Spring Security) will be added in next phase
* Pagination & Sorting can be added for scalability

---

## 🚀 Future Enhancements

* 🔐 Spring Security + JWT Authentication
* 📊 Pagination & Sorting
* 🔎 Search & Filtering
* 📦 Dockerization
* 📈 Logging & Monitoring

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to GitHub
5. Create Pull Request

---

## 📧 Contact

* GitHub: https://github.com/GouravVashistha
* Email: (add your email here)

---

## ⭐ Final Note

This project is built as part of backend development learning and is continuously being improved toward *
*production-level standards**.

---
