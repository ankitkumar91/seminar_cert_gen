@echo off
setlocal EnableExtensions

rem Adds this Maven web project to Ankit's Eclipse workspace as seminar-certificates.
rem Default workspace: C:\Users\Ankit\eclipse-workspace

set "WORKSPACE=%~1"
if "%WORKSPACE%"=="" set "WORKSPACE=C:\Users\Ankit\eclipse-workspace"

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "REPO=%%~fI"
set "DEST=%WORKSPACE%\seminar-certificates"

echo Eclipse workspace: %WORKSPACE%
echo Project source:    %REPO%
echo Project in workspace: %DEST%

if not exist "%WORKSPACE%" (
  mkdir "%WORKSPACE%"
  if errorlevel 1 (
    echo Could not create the workspace folder. Create it in Eclipse once, then re-run this script.
    exit /b 1
  )
)

if exist "%DEST%\.project" (
  echo The project is already present at:
  echo   %DEST%
  goto :next
)

rem Prefer a directory junction so git commits stay in the clone.
mklink /J "%DEST%" "%REPO%" >nul 2>&1
if exist "%DEST%\.project" (
  echo Linked %DEST% -^> %REPO%
  goto :next
)

echo Junction was not created ^(admin rights may be required^). Copying files instead.
mkdir "%DEST%" 2>nul
robocopy "%REPO%" "%DEST%" /E /XD .git target tools data eclipse-workspace .metadata /NFL /NDL /NJH /NJS /nc /ns /np
if errorlevel 8 (
  echo Copy failed.
  exit /b 1
)
echo Copied the project into the workspace.

:next
echo.
echo Next in Eclipse:
echo   1. File -^> Switch Workspace -^> Other... -^> %WORKSPACE%
echo   2. File -^> Import -^> Maven -^> Existing Maven Projects
echo   3. Root Directory: %DEST%
echo   4. Finish, then Run As -^> Run on Server ^(Tomcat 10.1^)
echo.
echo App URL: http://localhost:8080/seminar-certificates/
endlocal
