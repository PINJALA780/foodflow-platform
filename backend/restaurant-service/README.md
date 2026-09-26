# FoodFlow User Service (`user-service`)

A production-style **Spring Boot 3.x** microservice for user authentication, authorization, and profile management within the FoodFlow platform.

---

## 🛠️ Technology Stack & Requirements

- **Java Version**: 17
- **Build Tool**: Apache Maven 3.x
- **Framework**: Spring Boot 3.2.5
  - Spring Web
  - Spring Security (JWT-based authentication)
  - Spring Data JPA
  - Bean Validation (`hibernate-validator`)
  - Spring Boot Actuator
- **Database**: PostgreSQL (`postgresql` driver)
- **Utilities**:
  - Lombok
  - MapStruct (DTO <-> Entity Mapping)
  - JJWT (`io.jsonwebtoken:jjwt-api:0.12.5`)
- **Testing**:
  - JUnit 5
  - Mockito
  - Spring Boot Test
  - Spring Security Test

---

## 📁 Directory & Package Structure

```
user-service/
├── pom.xml
├── .gitignore
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/foodflow/userservice/
    │   │       ├── UserServiceApplication.java
    │   │       ├── config/
    │   │       │   ├── SecurityConfig.java
    │   │       │   └── CorsConfig.java
    │   │       ├── controller/
    │   │       │   ├── AuthController.java
    │   │       │   └── UserController.java
    │   │       ├── dto/
    │   │       │   ├── AuthResponse.java
    │   │       │   ├── LoginRequest.java
    │   │       │   ├── RefreshTokenRequest.java
    │   │       │   ├── RegisterRequest.java
    │   │       │   ├── UpdateUserRequest.java
    │   │       │   └── UserResponse.java
    │   │       ├── entity/
    │   │       │   ├── User.java
    │   │       │   ├── RefreshToken.java
    │   │       │   ├── Role.java
    │   │       │   └── UserStatus.java
    │   │       ├── exception/
    │   │       │   ├── ErrorResponse.java
    │   │       │   ├── GlobalExceptionHandler.java
    │   │       │   ├── InvalidTokenException.java
    │   │       │   ├── UserAlreadyExistsException.java
    │   │       │   └── UserNotFoundException.java
    │   │       ├── mapper/
    │   │       │   └── UserMapper.java
    │   │       ├── repository/
    │   │       │   ├── UserRepository.java
    │   │       │   └── RefreshTokenRepository.java
    │   │       ├── security/
    │   │       │   ├── JwtAuthenticationEntryPoint.java
    │   │       │   ├── JwtAuthenticationFilter.java
    │   │       │   └── JwtTokenProvider.java
    │   │       └── service/
    │   │           ├── AuthService.java
    │   │           ├── UserService.java
    │   │           └── impl/
    │   │               ├── AuthServiceImpl.java
    │   │               ├── UserDetailsServiceImpl.java
    │   │               └── UserServiceImpl.java
    │   └── resources/
    │       ├── application.yml
    │       └── application-dev.yml
    └── test/
        └── java/
            └── com/foodflow/userservice/
                ├── UserServiceApplicationTests.java
                └── service/
                    └── UserServiceTest.java
```

---

## ⚙️ Configuration & Environment Profiles

### Profiles
- **`dev`** (*default*): Auto-creates schema updates (`ddl-auto: update`), enables SQL debugging logs.
- **`prod`**: Strictly validates database schema (`ddl-auto: validate`), hides raw SQL.

### Environment Variables

| Property | Environment Variable | Default Value (Dev) | Description |
|---|---|---|---|
| Server Port | `SERVER_PORT` | `8081` | Microservice HTTP port |
| Database Host | `DB_HOST` | `localhost` | PostgreSQL host |
| Database Port | `DB_PORT` | `5432` | PostgreSQL port |
| Database Name | `DB_NAME` | `foodflow_users` | PostgreSQL DB name |
| Database User | `DB_USERNAME` | `postgres` | DB username |
| Database Password | `DB_PASSWORD` | `postgres` | DB password |
| JWT Secret Key | `JWT_SECRET` | *(Default 256-bit dev key)* | Signing key for JWT tokens |
| JWT Expiration | `JWT_EXPIRATION_MS` | `3600000` (1h) | Access token validity (ms) |
| Refresh Expiration | `JWT_REFRESH_EXPIRATION_MS` | `86400000` (24h) | Refresh token validity (ms) |

---

## 🚀 Building & Testing

### Build Project
```bash
mvn clean compile
```

### Run Unit & Integration Tests
```bash
mvn clean test
```

### Package Application JAR
```bash
mvn clean package -DskipTests
```

### Run Service Locally
```bash
mvn spring-boot:run
```
*(Runs on http://localhost:8081)*

---

## 🔒 Endpoints Overview

- **Auth Endpoints** (`/api/v1/auth`):
  - `POST /api/v1/auth/register` — Register new user
  - `POST /api/v1/auth/login` — Authenticate user and receive JWT tokens
  - `POST /api/v1/auth/refresh` — Refresh access token
  - `POST /api/v1/auth/logout` — Revoke refresh token

- **User Profile Endpoints** (`/api/v1/users`):
  - `GET /api/v1/users/me` — Retrieve current authenticated user details
  - `GET /api/v1/users/{id}` — Get user details by ID (Admin or Self)
  - `PATCH /api/v1/users/{id}` — Update user details (Admin or Self)
  - `DELETE /api/v1/users/{id}` — Delete user profile (Admin)

- **Actuator Health & Info**:
  - `GET /actuator/health`
  - `GET /actuator/info`
