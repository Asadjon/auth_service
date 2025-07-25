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

```html
Authorization: Bearer <access_token>
```

---

## ⚙️ Setup Instruction
> You can view the installation manual in the [transaction-management-system](https://github.com/Asadjon/transaction-management-system/blob/master/README.md) repository.

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

**🔐 Register** `POST /api/v1/auth/register`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "yourPassword123",
  "role": "USER"
}
```
**Response:** `User registered successfully. Please check your email for confirmation.`

**📩 Email Confirmation** `GET /api/v1/auth/confirm?token=yourToken`

**Response:** ``` Email confirmed successfully. ```

**🔄 Resend Verification Email** `POST /api/v1/auth/resend-verification`

**Request Body:**
```json
{
    "email": "john@example.com"
}
```
**Response:** `Verification email resent successfully.`

**🔑 Login** `POST /api/v1/auth/login`

**Request Body:**
```json
{
    "email": "john@example.com",
    "password": "yourPassword123"
}
```
**Response:**
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "john@example.com",
    "role": "USER"
} 
```

**🛡️ Validate Token** `GET  /api/v1/auth/validate?token=yourAccessToken`

**Response:**
```json
{
    "id": "1",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "role": "USER"
} 
```

**👤 Validate User by ID** `GET  /api/v1/auth/validate/{userId}`

**Response:**
```json
{
    "id": "1",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "role": "USER"
} 
```

**🔒 Forgot Password** `POST /api/v1/auth/forgot-password`

**Request Body:**
```json
{
    "email": "john@example.com"
}
```
**Response:** `Password reset link has been sent to your email.`

**🔁 Reset Password** `POST /api/v1/auth/reset-password`

**Request Body:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "newPassword": "yourNewPassword123",
    "confirmPassword": "yourNewPassword123"
}
```
**Response:** ``` Password successfully reset ```