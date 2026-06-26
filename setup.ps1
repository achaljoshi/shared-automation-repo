# setup.ps1 — First-time project setup (Windows PowerShell)
#
# Run once after cloning:
#   .\setup.ps1
#
# If you see "cannot be loaded because running scripts is disabled":
#   1. Open PowerShell as the SAME user (not as admin)
#   2. Run: Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force
#   3. Then re-run: .\setup.ps1
#
# This script sets its own execution policy first, then delegates to setup.bat.
# ─────────────────────────────────────────────────────────────────────────────

$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Shared Automation Framework - First Time Setup" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# ── Step 0: Fix execution policy ─────────────────────────────────────────────
# Government machines default to Restricted, which blocks .ps1 scripts and
# the Maven wrapper. RemoteSigned allows local scripts to run freely.
# Scope=CurrentUser requires no admin rights.
Write-Host "> Setting PowerShell execution policy..." -ForegroundColor Yellow
try {
    Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force
    Write-Host "[OK] Execution policy set to RemoteSigned (CurrentUser)" -ForegroundColor Green
} catch {
    Write-Host "[WARN] Could not set execution policy: $_" -ForegroundColor Yellow
    Write-Host "       Try running PowerShell as your own user (not as Administrator)" -ForegroundColor Yellow
    Write-Host "       or ask IT to run: Set-ExecutionPolicy RemoteSigned -Scope CurrentUser" -ForegroundColor Yellow
    Write-Host ""
}

# ── Step 1: Verify Java ───────────────────────────────────────────────────────
# PowerShell sometimes misses PATH entries set in System Properties (unlike cmd.exe).
# Refresh PATH from the registry before checking, and also try JAVA_HOME directly.
Write-Host "> Checking Java version..." -ForegroundColor Yellow

# Refresh PATH from both Machine and User registry so newly installed JDKs are found
$machinePath = [System.Environment]::GetEnvironmentVariable("Path", "Machine")
$userPath    = [System.Environment]::GetEnvironmentVariable("Path", "User")
$env:PATH    = "$machinePath;$userPath"

# Also honour JAVA_HOME if set — add its bin to PATH
if ($env:JAVA_HOME -and (Test-Path "$env:JAVA_HOME\bin\java.exe")) {
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
    Write-Host "  (using JAVA_HOME: $env:JAVA_HOME)" -ForegroundColor Gray
}

$javaCmd = Get-Command java -ErrorAction SilentlyContinue
if (-not $javaCmd) {
    Write-Host "[ERROR] 'java' not found on PATH and JAVA_HOME is not set." -ForegroundColor Red
    Write-Host "        Fix options:" -ForegroundColor Red
    Write-Host "          1. Close PowerShell, reopen it, and re-run .\setup.ps1" -ForegroundColor Yellow
    Write-Host "             (PATH changes from installers need a new shell session)" -ForegroundColor Yellow
    Write-Host "          2. Set JAVA_HOME in System Properties and re-run:" -ForegroundColor Yellow
    Write-Host "             [System Properties -> Environment Variables -> JAVA_HOME]" -ForegroundColor Yellow
    Write-Host "          3. Or run setup.bat from cmd.exe instead — it picks up PATH correctly" -ForegroundColor Yellow
    exit 1
}

try {
    $javaOutput = & java -version 2>&1
    $javaVerLine = $javaOutput | Select-String -Pattern 'version' | Select-Object -First 1
    $javaVerRaw = [regex]::Match($javaVerLine, '"([^"]+)"').Groups[1].Value

    # Handle both "1.8.x" (Java 8) and "11/17/21/26" formats
    $parts = $javaVerRaw.Split(".")
    $javaMajor = if ($parts[0] -eq "1") { [int]$parts[1] } else { [int]$parts[0] }

    if ($javaMajor -lt 11) {
        Write-Host "[ERROR] Java 11 or higher required. Found: Java $javaMajor ($javaVerRaw)" -ForegroundColor Red
        Write-Host "        Install OpenJDK 11 or newer, set JAVA_HOME, then re-run." -ForegroundColor Red
        exit 1
    }
    Write-Host "[OK] Java $javaMajor found (minimum required: 11)" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Could not determine Java version: $_" -ForegroundColor Red
    exit 1
}

# ── Step 2–4: Run setup.bat for the Maven build steps ────────────────────────
# setup.bat contains the full Maven build sequence. We call it here so the
# build logic lives in one place.
Write-Host ""
Write-Host "> Handing off to setup.bat for Maven build steps..." -ForegroundColor Yellow
Write-Host ""

# Use cmd /c so the batch file runs in its native environment
$batPath = Join-Path $PSScriptRoot "setup.bat"
if (-not (Test-Path $batPath)) {
    Write-Host "[ERROR] setup.bat not found at: $batPath" -ForegroundColor Red
    exit 1
}

# Skip the execution policy step inside setup.bat since we already did it here
# Pass SKIP_POLICY=1 so setup.bat skips Step 0
$env:SKIP_POLICY = "1"
cmd /c "`"$batPath`""
$exitCode = $LASTEXITCODE
$env:SKIP_POLICY = ""

if ($exitCode -ne 0) {
    Write-Host ""
    Write-Host "[ERROR] Setup failed. Check the output above." -ForegroundColor Red
    exit $exitCode
}
