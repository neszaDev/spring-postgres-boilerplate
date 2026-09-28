#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"
MVN_CMD="./mvnw"
if [ ! -x "$MVN_CMD" ]; then
  MVN_CMD="mvn"
fi
printf "Using %s\n" "$MVN_CMD"
# Set SKIP_INTEGRATION=true to skip potential integration tests that require Docker/Testcontainers
if [ "${SKIP_INTEGRATION:-false}" = "true" ]; then
  printf "SKIP_INTEGRATION=true — running tests with integration-tests skipped (project-dependent)\n"
  $MVN_CMD -B -DskipITs=true test
else
  $MVN_CMD -B test
fi
