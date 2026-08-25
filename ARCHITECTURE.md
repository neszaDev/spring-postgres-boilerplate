# Architecture

The project is organized by feature. The initial `auth` and `user` features own their HTTP/API, application service, DTOs, and persistence types; shared cross-cutting code lives under `common` and `security`.

Requests flow from controller to service to repository. Controllers only deserialize/validate and return API contracts. Services own transactions and business rules. JPA entities are not returned from controllers.

`AuditableEntity` supplies database-assigned IDs, UTC audit timestamps, and optimistic-locking versions. Flyway is the sole schema authority: JPA validates the schema and never alters it.

JWT access tokens use HS256 and are intentionally short-lived. Use a secret manager to provide `JWT_SECRET`; replace the implementation with an OIDC resource server when centralized identity/SSO is needed. Every request gets an `X-Request-Id`, also placed in logging MDC.

Operational endpoints are supplied by Actuator. Health/info are public for orchestrators; production exposure excludes the general metrics endpoint, retaining Prometheus scraping at `/actuator/prometheus`.
