from pathlib import Path
p = Path('src/main/java/com/example/boilerplate/config/SecurityConfig.java')
s = p.read_text(encoding='utf-8')
s = s.replace('@Bean\n  public PasswordEncoder passwordEncoder() {\n    return new BCryptPasswordEncoder();\n  }', '@Bean("configPasswordEncoder")\n  public PasswordEncoder configPasswordEncoder() {\n    return new BCryptPasswordEncoder();\n  }')
p.write_text(s,encoding='utf-8')
print('patched')
