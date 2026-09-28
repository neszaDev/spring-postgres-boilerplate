#!/usr/bin/env python3
"""
Safe cleanup script: removes UTF-8 BOM at start of files and removes stray single-character
lines that are only a backslash (\) or a backtick (`). Targets common text files that caused
CI compile failures: Java, YAML, XML, properties, markdown, and workflow files.

Usage: python scripts/fix_bom_and_stray_lines.py
"""

from pathlib import Path
import sys

ROOT = Path('.').resolve()
# patterns to search
GLOB_PATTERNS = [
    '**/*.java',
    '**/*.yml',
    '**/*.yaml',
    '**/*.xml',
    '**/*.properties',
    '**/*.md',
    '**/*.txt',
    '.github/workflows/*.yml',
    '.github/workflows/*.yaml'
]

BOM = b'\xEF\xBB\xBF'

def process_file(p: Path):
    try:
        data = p.read_bytes()
    except Exception as e:
        print(f"[SKIP] Could not read {p}: {e}")
        return False

    changed = False
    # remove BOM
    if data.startswith(BOM):
        data = data[len(BOM):]
        changed = True

    # try decode as utf-8
    try:
        text = data.decode('utf-8')
    except Exception:
        # if can't decode utf-8, skip
        print(f"[SKIP] Non-UTF8 file: {p}")
        return False

    lines = text.splitlines(keepends=True)
    new_lines = []
    for ln in lines:
        if ln.strip() in ('\\', '`'):
            # remove stray single-character line
            print(f"[FIX] Removing stray line in: {p}")
            changed = True
            continue
        new_lines.append(ln)

    new_text = ''.join(new_lines)

    # Normalize LF for workflow YAML files for consistency
    rel = str(p.as_posix())
    if rel.startswith('.github/') and p.suffix.lower() in ('.yml', '.yaml'):
        norm = new_text.replace('\r\n', '\n')
        if norm != new_text:
            new_text = norm
            changed = True

    if changed:
        # write back with UTF-8 and LF newlines
        try:
            p.write_text(new_text, encoding='utf-8', newline='\n')
            print(f"[WRITE] Fixed: {p}")
            return True
        except Exception as e:
            print(f"[ERROR] Could not write {p}: {e}")
            return False
    return False


def main():
    files = set()
    for pat in GLOB_PATTERNS:
        for p in ROOT.glob(pat):
            # skip .git
            if '.git/' in str(p):
                continue
            # skip the script itself
            if p.resolve() == (ROOT / 'scripts' / 'fix_bom_and_stray_lines.py').resolve():
                continue
            files.add(p)

    files = sorted(files)
    fixed_count = 0
    for f in files:
        if process_file(f):
            fixed_count += 1

    print(f"Done. Files fixed: {fixed_count}")

if __name__ == '__main__':
    main()
