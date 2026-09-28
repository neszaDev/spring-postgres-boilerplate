@echo off
REM Minimal Maven Wrapper CMD that uses a local .maven-dist if present, otherwise runs the wrapper jar.
SETLOCAL
SET MVNW_DIR=%~dp0
IF EXIST "%MVNW_DIR%.maven-dist\apache-maven-3.9.6\bin\mvn.cmd" (
  "%MVNW_DIR%.maven-dist\apache-maven-3.9.6\bin\mvn.cmd" %*
  EXIT /B %ERRORLEVEL%
)
REM Fallback to java -jar wrapper
java -jar "%MVNW_DIR%.mvn\wrapper\maven-wrapper.jar" %*

