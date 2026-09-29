#!/usr/bin/env python3
import os, subprocess, sys

jar = os.path.join(os.path.dirname(__file__), '..', 'google-java-format-1.17.0-all-deps.jar')
jar = os.path.normpath(jar)
if not os.path.exists(jar):
    print('formatter jar not found at', jar, file=sys.stderr)
    sys.exit(1)

count = 0
for root, dirs, files in os.walk(os.path.join(os.path.dirname(__file__), '..')):
    # skip VCS metadata
    if '.git' in root.split(os.sep):
        continue
    for f in files:
        if f.endswith('.java'):
            path = os.path.join(root, f)
            print('Formatting', path)
            try:
                subprocess.run(['java', '-jar', jar, '--replace', path], check=True)
                count += 1
            except subprocess.CalledProcessError as e:
                print('formatting failed for', path, '->', e, file=sys.stderr)

print('Formatted', count, 'files')

