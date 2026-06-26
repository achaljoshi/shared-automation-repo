@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script
@REM
@REM Required ENV vars:
@REM   JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars:
@REM   MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM   MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM   MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM ----------------------------------------------------------------------------

@IF "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%

@REM Set local scope for the variables with windows NT shell
@IF "%OS%"=="Windows_NT" @SETLOCAL
@IF "%OS%"=="Windows_NT" @SET MAVEN_BATCH_ECHO=%MAVEN_BATCH_ECHO%

@REM ==== START VALIDATION ====
IF NOT "%JAVA_HOME%" == "" GOTO OkJHome

@REM Try to find java on PATH
WHERE java >NUL 2>&1
IF %ERRORLEVEL% EQU 0 GOTO FindMvn

echo.
echo Error: JAVA_HOME not set and no 'java' command found on PATH.
echo Please set the JAVA_HOME variable in your environment to match
echo the location of your Java installation.
echo.
GOTO error

:OkJHome
IF EXIST "%JAVA_HOME%\bin\java.exe" GOTO FindMvn

echo.
echo Error: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo Please set JAVA_HOME correctly or remove it from the environment.
echo.
GOTO error

:FindMvn
@REM Find the project base dir
SET MAVEN_PROJECTBASEDIR=%~dp0
IF NOT "%MAVEN_PROJECTBASEDIR:~-1%"=="\" SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR%\

SET WRAPPER_PROPERTIES=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties

@REM Read distributionUrl from wrapper properties
FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%WRAPPER_PROPERTIES%") DO (
    IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
)

@REM Derive Maven version and local storage path
SET MAVEN_USER_HOME=%USERPROFILE%\.m2\wrapper
IF NOT EXIST "%MAVEN_USER_HOME%" MKDIR "%MAVEN_USER_HOME%"

@REM Extract maven version from URL (e.g. apache-maven-3.9.6)
FOR %%F IN ("%DISTRIBUTION_URL%") DO SET MAVEN_DIST_NAME=%%~nF
SET MAVEN_HOME=%MAVEN_USER_HOME%\dists\%MAVEN_DIST_NAME%

IF EXIST "%MAVEN_HOME%\bin\mvn.cmd" GOTO RunMvn
IF EXIST "%MAVEN_HOME%\bin\mvn.bat" GOTO RunMvn

@REM Maven not cached — try to download it
echo.
echo Downloading Maven from: %DISTRIBUTION_URL%
echo To: %MAVEN_HOME%
echo.

IF NOT EXIST "%MAVEN_USER_HOME%\dists" MKDIR "%MAVEN_USER_HOME%\dists"

SET MAVEN_ZIP=%MAVEN_USER_HOME%\dists\%MAVEN_DIST_NAME%.zip

powershell -NoProfile -Command ^
  "[Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; ^
   (New-Object System.Net.WebClient).DownloadFile('%DISTRIBUTION_URL%', '%MAVEN_ZIP%')"

IF %ERRORLEVEL% NEQ 0 (
  echo.
  echo [ERROR] Download failed. On an offline machine, place the Maven zip at:
  echo   %MAVEN_ZIP%
  echo Or install Maven globally and ensure 'mvn' is on PATH.
  GOTO error
)

powershell -NoProfile -Command ^
  "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_USER_HOME%\dists' -Force"

DEL "%MAVEN_ZIP%"

@REM The zip extracts to a subfolder — find it
FOR /D %%D IN ("%MAVEN_USER_HOME%\dists\apache-maven-*") DO SET MAVEN_HOME=%%D

:RunMvn
IF NOT "%JAVA_HOME%" == "" (
  SET JAVA_CMD=%JAVA_HOME%\bin\java.exe
) ELSE (
  SET JAVA_CMD=java
)

@REM Build Maven command
SET MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd
IF NOT EXIST "%MVN_CMD%" SET MVN_CMD=%MAVEN_HOME%\bin\mvn.bat
IF NOT EXIST "%MVN_CMD%" (
  echo [ERROR] Cannot find mvn.cmd or mvn.bat in %MAVEN_HOME%\bin\
  GOTO error
)

@REM Execute Maven
"%MVN_CMD%" %MAVEN_OPTS% %*
IF %ERRORLEVEL% NEQ 0 GOTO error
GOTO end

:error
IF "%OS%"=="Windows_NT" @ENDLOCAL
EXIT /B 1

:end
IF "%MAVEN_BATCH_PAUSE%"=="on" PAUSE
IF "%OS%"=="Windows_NT" @ENDLOCAL
EXIT /B 0
