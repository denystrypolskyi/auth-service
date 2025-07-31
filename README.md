# Auth Service

This is the authentication and authorization microservice for the microservices-based project. It handles user registration, login, token generation, and user identity management.

## 🚀 Features

- User registration — allows creating new user accounts with username, email, and password.
- User login — authenticates users and issues access and refresh JWT tokens.
- Token refresh — accepts a refresh token and returns a new access/refresh token pair.
- User management — provides access to a list of all registered users.
- JWT-based authentication.

## 📌 Endpoints

| Method | Endpoint          | Description                                      | Authentication Required                          |
|--------|-------------------|------------------------------------------------|----------------------------------|
| POST   | `/auth/register`  | Register a new user account                      | No                           |
| POST   | `/auth/login`     | Authenticate user and obtain access & refresh tokens | No                           |
| POST   | `/auth/refresh`   | Refresh JWT tokens using a valid refresh token | No                           |
| GET    | `/auth/all`       | Get a list of all registered users              | Yes |

## 🛡 Security

- Passwords are securely hashed before storage.
- JWT-based authentication with access and refresh tokens.
- In production, role-based access control would be added to restrict sensitive operations.

## 📦 Tech Stack

- Java 24  
- Spring Boot  
- Spring Security (JWT)  
- PostgreSQL  
- Lombok  
- Jakarta Validation  
