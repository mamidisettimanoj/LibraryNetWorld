@echo off
cd /d "%~dp0backend"
call mvnw.cmd clean test package
if errorlevel 1 exit /b 1
cd /d "%~dp0frontend"
call npm install
call npm run lint
if errorlevel 1 exit /b 1
call npm run build
