@echo off
setlocal

:: ── Locate JAVA_HOME ──────────────────────────────────────────────────────────
if "%JAVA_HOME%"=="" (
    set "JAVACMD=java"
) else (
    set "JAVACMD=%JAVA_HOME%\bin\java"
)

:: ── Locate wrapper JAR ────────────────────────────────────────────────────────
set "WRAPPER_JAR=%~dp0.mvn\wrapper\maven-wrapper.jar"
set "WRAPPER_PROPS=%~dp0.mvn\wrapper\maven-wrapper.properties"

if not exist "%WRAPPER_JAR%" (
    echo Downloading Maven Wrapper JAR...
    powershell -NoLogo -NoProfile -Command ^
        "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;" ^
        "$url = 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar';" ^
        "Invoke-WebRequest -Uri $url -OutFile '%WRAPPER_JAR%' -UseBasicParsing"
    if errorlevel 1 (
        echo ERROR: Failed to download maven-wrapper.jar
        exit /b 1
    )
)

:: ── Run Maven Wrapper ─────────────────────────────────────────────────────────
"%JAVACMD%" ^
    -classpath "%WRAPPER_JAR%" ^
    "-Dmaven.multiModuleProjectDirectory=%~dp0" ^
    "-Dmaven.wrapper.propertiesFile=%WRAPPER_PROPS%" ^
    org.apache.maven.wrapper.MavenWrapperMain %*

endlocal
