# API Documentation

## User Microservices

This document describes the REST APIs exposed by the User Microservice and accessed through the API Gateway.

The API Gateway acts as the public entry point for client requests.

---

## Base URLs

### API Gateway

```text
http://localhost:8080
```

### User Microservice

```text
http://localhost:8081
```

> Clients should use the API Gateway (`8080`) instead of directly accessing the User Microservice.

---

# API Overview

| Method | Endpoint | Authentication | Description |
|---|---|---|---|
| POST | `/api/users/register` | Public | Register a new user |
| POST | `/api/users/login` | Public | Authenticate user and generate JWT |
| GET | `/api/users` | JWT Required | Get all users |
| GET | `/api/users/{id}` | JWT Required | Get user by ID |
| PUT | `/api/users/{id}` | JWT Required | Update user |
| DELETE | `/api/users/{id}` | JWT Required | Delete user |

---

# Authentication

The application uses stateless JWT authentication.

Protected endpoints require the following HTTP header:

```http
Authorization: Bearer <JWT_TOKEN>
```

The following endpoints are publicly accessible:

```text
POST /api/users/register
POST /api/users/login
```

All other User APIs require a valid JWT.

---

# 1. Register User

Creates a new user account.

## Endpoint

```http
POST /api/users/register
```

## Gateway URL

```text
http://localhost:8080/api/users/register
```

## Authentication

```text
Not Required
```

## Request Headers

```http
Content-Type: application/json
```

## Request Body

```json
{
  "name": "Gaurav",
  "email": "gaurav@example.com",
  "password": "password123"
}
```

## Request Fields

| Field | Type | Required | Description |
|---|---|---|---|
| `name` | String | Yes | User name |
| `email` | String | Yes | User email address |
| `password` | String | Yes | User password |

## Success Response

```json
{
  "id": 1,
  "name": "Gaurav",
  "email": "gaurav@example.com",
  "role": "USER"
}
```

## Registration Flow

```text
Client
  |
  | POST /api/users/register
  v
API Gateway :8080
  |
  v
User Microservice :8081
  |
  +-- Validate Request
  |
  +-- Check Duplicate Email
  |
  +-- BCrypt Password Hashing
  |
  +-- Save User
  |
  +-- Publish USER_CREATED
           |
           v
         NATS
           |
           v
Notification Microservice
```

After successful registration, a `UserCreatedEvent` is published to the NATS subject:

```text
user.created
```

---

# 2. Login

Authenticates an existing user and generates a JWT token.

## Endpoint

```http
POST /api/users/login
```

## Gateway URL

```text
http://localhost:8080/api/users/login
```

## Authentication

```text
Not Required
```

## Request Headers

```http
Content-Type: application/json
```

## Request Body

```json
{
  "email": "gaurav@example.com",
  "password": "password123"
}
```

## Request Fields

| Field | Type | Required | Description |
|---|---|---|---|
| `email` | String | Yes | User email address |
| `password` | String | Yes | User password |

## Success Response

```json
{
  "token": "<JWT_TOKEN>",
  "email": "gaurav@example.com"
}
```

The returned JWT must be included in the `Authorization` header when accessing protected endpoints.

Example:

```http
Authorization: Bearer <JWT_TOKEN>
```

## Authentication Flow

```text
Client
  |
  | Email + Password
  v
User Microservice
  |
  +-- Find User
  |
  +-- Verify BCrypt Password
  |
  +-- Generate JWT
  |
  v
Client
```

---

# 3. Get All Users

Returns all registered users.

## Endpoint

```http
GET /api/users
```

## Gateway URL

```text
http://localhost:8080/api/users
```

## Authentication

```text
JWT Required
```

## Request Headers

```http
Authorization: Bearer <JWT_TOKEN>
```

## Success Response

```json
[
  {
    "id": 1,
    "name": "Gaurav",
    "email": "gaurav@example.com",
    "role": "USER"
  },
  {
    "id": 2,
    "name": "Rahul",
    "email": "rahul@example.com",
    "role": "USER"
  }
]
```

---

# 4. Get User By ID

Returns a specific user using the user ID.

## Endpoint

```http
GET /api/users/{id}
```

## Example

```text
GET http://localhost:8080/api/users/1
```

## Authentication

```text
JWT Required
```

## Request Headers

```http
Authorization: Bearer <JWT_TOKEN>
```

## Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| `id` | Long | Yes | Unique user ID |

## Success Response

