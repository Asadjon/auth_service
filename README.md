# Auth Service

Handles user authentication, registration, login, email verification, password reset, and role-based authorization.

---

## 📜 Overview

This service provides authentication and authorization for the Transaction Management System. It issues JWT tokens and verifies them across other services through the API Gateway.

---

## 🚀 Tech Stack

- Java 21
- Spring Boot 3.5.3
- Spring Security
- Spring Validation
- PostgreSQL
- Redis
- Thymeleaf
- JWT (JSON Web Token)
- Docker

---

## 🔐 Authentication

The service uses **JWT-based authentication**. After logging in, users receive an access token and a refresh token. All secured requests must include the access token in the header:

```http
Authorization: Bearer <access_token>
```

---

## 📦 Clone All Required Repositories
To run the full Transaction Management System, you'll need to clone each microservice repository into a common workspace folder. You can do it manually or with the following commands:

### 1. Create a project directory
   ```
   mkdir transaction-system && cd transaction-system
   ```

### > 2. Clone all required services
> * [auth_service](https://github.com/Asadjon/balance_service.git) this repository
> * [balance_service](https://github.com/Asadjon/balance_service.git)
> * [transaction_service](https://github.com/Asadjon/transaction_service.git)
> * [api_gateway](https://github.com/Asadjon/api_gateway.git)

---

## 🚀 Running with Docker
### 1. Create app-network (only once)
If you haven't created the custom network yet, run:
```
docker network create app-network
```

### 2. Build and start the container
Inside the directory where your Dockerfile and docker-compose.yml are located (e.g., auth_service), run:
```
docker-compose up --build
```

### 3. Useful Docker commands
Inspect all containers connected to app-network:
```docker
docker network inspect app-network
```

Stop and remove the container(s):
```
docker-compose down
```

---

## 🔁 API Endpoints

| Method | Endpoint                            | Description                          | Request Body                |
|--------|-------------------------------------|--------------------------------------|-----------------------------|
| POST   | `/api/v1/auth/register`             | Register a new user                  | `RegisterRequest`           |
| GET    | `/api/v1/auth/confirm`              | Confirm email verification token     | `token` as request param    |
| POST   | `/api/v1/auth/resend-verification`  | Resend email verification link       | `ResendVerificationRequest` |
| POST   | `/api/v1/auth/login`                | Login and receive JWT tokens         | `AuthRequest`               |
| GET    | `/api/v1/auth/validate`             | Validate JWT token and get user info | `token`as request param     |
| GET    | `/api/v1/auth/validate/{userId}`    | Validate user by user ID             | Path variable: `userId`     |
| POST   | `/api/v1/auth/forgot-password`      | Send password reset link             | `ForgotPasswordRequest`     |
| POST   | `/api/v1/auth/reset-password`       | Reset password with token            | `ResetPasswordRequest`      |


---

## 📦 Request & Response Body Structures

### 🔐 Register `POST /api/v1/auth/register`

#### Request Body:
```json
{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "yourPassword123",
    "role": "USER"
}
```
#### Response: ``` User registered successfully. Please check your email for confirmation. ```

### 📩 Email Confirmation `GET /api/v1/auth/confirm?token=yourToken`
#### Response: ``` Email confirmed successfully. ```

### 🔄 Resend Verification Email `POST /api/v1/auth/resend-verification`

#### Request Body:
```json
{
    "email": "john@example.com"
}
```
#### Response: ``` Verification email resent successfully.```

### 🔑 Login `POST /api/v1/auth/login`

#### Request Body:
```json
{
    "email": "john@example.com",
    "password": "yourPassword123",
}
```
#### Response: 
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "john@example.com",
    "role": "USER"
} 
```

### 🛡️ Validate Token `GET  /api/v1/auth/validate?token=yourAccessToken`
#### Response: 
```json
{
    "id": "1",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "role": "USER"
} 
```

### 👤 Validate User by ID `GET  /api/v1/auth/validate/{userId}`
#### Response: 
```json
{
    "id": "1",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "role": "USER"
} 
```

### 🔒 Forgot Password `POST /api/v1/auth/forgot-password`

#### Request Body:
```json
{
    "email": "john@example.com",
}
```
#### Response: ``` Password reset link has been sent to your email. ```

### 🔁 Reset Password `POST /api/v1/auth/reset-password`

#### Request Body:
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "newPassword": "yourNewPassword123",
    "confirmPassword": "yourNewPassword123"
}
```
#### Response: ``` Password successfully reset ```