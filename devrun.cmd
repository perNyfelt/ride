::@echo off
set DIR=%~dp0%
cd %DIR%

set PROPERTY_FILE=version.properties

FOR /F "tokens=1,2 delims==" %%G IN (%PROPERTY_FILE%) DO (set %%G=%%H)

set JAR_NAME=%jar.name%

set RELEASE_TAG=%release.tag%

set TARGET=%DIR%\target\%jar.name%

call mvn clean install

cd src/bin

call mvn initialize -Dride.jar=%TARGET% -Drelease.tag=%RELEASE_TAG%

call mvn "exec:java" -Dride.jar=%TARGET% -Drelease.tag=%RELEASE_TAG%