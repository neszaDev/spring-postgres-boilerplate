from pathlib import Path
patterns = ['**/*.java','**/*.yml','**/*.yaml','**/*.xml','**/*.properties','**/*.md']
modified = []
for pat in patterns:
    for p in Path('.').rglob(pat):
        if not p.is_file():
            continue
        try:
            b = p.read_bytes()
        except Exception:
            continue
        changed = False
        if len(b) >= 3 and b[0:3] == b'ï»¿':
            p.write_bytes(b[3:])
            modified.append((p,'bom'))
            changed = True
        try:
            txt = p.read_text(encoding='utf-8')
        except Exception:
            try:
                txt = p.read_text(encoding='latin-1')
            except Exception:
                continue
        lines = txt.splitlines()
        new_lines = []
        for L in lines:
            if L.strip() in ('\', ''):
                changed = True
                modified.append((p,'stray:'+L.strip()))
            else:
                new_lines.append(L)
        if changed:
            p.write_text('
'.join(new_lines) + ('
' if txt.endswith('
') else ''), encoding='utf-8')

if modified:
    print('Modifications:')
    for p,t in modified:
        print(p, t)
else:
    print('No modifications found')
