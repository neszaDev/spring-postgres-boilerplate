# Environment Variables

This project reads configuration from environment variables. The most commonly required variables are:

- `DB_URL` - JDBC connection string for the database. Default: `jdbc:postgresql://localhost:5432/boilerplate`
- `DB_USERNAME` - Database user name. Default: `boilerplate`
- `DB_PASSWORD` - Database password. Default: `boilerplate`

Security / JWT
- `JWT_SECRET` - Primary HMAC secret for signing JWTs (required for non-test profiles). Provide a secure 32+ character secret. Example: on Linux/macOS `export JWT_SECRET=$(openssl rand -hex 32)`.

Spring profiles
- `SPRING_PROFILES_ACTIVE` - Activate a Spring profile (`local`, `dev`, `test`, `prod`, ...).

CI / Testcontainers
- CI sets up a Postgres instance for integration tests; local developers who cannot run Docker should use the `-DskipITs=true` flag when running `mvn verify`.

Other settings
- `PORT` - Server port (default 8080)
- `CORS_ALLOWED_ORIGINS` - Comma-separated list of allowed CORS origins.

Notes
- Do not store production secrets in plaintext in the repository. Use CI secrets for GitHub Actions and environment/secret managers for deployments.
