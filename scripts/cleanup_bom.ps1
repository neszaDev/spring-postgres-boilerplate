 = New-Object System.Text.UTF8Encoding False  
 = @()  
 += Get-ChildItem -Path src -Recurse -Filter *.java -File -ErrorAction SilentlyContinue  
 += Get-ChildItem -Path src -Recurse -Filter *.yml -File -ErrorAction SilentlyContinue  
 += Get-ChildItem -Path src -Recurse -Filter *.yaml -File -ErrorAction SilentlyContinue  
 += Get-ChildItem -Path src -Recurse -Filter *.xml -File -ErrorAction SilentlyContinue  
 += Get-ChildItem -Path src -Recurse -Filter *.properties -File -ErrorAction SilentlyContinue  
 += Get-ChildItem -Path .github -Recurse -Filter *.yml -File -ErrorAction SilentlyContinue  
 = @()  
foreach ( in ) {  
  try {  = [System.IO.File]::ReadAllBytes(.FullName) } catch { continue }  
   = False  
  if (.Length -ge 3 -and [0] -eq 239 -and [1] -eq 187 -and [2] -eq 191) {  
     = [System.Text.Encoding]::UTF8.GetString(,3,.Length-3)  
    [System.IO.File]::WriteAllText(.FullName,,)  
     = True  
  }  
  try {  = Get-Content -Path .FullName -Raw -ErrorAction Stop } catch { continue }  
   =  -split '\r?\n'  
   =  | Where-Object { .Trim() -ne '\\' -and .Trim() -ne '' }  
