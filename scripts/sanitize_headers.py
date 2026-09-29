import os,sys
files = [
  'src/main/java/com/example/boilerplate/config/JwtProperties.java',
  'src/main/java/com/example/boilerplate/security/JwtService.java',
  'src/main/java/com/example/boilerplate/security/SecurityConfig.java',
  'src/main/java/com/example/boilerplate/BoilerplateApplication.java',
  'src/test/java/com/example/boilerplate/auth/JwtServiceTest.java',
  'src/test/java/com/example/boilerplate/e2e/HealthE2EIT.java'
]
changed = []
for f in files:
    p = os.path.join(os.getcwd(), f)
    if not os.path.exists(p):
        continue
    with open(p, 'rb') as fh:
        b = fh.read()
    # strip UTF-8 BOM
    if b.startswith(b'\xef\xbb\xbf'):
        b = b[3:]
    try:
        s = b.decode('utf-8')
    except Exception:
        s = b.decode('utf-8', errors='replace')
    lines = s.splitlines()
    # find first non-empty line and ensure it starts with 'package' or an annotation/import/class
    for i,l in enumerate(lines):
        if l.strip() == '':
            continue
        # if the line contains 'package' not at position 0, cut before package
        if 'package ' in l and not l.strip().startswith('package'):
            idx = l.find('package')
            lines[i] = l[idx:]
            changed.append(f)
        # if the line starts with backtick or stray char, remove until package or import or @ or public/class/interface
        if l and (l[0] == '\ufeff' or l[0] in ['`','\\']):
            # remove leading non-ASCII or control chars
            newl = l.lstrip('\ufeff`\\ \t')
            if newl != l:
                lines[i] = newl
                changed.append(f)
        break
    new_s = '\n'.join(lines) + ('\n' if s.endswith('\n') else '')
    with open(p, 'w', encoding='utf-8') as fh:
        fh.write(new_s)

print('Sanitized files:')
for c in set(changed):
    print(c)
