# PowerShell helper: run maven from .maven-dist, then mvnw, then system mvn
param([Parameter(ValueFromRemainingArguments=$true)] $args)
$root = Resolve-Path -Path "$(Split-Path -Path $PSScriptRoot -Parent)" | Select-Object -ExpandProperty Path
$mavenDist = Get-ChildItem -Path (Join-Path $root '.maven-dist') -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
if ($mavenDist) {
  $mvn = Join-Path $mavenDist.FullName 'bin\mvn.cmd'
  if (Test-Path $mvn) { & $mvn @args; exit $LASTEXITCODE }
}
if (Test-Path .\mvnw.cmd) { & .\mvnw.cmd @args; exit $LASTEXITCODE }
if (Get-Command mvn -ErrorAction SilentlyContinue) { mvn @args; exit $LASTEXITCODE }
Write-Error "No Maven available. Put a distribution in .maven-dist, make mvnw available, or install Maven."
exit 1
