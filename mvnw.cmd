@echo off
@REM Maven Start Up Batch script

@setlocal

set ERROR_CODE=0

@REM Find project base directory
set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

@REM Determine java command
set "JAVACMD=java"
if not "%JAVA_HOME%" == "" (
  if exist "%JAVA_HOME%\bin\java.exe" set "JAVACMD=%JAVA_HOME%\bin\java.exe"
)

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

"%JAVACMD%" ^
  %MAVEN_OPTS% ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  -classpath %WRAPPER_JAR% ^
  %WRAPPER_LAUNCHER% %*

if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & exit /b %ERROR_CODE%
