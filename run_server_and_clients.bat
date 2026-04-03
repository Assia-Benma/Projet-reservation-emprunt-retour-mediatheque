@echo off
setlocal EnableExtensions EnableDelayedExpansion

for %%I in ("%~dp0.") do set "ROOT=%%~fI"
cd /d "%ROOT%"

set "BUILD_DIR=%ROOT%\build"
set "TOOLS_DIR=%ROOT%\.tools"
set "JUNIT_VERSION=1.11.4"
set "JUNIT_JAR=%TOOLS_DIR%\junit-platform-console-standalone-%JUNIT_VERSION%.jar"
set "JUNIT_URL=https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/%JUNIT_VERSION%/junit-platform-console-standalone-%JUNIT_VERSION%.jar"
set "HOST=127.0.0.1"
set "HOST_SET=0"
set "COMPILE_ONLY=0"
set "PAUSE_AT_END=1"

for %%A in (%*) do (
    if /I "%%~A"=="--compile-only" set "COMPILE_ONLY=1"
    if /I "%%~A"=="--no-pause" set "PAUSE_AT_END=0"
    if /I not "%%~A"=="--compile-only" if /I not "%%~A"=="--no-pause" if "!HOST_SET!"=="0" (
        set "HOST=%%~A"
        set "HOST_SET=1"
    )
)

echo [1/4] Preparing build folder...
if exist "%BUILD_DIR%" (
    rmdir /s /q "%BUILD_DIR%"
)
mkdir "%BUILD_DIR%\classes" >nul 2>&1
mkdir "%TOOLS_DIR%" >nul 2>&1

if not exist "%JUNIT_JAR%" (
    echo [2/4] Downloading JUnit dependency for compilation...
    powershell -NoLogo -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%JUNIT_URL%' -OutFile '%JUNIT_JAR%'"
    if errorlevel 1 (
        echo ERROR: Failed to download dependency.
        goto :fail
    )
) else (
    echo [2/4] Dependency already present.
)

echo [3/4] Compiling Java sources...
set "SOURCES_FILE=%BUILD_DIR%\sources.txt"
dir /s /b "%ROOT%\src\*.java" > "%SOURCES_FILE%"

javac -encoding UTF-8 -Xlint:all -cp "%JUNIT_JAR%" -d "%BUILD_DIR%\classes" @"%SOURCES_FILE%"
if errorlevel 1 (
    echo ERROR: Compilation failed.
    goto :fail
)

if "%COMPILE_ONLY%"=="1" (
    echo Compilation completed. Launch skipped.
    if "%PAUSE_AT_END%"=="1" pause
    exit /b 0
)

echo [4/4] Launching server and sample clients...
echo Server host: %HOST%

timeout /t 1 >nul
start "Mediatheque Server" cmd /k "pushd ""%ROOT%"" && java -cp ""%BUILD_DIR%\classes"" bibliotheque.mediatheque.server.ServerMediatheque"

timeout /t 2 >nul
start "Reservation Client" cmd /k "pushd ""%ROOT%"" && java -cp ""%BUILD_DIR%\classes"" bibliotheque.mediatheque.client.ReservationClient %HOST% 2000"
start "Emprunt Client" cmd /k "pushd ""%ROOT%"" && java -cp ""%BUILD_DIR%\classes"" bibliotheque.mediatheque.client.EmpruntClient %HOST% 2001"
start "Retour Client" cmd /k "pushd ""%ROOT%"" && java -cp ""%BUILD_DIR%\classes"" bibliotheque.mediatheque.client.RetourClient %HOST% 2002"

echo Done. 4 terminals opened (1 server + 3 clients).
echo Tip: in clients, type commands then 'quitter' to close.
if "%PAUSE_AT_END%"=="1" pause
exit /b 0

:fail
echo.
echo Script failed. See errors above.
if "%PAUSE_AT_END%"=="1" pause
exit /b 1
