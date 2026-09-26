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
## CI notes (cleanup)

- Pinned org.springdoc:springdoc-openapi-starter-webmvc-ui to 2.8.13 to ensure compatibility with Spring Boot 3.5.5.
- Local validation commands:
  - Unit tests: mvn test
  - Full local build (skip integration tests): mvn -DskipITs clean verify
  - Integration tests (require Docker): run in CI; use gh run to inspect runs.
- Common CI debug commands:
  - gh run list --repo neszaDev/spring-postgres-boilerplate --limit 10
  - gh run view <run-id> --repo neszaDev/spring-postgres-boilerplate --log
  - mvn -DskipTests dependency:tree -Dincludes=org.springframework.boot -DoutputFile=target/deps.txt
- Maven wrapper: if ./mvnw fails, use system mvn as fallback.

## Verified CI run
- Integration Tests (main): https://github.com/neszaDev/spring-postgres-boilerplate/actions/runs/36118739938