```json
{
  "id": 1,
  "name": "Gaurav",
  "email": "gaurav@example.com",
  "role": "USER"
}
```

---

# 5. Update User

Updates an existing user's information.

## Endpoint

```http
PUT /api/users/{id}
```

## Example

```text
PUT http://localhost:8080/api/users/1
```

## Authentication

```text
JWT Required
```

## Request Headers

```http
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>
```

## Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| `id` | Long | Yes | User ID |

## Request Body

```json
{
  "name": "Gaurav Updated",
  "email": "gaurav.updated@example.com",
  "password": "newpassword123"
}
```

## Success Response

```json
{
  "id": 1,
  "name": "Gaurav Updated",
  "email": "gaurav.updated@example.com",
  "role": "USER"
}
```

---

# 6. Delete User

Deletes an existing user.

## Endpoint

```http
DELETE /api/users/{id}
```

## Example

```text
DELETE http://localhost:8080/api/users/1
```

## Authentication

```text
JWT Required
```

## Request Headers

```http
Authorization: Bearer <JWT_TOKEN>
```

## Path Parameters

| Parameter | Type | Required | Description |
|---|---|---|---|
| `id` | Long | Yes | User ID |

## Success Response

```text
204 NO CONTENT
```

The successful response does not contain a response body.

---

# Error Handling

The application provides centralized exception handling through:

```text
GlobalExceptionHandler
```

The application handles the following errors:

| Status | Error | Description |
|---|---|---|
| `400` | Bad Request | Request validation failed |
| `401` | Unauthorized | Invalid credentials or authentication failure |
| `404` | User Not Found | Requested user does not exist |
| `409` | Email Already Exists | Email is already registered |

---

# 400 Bad Request

Returned when request validation fails.

## Example

```json
{
  "timestamp": "2026-08-12T12:00:00",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
    "email": "Invalid email format",
    "password": "Password must not be blank"
  }
}
```

---

# 401 Unauthorized

Returned when authentication fails or invalid credentials are provided.

## Example

```json
{
  "timestamp": "2026-08-12T12:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password"
}
```

---

# 404 Not Found

Returned when the requested user does not exist.

## Example

```json
{
  "timestamp": "2026-08-12T12:00:00",
  "status": 404,
  "error": "User Not Found",
  "message": "User not found with id: 10"
}
```

---

# 409 Conflict

Returned when an email address is already registered.

## Example

```json
{
  "timestamp": "2026-08-12T12:00:00",
  "status": 409,
  "error": "Email Already Exists",
  "message": "Email already registered"
}
```

---

# HTTP Status Summary

| Status | Meaning | Typical Scenario |
|---|---|---|
| `200 OK` | Request successful | Login, Get User, Update User |
| `201 CREATED` | Resource created | User registration |
| `204 NO CONTENT` | Request successful without response body | User deletion |
| `400 BAD REQUEST` | Invalid request | Validation failure |
| `401 UNAUTHORIZED` | Authentication failed | Invalid credentials or JWT |
| `404 NOT FOUND` | Resource not found | User does not exist |
| `409 CONFLICT` | Resource conflict | Duplicate email |

---

# User Response Model

The User API does not expose the user's password in API responses.

```json
{
  "id": 1,
  "name": "Gaurav",
  "email": "gaurav@example.com",
  "role": "USER"
}
```

## Fields

| Field | Type | Description |
|---|---|---|
| `id` | Long | Unique user identifier |
| `name` | String | User name |
| `email` | String | User email |
| `role` | String | User role |

---

# Login Response Model

```json
{
  "token": "<JWT_TOKEN>",
  "email": "gaurav@example.com"
}
```

| Field | Type | Description |
|---|---|---|
| `token` | String | JWT authentication token |
| `email` | String | Authenticated user's email |

---

# NATS Event Documentation

The Notification Microservice does not communicate with the User Microservice through REST APIs or WebSockets.

Communication between these services is performed asynchronously through NATS.

## NATS Subject

```text
user.created
```

## Event Name

```text
USER_CREATED
```

## Producer

```text
User Microservice
```

## Consumer

```text
Notification Microservice
```

## Event Payload

```json
{
  "userId": 6,
  "name": "Gaurav",
  "email": "gaurav@example.com"
}
```

## Event Fields

| Field | Type | Description |
|---|---|---|
| `userId` | Long | ID of newly created user |
| `name` | String | User name |
| `email` | String | User email |

## Event Flow

