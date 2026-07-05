# Auth Service

Authentication service for the e-commerce backend. It handles user registration, login, refresh tokens, and JWT generation used by the other services.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Bean Validation
- JUnit, Mockito, MockMvc

## API

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| `POST` | `/auth/register` | No | Register a new user |
| `POST` | `/auth/login` | No | Authenticate and return access/refresh tokens |
| `POST` | `/auth/refresh` | No | Issue a new access/refresh token pair |
| `GET` | `/auth/all` | Yes | List registered users |

### Register Request

```json
{
  "username": "demo",
  "password": "password",
  "email": "demo@example.com"
}
```

### Login Request

```json
{
  "username": "demo",
  "password": "password"
}
```

### Token Response

```json
{
  "accessToken": "...",
  "refreshToken": "..."
}
```

## Notes

- Passwords are stored as BCrypt hashes.
- Access and refresh tokens are signed with `JWT_SECRET`.

## Run Tests

```powershell
.\mvnw.cmd test
```
