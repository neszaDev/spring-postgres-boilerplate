<#[
.SYNOPSIS
  Reset the database using Flyway (Windows PowerShell).
  This is destructive: it cleans and migrates the database.
#]>
[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location (Join-Path $scriptRoot '..')

Write-Host "WARNING: This will reset the database (destructive)."
$confirm = Read-Host 'Continue? [y/N]'
if ($confirm -ne 'y' -and $confirm -ne 'Y') {
  Write-Host 'Aborted.'
  exit 1
}

if (Test-Path '.\mvnw.cmd') {
  $mvnCmd = '.\mvnw.cmd'
} elseif (Test-Path '.\mvnw') {
  $mvnCmd = '.\mvnw'
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
  $mvnCmd = 'mvn'
} else {
  throw 'No Maven wrapper and no mvn in PATH. Generate the wrapper with: mvn -N io.takari:maven:wrapper'
}

$dockerAvailable = (Get-Command docker -ErrorAction SilentlyContinue) -ne $null

if ($dockerAvailable -and (Test-Path 'docker-compose.yml')) {
  Write-Host 'Running Flyway clean/migrate inside app-local container (Docker Compose local profile)...'
  docker compose --profile local run --rm app-local sh -c "$mvnCmd -B -DskipTests=true -Dflyway.cleanOnValidationError=true flyway:clean flyway:migrate"
} else {
  Write-Host 'Docker not available or docker-compose.yml missing. Running Flyway via Maven locally.'
  & $mvnCmd -B -DskipTests=true -Dflyway.cleanOnValidationError=true flyway:clean flyway:migrate
}
