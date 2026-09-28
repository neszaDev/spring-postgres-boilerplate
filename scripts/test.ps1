<#[
.SYNOPSIS
  Run tests (Windows PowerShell)
#]>
[CmdletBinding()]
param()
Set-StrictMode -Version Latest
$scriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location (Join-Path $scriptRoot '..')

if (Test-Path '.\mvnw.cmd') {
  $mvnCmd = '.\mvnw.cmd'
} elseif (Test-Path '.\mvnw') {
  $mvnCmd = '.\mvnw'
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
  $mvnCmd = 'mvn'
} else {
  throw 'No Maven wrapper and no mvn in PATH. Generate the wrapper with: mvn -N io.takari:maven:wrapper'
}

if ($env:SKIP_INTEGRATION -eq 'true') {
  Write-Host 'SKIP_INTEGRATION=true — running tests with integration-tests skipped (project-dependent)'
  & $mvnCmd -B -DskipITs=true test
} else {
  & $mvnCmd -B test
}
