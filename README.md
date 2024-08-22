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

- [x] **Password and Email Management**
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
    Open the `token.env` file and update the following properties to match your `Access Token Secret`, `Refresh Token Secret`, `Issuer` and `Audience`:
   ```properties
    ACCESS_TOKEN_SECRET={YourAccessTokenSecret}
    REFRESH_TOKEN_SECRET={YourRefreshTokenSecret}
    ISSUER={YourAppName}
    AUDIENCE={YourAppAudience}
    ```

   
6. **Start the API:**

   Right-click on your main class (the one annotated with `@SpringBootApplication`) and select **Run As > Spring Boot App**.

## HOW TO USE 🔎

### 1. Sign Up
**Method:** `Post`

**Endpoint:** `{path}/v1/auth/signup`

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
    "status": "success",
    "message": "User sign up successfully"
}
```
- Invalid email address (400 Bad Request)
```json
{
    "status": "error"
    "message": "Email is already in use"
}
```
- Password is weak (400 Bad Request)
```json
{
    "status": "error",
    "message": "Password is weak"
}
```
- Email is already in use (409 Conflict)
```json
{
    "status": "error",
    "message": "Email is already in use"
}
```

### 2. Sign In
**Method:** `Post`

**Endpoint:** `{path}/v1/auth/signin`

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
    "access_token": <access_token>
    "refresh_token": <refresh_token>
}
```
- Invalid password (401 Unauthorized)
```json
{
    "status": "error",
    "message": "Invalid password"
}
```

- Email not found (401 Unauthorized)
```json
{
    "status": "error",
    "message": "Email not found"
}
```

### 3. Validate Access Token
**Method:** `Get`

**Endpoint:** `{path}/v1/auth/validate`

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
    "status": "success",
    "message": "Token is valid"
}
```

- Don't Have Authorization Header (400 Bad Request)
```json
{
    "timestamp": "2024-08-21T05:12:22.502+00:00",
    "status": 400,
    "error": "Bad Request",
    "path": "/api/v1/auth"
}
```

- Invalid Token (400 Bad Request)
```json
{
    "status": "error",
    "message": "Token is invalid"
}
```

### 4. Refresh Access Token With Refresh Token
**Method:** `Post`

**Endpoint:** `{path}/v1/auth/refresh`

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
    "access_token": <access_token>
    "refresh_token": <refresh_token>
}
```

- Don't Have Authorization Header (400 Bad Request)
```json
{
    "timestamp": "2024-08-21T05:14:00.205+00:00",
    "status": 400,
    "error": "Bad Request",
    "path": "/api/v1/auth/refresh"
}
```

- Invalid Token (400 Bad Request)
```json
{
    "status": "error",
    "message": "Token is invalid"
}
```

### 5. Change Email
**Method:** `Put`

**Endpoint:** `{path}/v1/auth/changeEmail`

**Authorization:**
- **Type:** Bearer Token
- **Token:** Your access token received from the sign-in process.

**Headers:**
```http
Authorization: Bearer <your_access_token>
```

**Request Body:**
```json
{
  "newEmail": "newExample@example.com",
  "password": "Example1234*"
}
```

**Response Body:**
- Success (200 OK)
```json
{
    "status": "success",
    "message": "Email changed successfully",
    "access_token": <access_token>,
    "refresh_token": <refresh_token>
}
```

- Don't Have Authorization Header (400 Bad Request)
```json
{
    "timestamp": "2024-08-22T07:10:54.471+00:00",
    "status": 400,
    "error": "Bad Request",
    "path": "/v1/auth/changeEmail"
}
```

- Invalid Token (400 Bad Request)
```json
{
    "status": "error",
    "message": "Token is invalid"
}
```

### 6. Change Password
**Method:** `Put`

**Endpoint:** `{path}/v1/auth/changePassword`

**Authorization:**
- **Type:** Bearer Token
- **Token:** Your access token received from the sign-in process.

**Headers:**
```http
Authorization: Bearer <your_access_token>
```

**Request Body:**
```json
{
  "oldPassword": "Example1234*",
  "newPassword": "newExample1234*"
}
```

**Response Body:**
- Success (200 OK)
```json
{
    "status": "success",
    "message": "Password changed successfully"
}
```

- Don't Have Authorization Header (400 Bad Request)
```json
{
    "timestamp": "2024-08-22T07:12:32.447+00:00",
    "status": 400,
    "error": "Bad Request",
    "path": "/v1/auth/changePassword"
}
```

- Invalid Token (400 Bad Request)
```json
{
    "status": "error",
    "message": "Token is invalid"
}
```

### 6. Delete User
**Method:** `Delete`

**Endpoint:** `{path}/v1/auth/deleteUser`

**Authorization:**
- **Type:** Bearer Token
- **Token:** Your access token received from the sign-in process.

**Headers:**
```http
Authorization: Bearer <your_access_token>
```

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
    "message": "User deleted successfully"
}
```

- User Or Password Incorrect (401 Unauthorized)
```json
{
    "status": "error",
    "message": "Incorrect email or password"
}
```

- Don't Have Authorization Header (400 Bad Request)
```json
{
    "timestamp": "2024-08-22T07:12:32.447+00:00",
    "status": 400,
    "error": "Bad Request",
    "path": "/v1/auth/changePassword"
}
```

- Invalid Token (400 Bad Request)
```json
{
    "status": "error",
    "message": "Token is invalid"
}
```
---

🙌 Thank you for your interest in my project! 🙌
#
