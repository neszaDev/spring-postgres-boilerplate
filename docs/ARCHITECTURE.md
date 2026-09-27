# Architecture Overview

This repository implements a Spring Boot backend foundation with a layered architecture:

- Web (controllers): `com.example.boilerplate.web` — REST endpoints for authentication and API access.
- Service: `com.example.boilerplate.service` — business logic (AuthService, RefreshToken cleanup).
- Auth: `com.example.boilerplate.auth` — JWT handling and validation.
- Persistence: `com.example.boilerplate.model` and `com.example.boilerplate.repository` — JPA entities and Spring Data repositories.
- Migrations: `src/main/resources/db/migration` — Flyway SQL migration scripts (V1..V4).
- Tests: unit tests, WebMvc tests, DataJpaTests (H2), and Testcontainers-based integration tests (run in CI).

JWT flow:
- On successful login the service issues an access token (JWT) and a refresh token (stored hashed in DB).
- Access tokens are signed with HMAC using `app.security.jwt.secret` and validated by `JwtService`.
- Refresh tokens are opaque random values: their SHA-256 hash is stored in `refresh_tokens` and reused for validation rotation.

Observability:
- Actuator endpoints are enabled (`/actuator/health`, `/actuator/info`, `/actuator/metrics`, `/actuator/prometheus`).

Note: Integration tests use Testcontainers to start PostgreSQL and run Flyway programmatically in CI.
