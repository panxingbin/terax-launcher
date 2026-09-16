@echo off
echo Closing existing Terax...
taskkill /IM terax.exe /F 2>nul
timeout /t 2 /nobreak >nul

echo.
echo Test 1: terax E:\workspace\trac\terax-launcher
start "" "C:\Users\S6114648\AppData\Local\Terax\terax.exe" "E:\workspace\trac\terax-launcher"
timeout /t 3 /nobreak >nul
tasklist | findstr terax
echo.

echo Closing...
taskkill /IM terax.exe /F 2>nul
timeout /t 2 /nobreak >nul

echo.
echo Test 2: terax open E:\workspace\trac\terax-launcher
terax open "E:\workspace\trac\terax-launcher"
timeout /t 3 /nobreak >nul
tasklist | findstr terax
echo.

echo Closing...
taskkill /IM terax.exe /F 2>nul
timeout /t 2 /nobreak >nul

echo.
echo Test 3: terax (no args)
terax
timeout /t 3 /nobreak >nul
tasklist | findstr terax
echo.

pause
