@echo off
REM Hackathon Form - Quick Start Script for Windows

echo.
echo ========================================
echo  Hackathon Registration Form
echo  Quick Start Script
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH
    echo Please install Java 17 or higher
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed or not in PATH
    echo Please install Maven
    pause
    exit /b 1
)

REM Check if Node.js is installed
node --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js is not installed or not in PATH
    echo Please install Node.js
    pause
    exit /b 1
)

echo [OK] All prerequisites are installed!
echo.
echo Starting Backend and Frontend...
echo.

REM Start Backend
echo [INFO] Starting Spring Boot Backend on port 8080...
start "Hackathon Backend" cmd /k "cd backend && mvn spring-boot:run"

REM Wait for backend to start
timeout /t 5

REM Start Frontend
echo [INFO] Starting React Frontend on port 3000...
start "Hackathon Frontend" cmd /k "cd frontend && npm install && npm start"

echo.
echo ========================================
echo [SUCCESS] Application started!
echo ========================================
echo.
echo Backend URL: http://localhost:8080
echo Frontend URL: http://localhost:3000
echo.
echo The frontend should automatically open in your browser.
echo If it doesn't, open http://localhost:3000 manually.
echo.
echo Press Ctrl+C in each terminal to stop the servers.
echo ========================================
echo.

pause
