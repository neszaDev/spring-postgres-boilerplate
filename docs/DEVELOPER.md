# Developer Guide

This document describes how to build, test and iterate on the project locally and what the CI does.

## Local prerequisites

- Java 21 (Temurin / Adoptium / Microsoft builds recommended)
- Maven 3.9+
- (Optional) Docker when running integration tests locally — this repo's integration tests use Testcontainers and require Docker; since some Windows environments (e.g. Vanguard) cannot run Docker, CI runs integration tests for you.

If you cannot run Docker locally use `mvn -DskipITs=true clean verify` to run the full build without integration tests.

> Windows/OneDrive note: the Maven wrapper (`./mvnw`) may fail when the repository path contains non-ASCII characters or OneDrive-managed paths. Workarounds:
> - Use system `mvn` instead (recommended for Windows with OneDrive).
> - Move the repository to an ASCII-only path (e.g., `C:\dev\spring-postgres-boilerplate`).
> - Run under WSL/Ubuntu.

## Common commands

- Run unit tests only:

```sh
mvn test
```

- Full local build (skip Testcontainers integration tests):

```sh
mvn -DskipITs=true clean verify
```

- Apply Spotless formatting locally (uses configured formatter):

```sh
./mvnw -B spotless:apply   # or `mvn -B spotless:apply` if you use system mvn
```

- Strip UTF-8 BOM from files (safe to run):

```sh
python scripts/fix_bom_and_stray_lines.py   # or follow the repository-provided script
```

- Regenerate Maven wrapper (if you prefer a fresh wrapper):

```sh
mvn -N io.takari:maven:wrapper
```

## Running integration tests locally

Integration tests (Testcontainers) require Docker. If your environment cannot run Docker (for example, Riot Vanguard / Valorant anti-cheat on Windows), do not attempt to enable Docker — instead rely on CI to run these tests.

To run integration tests locally (ONLY if Docker is available):

```sh
mvn -Pit,test -DskipTests=false verify
```

## CI / GitHub Actions

- CI performs formatting (Spotless), unit tests and a verify build that skips integration tests.
- Integration tests using Testcontainers run in a separate CI workflow on ubuntu runners where Docker is available.
- CodeQL analysis runs after a successful Maven package step so CodeQL sees compiled sources.

## JWT secret validation

The application expects a `JWT_SECRET` environment variable (or equivalent Spring property) when running in non-test profiles. The secret should be a sufficiently strong key (recommendation: at least 32 random bytes encoded in base64 or a long passphrase). At startup the application validates the secret and will fail fast if it is missing or too weak.

## Help & debugging

If CI reports a test failure you cannot reproduce locally, attach the CI job logs and I will investigate. When reporting local failures include:

- `mvn -DskipITs=true clean verify` output
- `target/surefire-reports/*` contents for failing tests

