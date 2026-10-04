@echo off
setlocal
set GRADLE_VERSION=8.9
if not defined GRADLE_USER_HOME set GRADLE_USER_HOME=%USERPROFILE%\.gradle
set DIST_DIR=%GRADLE_USER_HOME%\wrapper\dists\gradle-%GRADLE_VERSION%-bin\meva
set GRADLE_BIN=%DIST_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat

if exist "%GRADLE_BIN%" goto run_gradle
if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"
echo Downloading Gradle %GRADLE_VERSION%...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $archive=Join-Path '%DIST_DIR%' 'gradle-%GRADLE_VERSION%-bin.zip'; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile $archive; Expand-Archive -LiteralPath $archive -DestinationPath '%DIST_DIR%' -Force; Remove-Item $archive -Force"
if errorlevel 1 exit /b %ERRORLEVEL%
if not exist "%GRADLE_BIN%" (
  echo Could not install Gradle %GRADLE_VERSION%.
  exit /b 1
)

:run_gradle
call "%GRADLE_BIN%" %*
exit /b %ERRORLEVEL%
