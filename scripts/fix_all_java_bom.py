import sys, os, re
root = os.getcwd()
count=0
fixed_files=[]
for dirpath, dirnames, filenames in os.walk(os.path.join(root,'src')):
    for fn in filenames:
        if fn.endswith('.java'):
            path = os.path.join(dirpath,fn)
            with open(path,'rb') as fh:
                b = fh.read()
            orig_b = b
            # strip UTF-8 BOM
            if b.startswith(b'\xef\xbb\xbf'):
                b=b[3:]
            try:
                s = b.decode('utf-8')
            except Exception:
                s = b.decode('utf-8',errors='replace')
            # find package or import or annotation
            m = re.search(r'(?m)^(package\s+[\w\.]+;)', s)
            if not m:
                m = re.search(r'(?m)^(import\s+[\w\.]+;)', s)
            if not m:
                m = re.search(r'(?m)^(@[A-Za-z_][\w\.]*)', s)
            if m and m.start()>0:
                new = s[m.start():]
                changed = True
            else:
                new = s
                changed = (orig_b!=b)
            # remove git conflict markers entirely
            if '<<<<<<<' in new or '>>>>>>>' in new or '=======' in new:
                new = re.sub(r'(?m)^<<<<<<<.*$','',new)
                new = re.sub(r'(?m)^>>>>>>>.*$','',new)
                new = re.sub(r'(?m)^=======$','',new)
                changed = True
            # remove lines that are only a backtick or a single backslash
            new = re.sub(r'(?m)^\s*[`\\]\s*\r?\n','',new)
            if changed:
                with open(path,'w',encoding='utf-8') as fh:
                    fh.write(new)
                fixed_files.append(path)
                count+=1
print('Fixed',count,'java files')
for f in fixed_files:
    print(f)
