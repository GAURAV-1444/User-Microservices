# User Microservices

A backend microservices application built with **Java 17, Spring Boot, Spring Cloud Gateway, MySQL, JWT, and NATS**.

This project demonstrates a practical microservices architecture where user management, authentication, API routing, and event-driven notifications are separated into independent services.

---

## 🏗️ Architecture

```text
                                ┌──────────────────────┐
                                │        Client        │
                                │   Postman / Frontend │
                                └───────────┬──────────┘
                                            │
                                            │ HTTP
                                            ▼
                                ┌──────────────────────┐
                                │     API Gateway      │
                                │        :8080         │
                                └───────────┬──────────┘
                                            │
                                            │ HTTP
                                            ▼
                         ┌──────────────────────────────────┐
                         │       User Microservice          │
                         │              :8081               │
                         │                                  │
                         │  • User Management               │
                         │  • REST APIs                     │
                         │  • JWT Authentication            │
                         │  • Password Encryption           │
                         │  • Validation                    │
                         │  • Exception Handling            │
                         └───────────────┬──────────────────┘
                                         │
                                         │ USER_CREATED
                                         ▼
                                ┌──────────────────────┐
                                │         NATS         │
                                │        :4222         │
                                │                      │
                                │   Subject:           │
                                │   user.created       │
                                └───────────┬──────────┘
                                            │
                                            │ Subscribe
                                            ▼
                         ┌──────────────────────────────────┐
                         │   Notification Microservice      │
                         │              :8082               │
                         │                                  │
                         │  • NATS Event Subscriber         │
                         │  • Event Processing              │
                         │  • Notification Handling         │
                         └──────────────────────────────────┘


                         ┌──────────────────────────────────┐
                         │             MySQL                │
                         │                                  │
                         │       user_microservice_db       │
                         └──────────────────────────────────┘

```
---

### Request Flow

```text

Client
  │
  ▼
API Gateway :8080
  │
  ▼
User Microservice :8081
  │
  ├──────────────► MySQL
  │
  └── USER_CREATED ──► NATS :4222
                           │
                           ▼
                  Notification Service :8082

```
---


## 🚀 Services

### 1. User Microservice

#### Port: 8081

The User Microservice is responsible for managing users and authentication.

#### Responsibilities
User registration
User login
JWT generation
User retrieval
User update
User deletion
Password encryption using BCrypt
Input validation
Duplicate email detection
MySQL persistence
Publishing user events

### 2. API Gateway

#### Port: 8080

The API Gateway acts as the single entry point for client requests.

Current Route

```text
/api/users/**
       │
       ▼
User Microservice
http://localhost:8081

```

Clients can therefore use:

```text
http://localhost:8080/api/users

```
instead of directly accessing:

```text
http://localhost:8081/api/users

```

This provides a centralized entry point and makes it easier to add additional microservices in the future.

### 3. Notification Microservice

#### Port: 8082

The Notification Microservice consumes user-related events through NATS.

When a new user registers:

```text
User Microservice
       │
       │ USER_CREATED
       ▼
     NATS
       │
       ▼
Notification Microservice

```

The notification service subscribes to:

```text
user.created

```
and processes the received event.

---

## 🛠️ Technology Stack
```text
Technology	                                   Purpose
Java 17                                        Programming Language
Spring Boot 3.3.2	                             Backend Framework
Spring Web	                                   REST APIs
Spring Data JPA	                               Database Access
Hibernate	                                     ORM
Spring Security	                               Authentication
JWT	                                           Stateless Authentication
BCrypt	                                       Password Hashing
Spring Cloud Gateway	                         API Gateway
MySQL	                                         Relational Database
NATS	                                         Event-Driven Messaging
Maven	                                         Build & Dependency Management
Lombok	                                       Boilerplate Reduction
Docker	                                       NATS Containerization
Postman	                                       API Testing
Git / GitHub	                                 Version Control
```

---


## ✨ Features
### 👤 User Management

The User Microservice provides complete CRUD functionality.

### Register User

Endpoint

```text
POST /api/users/register
```

Request

```text
{
    "name": "Gaurav",
    "email": "gaurav@example.com",
    "password": "password123"
}
```

Response

```text
{
    "id": 1,
    "name": "Gaurav",
    "email": "gaurav@example.com",
    "role": "USER"
}
```


### 🔐 Login

