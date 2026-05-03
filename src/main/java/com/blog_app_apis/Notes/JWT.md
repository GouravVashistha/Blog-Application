# 🔐 JWT (JSON Web Token) Authentication

## 1. What is JWT?

JWT (JSON Web Token) is an open standard (**RFC 7519**) used to securely transmit information between two parties as a
JSON object.

It is:

- Compact (URL-safe)
- Self-contained (contains user data)
- Digitally signed (to ensure integrity)

JWT is commonly used for:

- Authentication
- Authorization
- Information exchange

---

## 2. Key Characteristics

| Feature        | Description                           |
|----------------|---------------------------------------|
| Stateless      | No session stored on server           |
| Self-contained | Token contains all required user info |
| Secure         | Signed using secret/private key       |
| Compact        | Easy to send via HTTP headers         |

---

## 3. ⚠️ Reality Check (Important)

JWT is **NOT always the best solution**.

| Myth                       | Reality                      |
|----------------------------|------------------------------|
| JWT is always best         | ❌ Not true                   |
| Stateless is always better | ❌ Depends on use case        |
| JWT replaces sessions      | ❌ Only in some architectures |

👉 Use JWT when:

- You need stateless APIs
- You have microservices
- You want scalability

👉 Avoid JWT when:

- You need easy logout/session invalidation
- Security requirements are strict (token revocation needed)

---

## 4. Structure of JWT

A JWT has **3 parts**, separated by dots (`.`):

1. **Header**: Contains metadata about the token (type and signing algorithm).
2. **Payload**: Contains the claims (user data and other info).
3. **Signature**: Used to verify the token's integrity.
4. **Example**:
   ```eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c```
    - **Header**: `{"alg": "HS256", "typ": "JWT"}`
    - **Payload**: `{"sub": "1234567890", "name": "John Doe", "iat": 1516239022}`
    - **Signature**: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)

---

## 🧠 5. High-Level Flow

Client → Login → JWT Generated → Store Token → Send Token → Filter Validates → Access Granted


---

## Step-by-Step Workflow

---

### 🔹 Step 1: User Login Request

Client sends credentials:

```http
POST /login
Content-Type: application/json

{
  "username": "gourav",
  "password": "1234"
}