```text
User Registration
       |
       v
User Microservice
       |
       | Save User
       v
     MySQL
       |
       | Publish Event
       v
      NATS
  user.created
       |
       v
Notification Microservice
       |
       v
Notification Processing
```

---

# API Gateway Routing

The API Gateway runs on port `8080`.

The current User Service route is:

```text
/api/users/**
       |
       v
http://localhost:8081
```

Therefore:

```text
Client
  |
  | http://localhost:8080/api/users/...
  v
API Gateway :8080
  |
  | Forward Request
  v
User Microservice :8081
```

The Notification Microservice is not exposed through the API Gateway.

User-to-Notification communication is performed asynchronously through NATS.

---

# Postman Testing Flow

For a complete application test, use the following sequence.

## Step 1 — Register User

```http
POST http://localhost:8080/api/users/register
```

Example:

```json
{
  "name": "Gaurav",
  "email": "gaurav@example.com",
  "password": "password123"
}
```

---

## Step 2 — Verify NATS Event

After successful registration, the User Microservice publishes:

```text
user.created
```

The Notification Microservice should receive and process the event.

Example log:

```text
USER_CREATED event received
Notification: Welcome Gaurav!
```

---

## Step 3 — Login

```http
POST http://localhost:8080/api/users/login
```

Copy the JWT returned by the login response.

---

## Step 4 — Add JWT

For protected endpoints, add:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## Step 5 — Test Protected APIs

```http
GET    http://localhost:8080/api/users
```

```http
GET    http://localhost:8080/api/users/{id}
```

```http
PUT    http://localhost:8080/api/users/{id}
```

```http
DELETE http://localhost:8080/api/users/{id}
```

---

## Step 6 — Test Error Scenarios

Test the following cases:

- Invalid email
- Missing email
- Missing password
- Invalid password
- Duplicate email
- Invalid login credentials
- Non-existent user ID
- Missing JWT
- Invalid JWT

---

# Security

The application implements the following security practices:

- JWT-based stateless authentication
- BCrypt password hashing
- Passwords are not returned in API responses
- Protected endpoints require authentication
- Registration and login are publicly accessible
- Sensitive configuration should be provided through environment variables
- Database credentials should not be committed to GitHub
- JWT secrets should not be committed to GitHub

---

# Complete API Flow

```text
                         Client
                           |
                           | HTTP
                           v
                  +-------------------+
                  |   API Gateway     |
                  |      :8080        |
                  +---------+---------+
                            |
                            | /api/users/**
                            v
                  +-------------------+
                  | User Microservice |
                  |      :8081        |
                  +---------+---------+
                            |
              +-------------+-------------+
              |                           |
              |                           |
              v                           v
        +-----------+                  +------+
        |   MySQL   |                  | NATS |
        |   :3306   |                  | :4222|
        +-----------+                  +--+---+
                                          |
                                          | user.created
                                          v
                              +------------------------+
                              | Notification Service   |
                              |        :8082            |
                              +------------------------+
```

---

# API Endpoint Summary

| # | Method | Endpoint | Auth | Purpose |
|---|---|---|---|---|
| 1 | `POST` | `/api/users/register` | Public | Register user |
| 2 | `POST` | `/api/users/login` | Public | Login and generate JWT |
| 3 | `GET` | `/api/users` | JWT | Get all users |
| 4 | `GET` | `/api/users/{id}` | JWT | Get user by ID |
| 5 | `PUT` | `/api/users/{id}` | JWT | Update user |
| 6 | `DELETE` | `/api/users/{id}` | JWT | Delete user |

---

# Documentation Structure

Recommended documentation structure:

```text
User-Microservices/
│
├── README.md
│
├── Documents/
│   │
│   ├── Architecture/
│   │   ├── system-architecture.md
│   │   ├── system-architecture.png
│   │   ├── request-flow.md
│   │   ├── request-flow.png
│   │   ├── authentication-flow.md
│   │   ├── authentication-flow.png
│   │   ├── event-driven-architecture.md
│   │   └── event-driven-architecture.png
│   │
│   └── API/
│       └── api-documentation.md
│
├── User-Microservice/
│
├── Notification-Microservice/
│
├── API-Gateway/
│
└── .gitignore
```

---

## Documentation Purpose

This API documentation provides:

- REST API endpoints
- Request methods
- Request URLs
- Request headers
- Request bodies
- Response examples
- Authentication requirements
- Error responses
- HTTP status codes
- API Gateway routing
- NATS event documentation
- Postman testing flow
- Security information