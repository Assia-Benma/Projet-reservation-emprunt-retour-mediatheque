@echo off
setlocal EnableExtensions EnableDelayedExpansion

for %%I in ("%~dp0.") do set "ROOT=%%~fI"
cd /d "%ROOT%"

set "BUILD_DIR=%ROOT%\build"
set "TOOLS_DIR=%ROOT%\.tools"
set "JUNIT_VERSION=1.11.4"
set "JUNIT_JAR=%TOOLS_DIR%\junit-platform-console-standalone-%JUNIT_VERSION%.jar"
set "JUNIT_URL=https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/%JUNIT_VERSION%/junit-platform-console-standalone-%JUNIT_VERSION%.jar"
set "PAUSE_AT_END=1"

for %%A in (%*) do (
    if /I "%%~A"=="--no-pause" set "PAUSE_AT_END=0"
)

if exist "%BUILD_DIR%" (
    rmdir /s /q "%BUILD_DIR%"
)
mkdir "%BUILD_DIR%\classes" >nul 2>&1
mkdir "%TOOLS_DIR%" >nul 2>&1

echo [1/5] Build folders ready.

if not exist "%JUNIT_JAR%" (
    echo [2/5] Downloading JUnit platform standalone %JUNIT_VERSION%...
    powershell -NoLogo -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%JUNIT_URL%' -OutFile '%JUNIT_JAR%'"
    if errorlevel 1 (
        echo ERROR: Failed to download JUnit from %JUNIT_URL%
        goto :fail
    )
) else (
    echo [2/5] JUnit jar already present.
)

echo [3/5] Collecting Java sources...
set "SOURCES_FILE=%BUILD_DIR%\sources.txt"
dir /s /b "%ROOT%\src\*.java" > "%SOURCES_FILE%"

echo [4/5] Compiling sources...
javac -encoding UTF-8 -Xlint:all -cp "%JUNIT_JAR%" -d "%BUILD_DIR%\classes" @"%SOURCES_FILE%"
if errorlevel 1 (
    echo ERROR: Compilation failed.
    goto :fail
)

echo [5/5] Running test suite...
java -jar "%JUNIT_JAR%" --class-path "%BUILD_DIR%\classes" --scan-class-path --details=tree --disable-banner
if errorlevel 1 (
    echo ERROR: Some tests failed.
    goto :fail
)

echo SUCCESS: compilation and all tests passed.
if "%PAUSE_AT_END%"=="1" pause
exit /b 0

:fail
echo.
echo Script failed. See errors above.
if "%PAUSE_AT_END%"=="1" pause
exit /b 1
