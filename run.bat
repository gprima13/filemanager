@echo off
setlocal

cd /d "%~dp0"

if not exist logs (
mkdir logs
)

echo Starting FileManager...

java -jar filemanager-1.0.jar ^
--spring.config.additional-location=file:./config/ ^
> logs/application.log 2>&1

pause
