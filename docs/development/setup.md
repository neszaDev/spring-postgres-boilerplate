# Local development setup

1. Copy `.env.example` to `.env` and set a strong `JWT_SECRET` (at least 32 bytes).

2. Start local Postgres + app (three common modes):

- Local hot reload (recommended for development):

  ```sh
  docker compose --profile local build app-local
  docker compose --profile local watch
  ```

  This runs Maven inside the container with Spring DevTools; source changes under `src/` are synced and Maven restarts automatically.

- Development image (packaged app):

  ```sh
  docker compose --profile dev up --build
  ```

- Production-style image (use only for smoke testing):

  ```sh
  docker compose --profile prod up --build
  ```

3. Run tests locally (may require Docker for integration tests):

  ```sh
  ./scripts/test.sh
  ```

4. Build a packaged JAR locally:

  ```sh
  ./scripts/build.sh
  # or: mvn -B -DskipTests package
  ```

Notes:

- Production requires `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and `CORS_ALLOWED_ORIGINS` to be provided by the deployment environment.
- Do not commit secrets. `.env.example` documents local-only defaults.
