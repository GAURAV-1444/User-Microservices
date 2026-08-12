# Request Flow

## Overview

Client requests are sent through the API Gateway before reaching the User Microservice.


## Request Processing

1. Client sends an HTTP request.
2. API Gateway receives the request on port 8080.
3. Gateway routes `/api/users/**` to port 8081.
4. UserController receives the request.
5. Request validation is performed using Jakarta Bean Validation.
6. UserService performs business logic.
7. UserRepository communicates with MySQL.
8. The response is returned to the client.

## Registration Flow

For registration:

Client
→ API Gateway
→ User Controller
→ User Service
→ BCrypt password hashing
→ Repository
→ MySQL
→ UserCreatedEvent
→ NATS
→ Notification Service