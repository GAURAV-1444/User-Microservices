# Authentication Flow

## Overview

The User Microservice implements stateless authentication using Spring Security and JWT.


## Login

1. Client sends email and password.
2. User Microservice retrieves the user from MySQL.
3. BCrypt verifies the supplied password.
4. If credentials are valid, a JWT is generated.
5. JWT is returned to the client.

## Authenticated Requests

The client sends:

Authorization: Bearer <JWT>

The JwtAuthenticationFilter:

1. Extracts the Authorization header.
2. Extracts the JWT.
3. Validates the token signature and expiration.
4. Extracts the user email.
5. Loads the user.
6. Creates an authenticated SecurityContext.
7. Allows the request to continue.

## Security

- Passwords are hashed using BCrypt.
- Sessions are stateless.
- JWT is used for authentication.
- Protected endpoints require a valid Bearer token.