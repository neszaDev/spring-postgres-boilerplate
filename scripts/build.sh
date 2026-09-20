#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT_DIR"
MVN_CMD="./mvnw"
if [ ! -x "$MVN_CMD" ]; then
  MVN_CMD="mvn"
fi
printf "Using %s\n" "$MVN_CMD"
printf "Building package (skip tests)...\n"
$MVN_CMD -B -DskipTests package
