from pathlib import Path
p = Path('src/main/java/com/example/boilerplate/security/JwtService.java')
if not p.exists():
    print('MISSING', p)
    raise SystemExit(0)
b = p.read_bytes()
if b.startswith(b'\xef\xbb\xbf'):
    print('BOM detected: stripping')
    p.write_bytes(b[3:])
else:
    print('No BOM found')
print('leading bytes:', list(p.read_bytes()[:20]))
