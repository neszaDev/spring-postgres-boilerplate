'
Developer Quick Start

Build & Local Validation
- Run unit tests locally: `mvn test`
- Full local build (skip integration tests): `mvn -DskipITs clean verify`
- If `./mvnw` fails locally, use system `mvn` (Maven 3.6+). We generated a Maven wrapper in the repo.

Integration tests & CI
- Testcontainers-based integration tests run in CI only (ubuntu runners). Do not run integration tests locally if your machine cannot run Docker.
- CI includes a diagnostics workflow that prints `docker version`, `docker info`, `ls -la /var/run/docker.sock`, and `df -h`.

Maven wrapper
- If `./mvnw` is missing, regenerate with: `mvn -N io.takari:maven:wrapper`

Formatting
- Spotless is used in CI: `./mvnw -B spotless:check` (or `spotless:apply` to auto-format locally).

Workflow debugging
- If a workflow aborts at 0s with a "workflow file issue" banner on GitHub Actions, copy the exact red-banner parse message and paste it into the issue/PR or here so CI syntax errors can be fixed.

Notes
- Do not change Windows security (e.g., Vanguard) or Docker configuration to enable local Docker.
- Integration tests must remain in the repo and run in CI.
'
