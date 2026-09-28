import os,sys  
for root,dirs,files in os.walk('src'):  
  for f in files:  
    if f.endswith('.java'):  
      p=os.path.join(root,f)  
      try:  
        with open(p,'rb') as fh:  
          b=fh.read(3)  
          if b==b'\xEF\xBB\xBF':  
            print(p)  
      except:  
        pass  