Endpoint

```text
POST /api/users/login
```

Request

```text
{
    "email": "gaurav@example.com",
    "password": "password123"
}
```

Response

```text
{
    "token": "JWT_TOKEN",
    "email": "gaurav@example.com"
}
```

The generated JWT can be used for authenticated requests.


### 📋 Get All Users

Endpoint

```text
GET /api/users
```

Example

```text
http://localhost:8080/api/users
```

### 🔎 Get User By ID

Endpoint

```text
GET /api/users/{id}
```

Example

```text
GET /api/users/1
```

### ✏️ Update User

Endpoint

```text
PUT /api/users/{id}
```

Request

```text
{
    "name": "Updated Name",
    "email": "updated@example.com",
    "password": "newpassword123"
}
```

### 🗑️ Delete User

Endpoint

```text
DELETE /api/users/{id}
```

Example

```text
DELETE /api/users/1
```

---

## 🔐 Authentication

The User Microservice uses JWT for generating authentication tokens during login.

Currently, the login endpoint:

1. Validates the user's email.
2. Validates the password using BCrypt.
3. Generates a JWT token.
4. Returns the token to the client.

Authorization and role-based access control are not currently enforced on the API endpoints.

#### Login Flow

```text

                    Login Request
                         │
                         ▼
                ┌──────────────────┐
                │ User Microservice│
                └────────┬─────────┘
                         │
                         │ Validate Credentials
                         ▼
                ┌──────────────────┐
                │ Generate JWT     │
                └────────┬─────────┘
                         │
                         ▼
                       Client
                         │
                         ▼
                    JWT Token
```

---


## 📡 Event-Driven Communication

The project uses NATS for asynchronous communication between services.

When a user successfully registers, the User Microservice creates a:

```text
UserCreatedEvent
```

#### Event Structure

```text
{
    "userId": 6,
    "name": "Gaurav",
    "email": "gaurav@example.com"
}
```

The event is published to the NATS subject:

```text
user.created
```

The Notification Microservice subscribes to the same subject.

#### Event Flow

```text
POST /api/users/register
            │
            ▼
   User Microservice
            │
            │ Save User
            ▼
          MySQL
            │
            │ Publish Event
            ▼
          NATS
      user.created
            │
            ▼
 Notification Service
            │
            ▼
 "Notification: Welcome Gaurav!"
 ```

This keeps the notification process decoupled from the User Microservice.

---


## 🗄️ Database

The project uses MySQL.

#### Database

```text
user_microservice_db
```

#### Main Table
```text
users
```

#### User Entity

```text

Field	                                   Description
id	                                     Unique user identifier
name	                                   User name
email	                                   Unique email address
password	                               BCrypt encoded password
role	                                   User role
```

The ` email ` field is configured as unique.

---

## 📁 Project Structure
```text
User-Microservices-Task/
│
├── README.md
│
├── Documents/
│   │
│   ├── Architecture/
│   │   ├── System Architecture.png
│   │   ├── system-architecture.md
│   │   │
│   │   ├── Request Flow.png
│   │   ├── request-flow.md
│   │   │
│   │   ├── Authentication Flow.png
│   │   ├── authentication-flow.md
│   │   │
│   │   ├── Event Driven Architecture.png
│   │   └── event-driven-architecture.md
│   │
│   └── API/
│       └── api-documentation.md
│
├── User-Microservices/
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── Notification-Microservice/
│   ├── src/
│   ├── pom.xml
│   └── ...
│
├── API-Gateway/
│   ├── src/
│   ├── pom.xml
│   └── ...
│
└── .gitignore
```

---


## 📋 Prerequisites

##### Make sure the following are installed:

Java 17

Maven

MySQL

Docker

Git

Postman


---


## 🐳 Running NATS

NATS is used as the messaging system between microservices.

### Start NATS

```text
docker run -d --name nats-server -p 4222:4222 nats:latest
```

### Check the Container

```text
docker ps
```

NATS should be available on:

```text
localhost:4222
```

If the Container Already Exists

```text
docker start nats-server
```

### Stop NATS

```text
docker stop nats-server
```

### Remove NATS

```text
docker rm -f nats-server
```

---


## 🗄️ MySQL Configuration

Create the database:

```text
CREATE DATABASE user_microservice_db;
```

Configure your local credentials in:

```text
User-Microservice/src/main/resources/application.properties
```

