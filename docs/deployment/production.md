# Production deployment notes

Required environment variables (examples):

- `DB_URL` — JDBC URL for production Postgres (for example `jdbc:postgresql://host:5432/dbname`)
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET` — must be a strong secret (minimum 32 bytes for HS256)
- `CORS_ALLOWED_ORIGINS` — production frontend origins
- `APP_VERSION` — recommended to be set at deploy time

The `application-prod.yml` reads these variables; production **must not** contain any secrets in the repo.

Build and run (example using Docker Compose):

```sh
# Build a production image and run
docker compose --profile prod up --build
```

Actuator exposure in production is intentionally limited to `health`, `info`, and `prometheus`. Do not expose sensitive actuator endpoints publicly.
