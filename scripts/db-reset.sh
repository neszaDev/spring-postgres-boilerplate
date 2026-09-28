#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"

cat <<'EOF'
WARNING: This will reset the database (destructive).
This script attempts to run Flyway clean + migrate via Maven. It prefers running inside the
`app-local` container (Docker Compose local profile) when Docker is available.
EOF

read -p "Continue? [y/N]: " confirm
if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
  echo "Aborted."
  exit 1
fi

MVN_CMD="./mvnw"
if [ ! -x "$MVN_CMD" ]; then
  MVN_CMD="mvn"
fi

if command -v docker > /dev/null 2>&1 && [ -f docker-compose.yml ]; then
  echo "Running Flyway clean/migrate inside app-local container (Docker Compose local profile)..."
  # Note: this will run Maven inside the app-local build target. It may download plugins if needed.
  docker compose --profile local run --rm app-local sh -c "$MVN_CMD -B -DskipTests=true -Dflyway.cleanOnValidationError=true flyway:clean flyway:migrate"
else
  echo "Docker is not available or docker-compose.yml missing. Running Flyway via Maven locally."
  echo "This may download the Flyway Maven plugin and requires network access."
  $MVN_CMD -B -DskipTests=true -Dflyway.cleanOnValidationError=true flyway:clean flyway:migrate
fi
