import os
for root,dirs,files in os.walk('src'):
    for f in files:
        if f.endswith('.java'):
            p = os.path.join(root,f)
            b = open(p,'rb').read(4)
            print(p, list(b))
