from pathlib import Path
p = Path('src/main/java/com/example/boilerplate/security/JwtService.java')
s = p.read_text(encoding='utf-8')
repl = '''    if ("00000000000000000000000000000000".equals(secret) || (secret != null && secret.length() < 32)) {
      if (!isTest) {
        log.error(
            "JWT secret is insecure or too short. Provide a secure random 32+ character secret in"
                + " app.security.jwt.secret");
        throw new IllegalStateException(
            "JWT secret missing or invalid (too short or default placeholder)");
      } else {
        log.warn("JWT secret is insecure or too short but running with 'test' profile; using test fallback key");
        secret = "00000000000000000000000000000000";
      }
    }
'''
if 'JWT secret missing or invalid (too short or default placeholder)' in s:
    s_new = s.replace('if (!isTest\n        && ("00000000000000000000000000000000".equals(secret)\n            || (secret != null && secret.length() < 32))) {\n      log.error(\n          "JWT secret is insecure or too short. Provide a secure random 32+ character secret in"\n              + " app.security.jwt.secret");\n      throw new IllegalStateException(\n          "JWT secret missing or invalid (too short or default placeholder)");\n    }\n', repl)
    if s_new != s:
        p.write_text(s_new, encoding='utf-8')
        print('JwtService patched')
    else:
        print('JwtService pattern not found; no change')
else:
    print('JwtService seems already patched or pattern missing')
