#!/usr/bin/env bash
set -euo pipefail
if [ -x ./mvnw ]; then
  ./mvnw -B -DskipITs=true clean verify
else
  mvn -B -DskipITs=true clean verify
fi
