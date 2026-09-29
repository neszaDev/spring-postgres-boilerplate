from pathlib import Path
base = Path.cwd()
sec = base / 'src/main/java/com/example/boilerplate/security/SecurityConfig.java'
if sec.exists():
    content = sec.read_text(encoding='utf-8')
    content = content.replace('<<<<<<< HEAD\n','').replace('\n=======\n','\n').replace('\n>>>>>>> 7ffc93fdb55ace73c33f0cbba5f2a57ffd541b6f\n','\n')
    content = content.replace('<<<<<<< HEAD','').replace('>>>>>>>','')
    sec.write_text(content, encoding='utf-8')
    print('Fixed SecurityConfig')
else:
    print('SecurityConfig not found')

jwt = base / 'src/main/java/com/example/boilerplate/security/JwtService.java'
if jwt.exists():
    s = jwt.read_text(encoding='utf-8')
    s_new = s.replace('import org.springframework.beans.factory.annotation.Qualifier;\n','')
    s_new = s_new.replace('@Qualifier("jwtProperties") ','')
    if s != s_new:
        jwt.write_text(s_new, encoding='utf-8')
        print('Updated JwtService')
    else:
        print('JwtService no changes')
else:
    print('JwtService not found')
