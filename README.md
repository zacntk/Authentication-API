# Authentication-API 🔒
Welcome to the Authentication API project! This powerful and secure API is meticulously developed using Java Spring Boot and MySQL, offering a comprehensive solution for user authentication and credential management. Designed to be both robust and scalable, this API provides a RESTful interface that ensures the highest standards of security during user signup and signin processes. By leveraging advanced features like unique email verification, password hashing, and JSON Web Token (JWT) protection, this API safeguards user data while maintaining seamless communication between the server and client. Whether you're looking to integrate it into an existing application or use it as a foundation for new projects, the Authentication API is equipped to handle your authentication needs with efficiency and reliability.
<br>

# Project Features 🚀
- [x] **Unique Email Verification**
  - Ensures that each email address is unique during the registration process.

- [x] **Password Security Enhancement**
  - Implements password hashing to secure passwords before storing them in MySQL.

- [x] **Enhanced Password Requirements**
  - Enforces a minimum password length of 8 characters, including at least one uppercase letter, one number, and one special character.

- [ ] **Password and Email Management**
  - Enable users to securely update their email addresses and passwords.

- [x] **JSON Web Token (JWT) Protection**
  - Securely transmit and protect access tokens/refresh tokens between server and client using JSON Web Tokens.


## How to Run 🗒  

1. **Clone the repository:**
    ```bash
    git clone https://github.com/zacntk/Authentication-API.git
    ```

2. **Navigate to the code directory:**
    ```bash
    cd Authentication-API
    ```

3. **Configure Database Settings:**

   Open the `application.properties` file and update the following properties to match your MySQL configuration:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/{YourSchema}?useSSL=false
    spring.datasource.username={YourUsername}
    spring.datasource.password={YourPassword}

    spring.jpa.hibernate.ddl-auto=update

    spring.jpa.open-in-view=false
    ```
4. **Set Access Token Secret And Refresh Token Secret **
    Open the `token.env` file and update the following properties to match your `Access Token Secret` and `Refresh Token Secret`:
   ```properties
    ACCESS_TOKEN_SECRET={YourAccessTokenSecret}
    REFRESH_TOKEN_SECRET={YourRefreshTokenSecret}
    ```

   
6. **Start the API:**

   Right-click on your main class (the one annotated with `@SpringBootApplication`) and select **Run As > Spring Boot App**.

## HOW TO USE 🔎

### 1. Sign Up
**Method:** `Post`

**Endpoint:** `{path}/api/v1/auth/signup`

**Request Body:**
```json
{
  "email": "example@example.com",
  "password": "Example1234*"
}
```

**Response Body:**
- Success (201 Created)
```json
{
    "message": "User sign up successfully",
    "status": "success"
}
```
- Invalid email address (400 Bad Request)
```json
{
    "message": "Email is already in use",
    "status": "error"
}
```
- Password is weak (400 Bad Request)
```json
{
    "message": "Password is weak",
    "status": "error"
}
```
- Email is already in use (409 Conflict)
```json
{
    "message": "Email is already in use",
    "status": "error"
}
```

### 2. Sign In
**Method:** `Post`

**Endpoint:** `{path}/api/v1/auth/signin`

**Request Body:**
```json
{
  "email": "example@example.com",
  "password": "Example1234*"
}
```

**Response Body:**
- Success (200 OK)
```json
{
    "status": "success",
    "message": "User sign in successfully.",
    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJJZCI6MTAzLCJlbWFpbCI6ImV4YW1wbGVAZXhhbXBsZS5jb20iLCJleHAiOjE3MjQxNDE1ODl9.bMpSAv5Ct5YlsCKZY0LVbRvVyPgqe6O_F3_F6r3Zmjg",
    "refresh_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJJZCI6MTAzLCJlbWFpbCI6ImV4YW1wbGVAZXhhbXBsZS5jb20iLCJleHAiOjE3MjQyMjQzODl9.HRdaazw7b4j_hRE8gHuqJGEUi6GkcfVgxD_dKwkojnk"
}
```
- Invalid password (401 Unauthorized)
```json
{
    "message": "Invalid password",
    "status": "error"
}
```

- Email not found (401 Unauthorized)
```json
{
    "message": "Email not found",
    "status": "error"
}
```

### 3. Access Data With Access Token
**Method:** `Get`

**Endpoint:** `{path}/api/v1/auth/data`

**Authorization:**
- **Type:** Bearer Token
- **Token:** Your access token received from the sign-in process.

**Headers:**
```http
Authorization: Bearer <your_access_token>
```

**Response Body:**
- Success (200 OK)
```json
{
    "role": "User",
    "email": "example@example.com"
}
```

- Don't Have Authorization Header (400 Bad Request)
```json
{
    "timestamp": "2024-08-20T07:32:28.753+00:00",
    "status": 400,
    "error": "Bad Request",
    "path": "/api/v1/auth/data"
}
```

- Invalid Token (400 Bad Request)
```json
{
    "message": "Token is not valid"
}
```

### 4. Refresh Access Token With Refresh Token
**Method:** `Post`

**Endpoint:** `{path}/api/v1/auth/refresh`

**Authorization:**
- **Type:** Bearer Token
- **Token:** Your access token received from the sign-in process.

**Headers:**
```http
Authorization: Bearer <your_refresh_token>
```

**Response Body:**
- Success (200 OK)
```json
{
    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJJZCI6MTAzLCJlbWFpbCI6ImV4YW1wbGVAZXhhbXBsZS5jb20iLCJleHAiOjE3MjQxNDMyNjF9.-oqRqksEHtOZl6JbjwS-VFqBTTHKD-INWIhnYzVZY9Q",
    "refresh_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJJZCI6MTAzLCJlbWFpbCI6ImV4YW1wbGVAZXhhbXBsZS5jb20iLCJleHAiOjE3MjQyMjYwNjF9.56XDHYx3e6Rz4Kn7uqxHydXgYpBBiSXMmCq0fq9j1wA"
}
```

- Don't Have Authorization Header (400 Bad Request)
```json
{
    "timestamp": "2024-08-20T07:41:29.268+00:00",
    "status": 400,
    "error": "Bad Request",
    "path": "/api/v1/auth/refresh"
}
```

- Invalid Token (400 Bad Request)
```json
{
    "message": "Token is not valid"
}
```

---

🙌 Thank you for your interest in my project! 🙌
#
