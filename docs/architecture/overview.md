# Architecture Overview

See `ARCHITECTURE.md` for the full project architecture. This document highlights key components:

- Spring Boot application (REST API)
- PostgreSQL database with Flyway migrations under `src/main/resources/db/migration`
- Authentication: JWT and Spring Security
- Observability: Actuator, Micrometer/Prometheus, structured logs
- CI: Maven-based build and test

Keep this focused on this repository's actual design.
