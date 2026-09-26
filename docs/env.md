# Environment variables

This project is environment-driven. The following environment variables are referenced in the configuration and must be set in production.

- DB_URL — JDBC URL for Postgres (e.g. `jdbc:postgresql://localhost:5432/boilerplate`)
- DB_USERNAME — database username (e.g. `boilerplate`)
- DB_PASSWORD — database password
- JWT_SECRET — HMAC secret for signing JWTs (must be at least 32 bytes)
- CORS_ALLOWED_ORIGINS — allowed CORS origins (comma-separated or `*` for permissive)
- PORT — server port (default 8080)
- JWT_ACCESS_TOKEN_TTL — ISO-8601 duration for access tokens (default `PT15M`)
- REFRESH_TOKEN_TTL — ISO-8601 duration for refresh tokens (default `P30D`)

Examples:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/boilerplate"
export DB_USERNAME=boilerplate
export DB_PASSWORD=boilerplate
export JWT_SECRET="really-strong-secret-at-least-32-bytes-long"
```

Do not commit real secrets into the repository.
