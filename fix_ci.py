from pathlib import Path  
p=Path('.github\\workflows\\ci.yml')  
s=p.read_text(encoding='utf-8')  
