@echo off
chcp 65001 >nul
cd /d "%~dp0"

:: T??? ?????ng t??m l???nh mvn (??u ti??n PATH h??? th???ng, n???u kh??ng c?? s??? l???y Maven c?? s???n c???a IntelliJ)
set "MVN_CMD=mvn"
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    if exist "D:\programFileD\IntelliJ IDEA 2026.2.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
        set "MVN_CMD=D:\programFileD\IntelliJ IDEA 2026.2.2\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
    )
)

echo ===== Running Selenium tests (headless) =====
call "%MVN_CMD%" clean test -Dheadless=true -Dmaven.test.failure.ignore=true %*

echo ===== Generating HTML report =====
call "%MVN_CMD%" surefire-report:report-only -DlinkXRef=false -q

if exist target\reports\surefire.html (
    start "" target\reports\surefire.html
) else if exist target\site\surefire-report.html (
    start "" target\site\surefire-report.html
)
if exist target\screenshots (
    echo Screenshots of failed tests: target\screenshots
)
