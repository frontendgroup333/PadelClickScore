@echo off
setlocal
set GRADLE_VERSION=8.9
set BOOTSTRAP=%USERPROFILE%\.gradle\padel-bootstrap
set GRADLE_HOME_DIR=%BOOTSTRAP%\gradle-%GRADLE_VERSION%
set DIST_ZIP=%BOOTSTRAP%\gradle-%GRADLE_VERSION%-bin.zip
set DIST_URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip
if not exist "%GRADLE_HOME_DIR%\bin\gradle.bat" (
  if not exist "%BOOTSTRAP%" mkdir "%BOOTSTRAP%"
  echo Downloading Gradle %GRADLE_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%DIST_URL%' -OutFile '%DIST_ZIP%'"
  if errorlevel 1 exit /b 1
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%DIST_ZIP%' -DestinationPath '%BOOTSTRAP%' -Force"
  if errorlevel 1 exit /b 1
)
call "%GRADLE_HOME_DIR%\bin\gradle.bat" %*
