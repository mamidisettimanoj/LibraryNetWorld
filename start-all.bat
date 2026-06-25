@echo off
start "LibraryNet Backend" cmd /k "%~dp0start-backend.bat"
start "LibraryNet Frontend" cmd /k "%~dp0start-frontend.bat"
echo Start PostgreSQL first, then wait for both terminals.
