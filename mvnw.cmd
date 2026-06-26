@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script
@REM Uses globally installed Maven when available; otherwise downloads and caches.
@REM ----------------------------------------------------------------------------

@IF "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%
@IF "%OS%"=="Windows_NT" @SETLOCAL

@REM ==== Java validation ====
IF NOT "%JAVA_HOME%" == "" GOTO OkJHome
WHERE java >NUL 2>&1
IF %ERRORLEVEL% EQU 0 GOTO CheckGlobalMvn
echo.
echo Error: JAVA_HOME not set and no 'java' command found on PATH.
echo.
GOTO error

:OkJHome
IF EXIST "%JAVA_HOME%\bin\java.exe" GOTO CheckGlobalMvn
echo.
echo Error: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
GOTO error

@REM ==== Prefer globally installed Maven ====
:CheckGlobalMvn
WHERE mvn >NUL 2>&1
IF %ERRORLEVEL% NEQ 0 GOTO FindMvn

@REM Global mvn found — use it directly
mvn %MAVEN_OPTS% %*
SET MVN_EXIT=%ERRORLEVEL%
IF "%OS%"=="Windows_NT" @ENDLOCAL
EXIT /B %MVN_EXIT%

@REM ==== No global Maven — use cached wrapper or download ====
:FindMvn
SET MAVEN_PROJECTBASEDIR=%~dp0
IF NOT "%MAVEN_PROJECTBASEDIR:~-1%"=="\" SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR%\

SET WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties

FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%WRAPPER_PROPERTIES%") DO (
    IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
)

SET MAVEN_USER_HOME=%USERPROFILE%\.m2\wrapper
IF NOT EXIST "%MAVEN_USER_HOME%" MKDIR "%MAVEN_USER_HOME%"

FOR %%F IN ("%DISTRIBUTION_URL%") DO SET MAVEN_DIST_NAME=%%~nF
SET MAVEN_HOME=%MAVEN_USER_HOME%\dists\%MAVEN_DIST_NAME%

IF EXIST "%MAVEN_HOME%\bin\mvn.cmd" GOTO RunMvn
IF EXIST "%MAVEN_HOME%\bin\mvn.bat" GOTO RunMvn

@REM Not cached — download
echo.
echo Maven not found globally or cached. Downloading...
echo   From: %DISTRIBUTION_URL%
echo   To:   %MAVEN_HOME%
echo.

IF NOT EXIST "%MAVEN_USER_HOME%\dists" MKDIR "%MAVEN_USER_HOME%\dists"
SET MAVEN_ZIP=%MAVEN_USER_HOME%\dists\%MAVEN_DIST_NAME%.zip

powershell -NoProfile -Command "[Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12;(New-Object System.Net.WebClient).DownloadFile('%DISTRIBUTION_URL%','%MAVEN_ZIP%')"
IF %ERRORLEVEL% NEQ 0 (
  echo.
  echo [ERROR] Download failed. Install Maven globally so 'mvn' is on PATH, then re-run.
  GOTO error
)

powershell -NoProfile -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_USER_HOME%\dists' -Force"
DEL "%MAVEN_ZIP%"

FOR /D %%D IN ("%MAVEN_USER_HOME%\dists\apache-maven-*") DO SET MAVEN_HOME=%%D

:RunMvn
SET MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd
IF NOT EXIST "%MVN_CMD%" SET MVN_CMD=%MAVEN_HOME%\bin\mvn.bat
IF NOT EXIST "%MVN_CMD%" (
  echo [ERROR] Cannot find mvn.cmd or mvn.bat in %MAVEN_HOME%\bin\
  GOTO error
)

"%MVN_CMD%" %MAVEN_OPTS% %*
IF %ERRORLEVEL% NEQ 0 GOTO error

IF "%OS%"=="Windows_NT" @ENDLOCAL
EXIT /B 0

:error
IF "%OS%"=="Windows_NT" @ENDLOCAL
EXIT /B 1
