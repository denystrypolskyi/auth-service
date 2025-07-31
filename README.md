# Auth Service

This is the authentication and authorization microservice for the microservices-based project. It handles user registration, login, token generation, and user identity management.

## 🚀 Features

- **User registration**  
  `POST /auth/register` — Create a new user account with username, email, and password.

- **User login**  
  `POST /auth/login` — Authenticates user and returns access & refresh tokens.

- **Token refresh**  
  `POST /auth/refresh` — Accepts a refresh token and returns a new token pair.

- **Get all users**  
  `GET /auth/all` — Returns a list of all registered users.  
  🔒 Protected with custom `@Authenticated` annotation.  
  ⚠️ Currently, no role-based access is enforced — any authenticated user can access this endpoint.

## 🛡 Security

- Passwords are securely hashed before storage.
- JWT-based authentication with access and refresh tokens.
- Custom `@Authenticated` annotation is used to protect sensitive endpoints.
- In a production setup, role-based access control should be added to restrict admin-only endpoints like `/auth/all`.

## 📦 Tech Stack

- Java 24
- Spring Boot
- Spring Security (JWT)
- PostgreSQL
- Lombok
- Jakarta Validation