Example:

```text
spring.datasource.url=jdbc:mysql://localhost:3306/user_microservice_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

Important: Do not commit your real database password to GitHub.

---


## 🔑 JWT Configuration

Configure your local JWT settings in:

```text
application.properties
```

Example:

```text
jwt.secret=YOUR_SECRET_KEY
jwt.expiration=86400000
```

For GitHub, use:

```text
application-example.properties
```
instead of exposing real secrets.

---


## ⚙️ Environment Variables

Sensitive configuration can be supplied through environment variables.

Supported variables include:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
NATS_URL
JWT_SECRET
JWT_EXPIRATION
USER_SERVICE_URL
```

Example:

```text
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/user_microservice_db}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}

nats.url=${NATS_URL:nats://localhost:4222}

jwt.secret=${JWT_SECRET:YOUR_SECRET_KEY}
jwt.expiration=${JWT_EXPIRATION:86400000}
```
---


## ▶️ Running the Application

Start the services in the following order.

### 1. Start NATS

```text
docker start nats-server
```

Or create a new container:

```text
docker run -d --name nats-server -p 4222:4222 nats:latest
```

### 2. Start User Microservice

Navigate to:

```text
User-Microservice/
```

Run:

```text
mvn spring-boot:run
```

The service runs on:
```text
http://localhost:8081
```

### 3. Start Notification Microservice

Navigate to:

```text
Notification-Microservice/
```

Run:

```text
mvn spring-boot:run
```

The service runs on:

```text
http://localhost:8082
```

You should see:

```text
Subscribed to NATS subject: user.created
```

### 4. Start API Gateway

Navigate to:

```text
API-Gateway/
```

Run:

```text
mvn spring-boot:run
```

The gateway runs on:

```text
http://localhost:8080
```

---


## 🧪 API Testing

The recommended way to test the application is through Postman.

Use the API Gateway as the main entry point:

```text
http://localhost:8080
```

### Register

```text
POST http://localhost:8080/api/users/register
```

### Login

```text
POST http://localhost:8080/api/users/login
```

### Get All Users

```text
GET http://localhost:8080/api/users
```

### Get User

```text
GET http://localhost:8080/api/users/{id}
```

### Update User

```text
PUT http://localhost:8080/api/users/{id}
```

### Delete User

```text
DELETE http://localhost:8080/api/users/{id}
```

---


## ❌ Error Handling

The application provides centralized exception handling.

```text

Error                                                 	HTTP Status
User Not Found	                                        404 NOT_FOUND
Duplicate Email	                                        409 CONFLICT
Validation Failure	                                    400 BAD_REQUEST
Invalid Credentials	                                    401 UNAUTHORIZED
```

#### Example

```text
{
    "timestamp": "2026-08-12T12:00:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid email or password"
}
```

---


## 🌐 API Gateway Routing

The current gateway route is:

```text
/api/users/**
       │
       ▼
User Microservice
http://localhost:8081
```

The gateway provides a centralized entry point for the application and allows additional services to be added without exposing every microservice directly to clients.


---


## 🧩 Design Principles

### This project demonstrates:

Microservice separation
RESTful API design
JWT authentication
Stateless authentication
Password hashing
Centralized exception handling
DTO-based API communication
Input validation
Event-driven architecture
Asynchronous messaging
API Gateway pattern
Database persistence
Environment-based configuration

---


## 🔮 Future Improvements

### Potential improvements include:

Service discovery with Eureka

Centralized configuration with Spring Cloud Config

Dockerizing all services

Docker Compose

Refresh tokens

Role-based authorization

Distributed tracing

Centralized logging

Resilience4j circuit breakers

Redis caching

CI/CD pipeline

Production deployment

Spring Boot Actuator

Prometheus monitoring

Grafana dashboards


---


## 📚 Documentation

- [API Documentation](Documents/API/api-documentation.md)
- [System Architecture](Documents/Architecture/system-architecture.md)
- [Request Flow](Documents/Architecture/request-flow.md)
- [Authentication Flow](Documents/Architecture/authentication-flow.md)
- [Event-Driven Architecture](Documents/Architecture/event-driven-architecture.md)


---


### 👨‍💻 Author

Gaurav Kshirsagar

Computer Science & Engineering

---


## 📄 License

This project is intended for learning, portfolio, and demonstration purposes.