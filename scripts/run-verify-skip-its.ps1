# Windows PowerShell script
if (Test-Path .\mvnw.cmd) {
  .\mvnw.cmd -B -DskipITs=true clean verify
} else {
  mvn -B -DskipITs=true clean verify
}
