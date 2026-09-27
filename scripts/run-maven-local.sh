#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR=$(cd "$(dirname "$0")/.." && pwd)
# prefer .maven-dist/apache-maven-*/bin/mvn if available
if [ -d "$ROOT_DIR/.maven-dist" ]; then
  MVN_PATH=$(ls -d "$ROOT_DIR/.maven-dist"/*/bin/mvn 2>/dev/null | head -n1 || true)
  if [ -n "$MVN_PATH" -a -x "$MVN_PATH" ]; then
    "$MVN_PATH" "$@"
    exit $?
  fi
fi
# fallback to mvnw
if [ -x ./mvnw ]; then
  ./mvnw "$@"
  exit $?
fi
# fallback to system mvn
if command -v mvn >/dev/null 2>&1; then
  mvn "$@"
  exit $?
fi

echo "No Maven found: place a maven binary in .maven-dist, make ./mvnw executable, or install mvn on PATH." >&2
exit 1
