@echo off
echo ============================================
echo   Hospital Management System - Builder
echo ============================================

REM Create output directory for .class files
if not exist "..\out" mkdir "..\out"
if not exist "..\out\data" mkdir "..\out\data"

echo Compiling all Java files...

javac -d ..\out models\*.java managers\*.java gui\*.java Main.java

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Check the errors above.
    pause
    exit /b 1
)

echo Compilation successful!
echo Starting application...
echo.

cd ..\out
java Main

pause
