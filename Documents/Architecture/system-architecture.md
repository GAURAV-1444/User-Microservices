# System Architecture

## Overview

The application follows a microservices architecture consisting of three independent services:

1. User Microservice
2. Notification Microservice
3. API Gateway

The services communicate using HTTP through the API Gateway for client requests, while the User Microservice communicates with the Notification Microservice asynchronously using NATS.


## Components

### Client

The client can be Postman or a frontend application.

### API Gateway

- Port: 8080
- Technology: Spring Cloud Gateway
- Acts as the single entry point for client requests.
- Routes `/api/users/**` to the User Microservice.

### User Microservice

- Port: 8081
- Handles user registration, authentication and CRUD operations.
- Uses MySQL for persistence.
- Generates and validates JWT tokens.
- Publishes `USER_CREATED` events to NATS.

### Notification Microservice

- Port: 8082
- Does not expose REST APIs for communication with the User Microservice.
- Subscribes to the `user.created` NATS subject.
- Processes user-created events.

### MySQL

Stores user information in the `user_microservice_db` database.

### NATS

Provides asynchronous communication between the User and Notification Microservices.

## Communication

Client → API Gateway → User Microservice

User Microservice → NATS → Notification Microservice