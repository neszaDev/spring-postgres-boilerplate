$path = "$env:REPO_ROOT\src\main\java\com\example\boilerplate\security\JwtService.java"
if (-not (Test-Path $path)) { Write-Output "File not found: $path"; exit 0 }
$text = Get-Content -LiteralPath $path -Raw -Encoding UTF8
if ($text -match 'Qualifier') { Write-Output 'JwtService already qualified'; exit 0 }
$text = $text -replace 'import org.springframework.core.env.Environment;','import org.springframework.core.env.Environment;`nimport org.springframework.beans.factory.annotation.Qualifier;'
$text = $text -replace 'public JwtService\(JwtProperties properties, Environment env\)','public JwtService([Qualifier("jwtProperties")] JwtProperties properties, Environment env)'
Set-Content -LiteralPath $path -Value $text -Encoding UTF8
Write-Output 'JwtService patched'
