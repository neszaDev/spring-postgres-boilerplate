## Summary

- Add JWT authentication foundation (JWT validation, refresh tokens) and scheduled refresh-token cleanup.
- Add debug CI workflows and .gitattributes to fix cross-platform BOM/EOL issues and improve CI diagnostics.

## What Changed

- Application
  - Implemented `JwtService` with startup secret validation and parsing; fail-fast on missing/invalid secret (skipped in `test` profile).
  - Added `User` and `RefreshToken` entities, repositories, `AuthService`, and `AuthController` (register/login/refresh/logout).
  - Added global exception handler returning structured `ApiError` and mapping `IllegalArgumentException` to HTTP 400.

- Build & Development
  - Added `.gitattributes` to normalize EOL and avoid BOM issues.
  - Added scripts to run Maven from `.maven-dist`, fallback to `./mvnw` or system `mvn`.
  - Added developer docs (`DEVELOPER.md`, `docs/env.md`) and troubleshooting notes for Maven wrapper on Windows.

- Testing
  - Added unit tests for JwtService and AuthService, WebMvc tests for AuthController (positive & negative), DataJpaTest (H2) for UserRepository, and Testcontainers integration tests (DatabaseIntegrationIT, AuthIntegrationIT) that run in CI.

- CI/CD
  - Added `verify-debug` workflow (manual + push) that uploads `mvn-verify-debug.log` and `flyway-deps.txt` to help diagnose CI issues.
  - Added CodeQL workflow that builds (`mvn -DskipTests package`) before analysis.

- Documentation / Developer Experience
  - Updated README with quickstart and DEVELOPER.md with build/test instructions, env vars, and Maven wrapper fallback scripts.

## Testing

- Tests actually run locally:
  - Unit tests: `mvn test` (JwtService, AuthService, etc.)
  - WebMvc tests: run with `mvn test` locally
  - DataJpa H2 tests: `mvn test`

- Tests that require Docker/CI:
  - Testcontainers integration tests: `AuthIntegrationIT` and `DatabaseIntegrationIT` run on GitHub Actions (ubuntu runners). Local Docker is NOT required or expected on machines with Vanguard/Vanguard.

- Tests that could not be run here:
  - Full CI Verify + integration pipeline — currently blocked by a Maven ModelParseException; please see the debug artifact `verify-debug-logs` uploaded by the `verify-debug` workflow for the full stack trace.

## Notes

- JWT secret validation is enforced at startup; when running tests the `test` profile skips strict validation and uses a fallback test key.
- The Maven wrapper may fail on Windows if the project path contains non-ASCII characters (e.g., OneDrive). Use `scripts/run-maven-local.*` or install system `mvn`.
- Flyway migrations are in `src/main/resources/db/migration`. Integration tests run Flyway programmatically against Testcontainers Postgres.

## Breaking Changes

- None

## Review Focus

- Verify JWT secret validation and exception messages are appropriate and non-sensitive.
- Check Flyway migration scripts against JPA entities (users, refresh_tokens) for schema correctness.
- Confirm CI debug workflow captures `mvn-verify-debug.log` and `flyway-deps.txt` for diagnosing ModelParseException and Flyway classpath issues.
