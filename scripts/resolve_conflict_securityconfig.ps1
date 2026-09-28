$path = Join-Path $env:REPO_ROOT 'src\main\java\com\example\boilerplate\security\SecurityConfig.java'
Write-Output "Resolving conflict in: $path"
if (-not (Test-Path $path)) { Write-Output "File not found: $path"; exit 0 }
$s = Get-Content -LiteralPath $path -Raw -Encoding UTF8
if ($s -notmatch '<<<<<<<') { Write-Output 'No conflict markers present'; exit 0 }
$start = $s.IndexOf('=======')
$end = $s.IndexOf('>>>>>>>')
if ($start -eq -1 -or $end -eq -1) { Write-Output 'Conflict markers not found properly'; exit 1 }
$new = $s.Substring($start + 7, $end - ($start + 7))
Set-Content -LiteralPath $path -Value $new -Encoding UTF8
Write-Output 'Conflict resolved: kept latter block'
