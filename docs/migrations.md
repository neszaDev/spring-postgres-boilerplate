# Database Migrations (Flyway)

Migrations are stored in `src/main/resources/db/migration` and follow Flyway's SQL naming convention `V{version}__{description}.sql`.

Guidelines:

- Keep migrations immutable once applied to production. New changes require a new `V{n}__desc.sql` migration.
- Do not use `ddl-auto: update` — Flyway is the source of truth for schema changes.
- For existing databases that are not yet under Flyway control, set `baseline-on-migrate: true` and choose an appropriate `baselineVersion` (for example `1`).

Common commands:

```sh
# Run migrations against local DB
mvn -DskipITs=true -Dflyway.configurationFile=src/main/resources/application-dev.yml flyway:migrate

# Clean (destructive) + migrate (use with caution)
mvn -DskipITs=true flyway:clean flyway:migrate
```

