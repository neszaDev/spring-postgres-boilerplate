import sys, yaml
f = sys.argv[1]
try:
    with open(f, 'r', encoding='utf-8') as fh:
        yaml.safe_load(fh)
    print('PARSE_OK')
except Exception as e:
    print('PARSE_ERROR:', e)
