@echo off
REM ─────────────────────────────────────────────────────────────────────────────
REM setup.bat — First-time project setup (Windows)
REM
REM Run once after cloning:
REM
REM   Command Prompt (cmd.exe):   setup.bat
REM   PowerShell:                 .\setup.bat
REM
REM If PowerShell shows "not recognized" error, you typed "setup.bat" without
REM the dot-backslash prefix. PowerShell requires .\  to run scripts in the
REM current directory. Use: .\setup.bat
REM
REM What it does:
REM   1. Verifies Java 11 is present
REM   2. Builds shared-core modules
REM   3. Builds team-a and installs its test-jar (+ test-sources.jar) to .m2
REM   4. Compiles team-b to verify the dependency resolves
REM   5. Prints a "ready" message with next steps
REM ─────────────────────────────────────────────────────────────────────────────

setlocal enabledelayedexpansion

echo.
echo ============================================================
echo   Shared Automation Framework — First Time Setup
echo ============================================================
echo.

REM ── 0. Fix PowerShell execution policy ───────────────────────────────────────
REM
REM Government machines often set PowerShell execution policy to "Restricted",
REM which blocks .ps1 scripts — including those called internally by mvnw.cmd
REM and the Maven wrapper download step.
REM
REM RemoteSigned allows locally created scripts to run freely and only requires
REM a signature for scripts downloaded from the internet. This is the minimum
REM policy needed for Maven wrapper and VS Code extension tasks to work.
REM
REM Scope=CurrentUser: applies to your profile only — no admin rights required.
REM The 2>nul suppresses the error if PowerShell itself is blocked by policy.
REM
REM Skip if setup.ps1 already handled the execution policy
if "%SKIP_POLICY%"=="1" goto :skip_policy

echo ^> Setting PowerShell execution policy (CurrentUser: RemoteSigned)...
powershell -NoProfile -Command "Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force" 2>nul
if errorlevel 1 (
  echo [WARN] Could not set execution policy automatically.
  echo        If Maven wrapper or mvnw.cmd fails, run this manually in PowerShell:
  echo          Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force
  echo        Or ask IT to set it, then re-run .\setup.bat
  echo.
) else (
  echo [OK] Execution policy set to RemoteSigned for current user
)

:skip_policy

REM ── 1. Check Java ────────────────────────────────────────────────────────────
echo ^> Checking Java version...
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
  set JAVA_VER=%%g
)
set JAVA_VER=%JAVA_VER:"=%

REM Extract major version — handles both "1.8.x" (Java 8) and "11/17/21/26" formats
for /f "tokens=1 delims=." %%a in ("%JAVA_VER%") do set JAVA_MAJOR=%%a
if "%JAVA_MAJOR%"=="1" (
  REM Old format: 1.8.x → second token is the real major
  for /f "tokens=2 delims=." %%b in ("%JAVA_VER%") do set JAVA_MAJOR=%%b
)

REM Require Java 11 or higher (pom.xml compiles to --release 11 bytecode,
REM which any JDK 11+ supports — no need to restrict to exactly Java 11)
if %JAVA_MAJOR% LSS 11 (
  echo [ERROR] Java 11 or higher required. Found: Java %JAVA_MAJOR% ^(%JAVA_VER%^)
  echo         Install OpenJDK 11 ^(or any newer JDK^), set JAVA_HOME, then re-run.
  exit /b 1
) else (
  echo [OK] Java %JAVA_MAJOR% found ^(minimum required: 11^)
)

REM ── 2. Mark IDE config files so local IDE changes do not dirty the working tree
echo.
echo ^> Configuring git to ignore local IDE file changes...
for /f "usebackq" %%F in (`git ls-files .idea/ .vscode/`) do git update-index --skip-worktree "%%F"
echo [OK] IDE config files marked skip-worktree

REM ── 3. Build shared-core ─────────────────────────────────────────────────────
echo.
echo ^> Building shared-core modules...
call mvnw.cmd install -pl shared-core/cucumber-base,shared-core/selenium-base,shared-core/api-base,shared-core/testdata-base -am -DskipTests -q
if errorlevel 1 (
  echo [ERROR] shared-core build failed. Check Maven output above.
  exit /b 1
)
echo [OK] shared-core installed

REM ── 3. Build team-a ──────────────────────────────────────────────────────────
echo.
echo ^> Building team-a-system1 ^(shared step-jar + sources^)...
call mvnw.cmd install -pl team-a-system1 -DskipTests -q
if errorlevel 1 (
  echo [ERROR] team-a-system1 build failed.
  exit /b 1
)
echo [OK] team-a-system1 installed

REM ── 4. Compile team-b ────────────────────────────────────────────────────────
echo.
echo ^> Verifying team-b-system2 resolves team-a dependency...
call mvnw.cmd compile -pl team-b-system2 -q
if errorlevel 1 (
  echo [ERROR] team-b-system2 compile failed. team-a test-jar may not be in .m2.
  exit /b 1
)
echo [OK] team-b-system2 compiles successfully

REM ── 5. Done ──────────────────────────────────────────────────────────────────
echo.
echo ============================================================
echo   Setup complete! Dependencies installed.
echo ============================================================
echo.
echo   NEXT -- open your IDE NOW (setup must finish before IDE import):
echo.
echo   +-- IntelliJ IDEA -----------------------------------------+
echo   ^|  File -^> Open -^> select THIS folder                      ^|
echo   ^|  Click "Open as Maven Project" when prompted             ^|
echo   ^|  Run configs appear in toolbar automatically             ^|
echo   ^|  Ctrl+Click any .feature step -^> jumps to correct .java  ^|
echo   +-----------------------------------------------------------+
echo.
echo   +-- VS Code ------------------------------------------------+
echo   ^|  Run: code .                                              ^|
echo   ^|  Click "Install" on extension recommendations popup      ^|
echo   ^|  Wait for Java indexing (progress bar bottom-left)       ^|
echo   ^|  Ctrl+Click any .feature step -^> jumps to correct .java  ^|
echo   +-----------------------------------------------------------+
echo.
echo   Run tests from terminal (IDE optional):
echo     mvnw.cmd test -pl team-a-system1
echo     mvnw.cmd test -pl team-b-system2
echo.
pause
