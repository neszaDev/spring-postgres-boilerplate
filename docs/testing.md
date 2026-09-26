# Testing

Unit tests and integration tests are run with Maven. Integration tests use Testcontainers and require Docker when run locally or in CI runners that provide Docker.

## Unit tests (fast)

Run unit tests only (no Docker required):

```sh
mvn test
# or with Maven wrapper
./mvnw test
```

## Full build locally (skip integration tests)

To perform a full build locally but avoid running Testcontainers-based integration tests (useful on machines without Docker):

```sh
mvn clean verify -DskipITs
# or with Maven wrapper
./mvnw clean verify -DskipITs
```

## Integration tests (Testcontainers)

Integration tests use Testcontainers and require Docker. These tests are executed in CI via the `Integration Tests` workflow (see `.github/workflows/integration.yml`). To run integration tests locally:

1. Start Docker on your machine.
2. Run the full Maven verify: `./mvnw -B -DskipITs=false verify`.

If Docker is not available, avoid running the integration tests locally and rely on the CI workflow to validate integration behavior.
