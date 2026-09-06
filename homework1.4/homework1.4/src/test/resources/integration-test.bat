@echo off
chcp 65001 >nul
REM ============================================================
REM Integration test script for Log Analyzer (Windows)
REM Run from project root directory after "mvn clean package"
REM ============================================================

echo ========================================
echo  Log Analyzer Integration Tests
echo ========================================
echo.

set JAR=target\log-analyzer-1.0.0.jar
set LOG=src\test\resources\sample.log
set OUTPUT=target\test-output.txt

REM ---- Check JAR exists ----
if not exist "%JAR%" (
    echo [FAIL] JAR not found. Run "mvn clean package" first.
    exit /b 1
)

echo [PASS] JAR found: %JAR%
echo.

REM ---- Test 1: Basic keyword search ----
echo ---- Test 1: Basic keyword search ----
echo Command: java -jar %JAR% -f %LOG% -k ERROR
echo.
java -jar "%JAR%" -f "%LOG%" -k ERROR
echo.
echo Expected: lines containing "ERROR"
echo.

REM ---- Test 2: Multiple keywords (OR logic) ----
echo ---- Test 2: Multiple keywords ----
echo Command: java -jar %JAR% -f %LOG% -k ERROR,WARN
echo.
java -jar "%JAR%" -f "%LOG%" -k ERROR,WARN
echo.
echo Expected: lines containing "ERROR" or "WARN"
echo.

REM ---- Test 3: Case-insensitive matching ----
echo ---- Test 3: Case-insensitive matching ----
echo Command: java -jar %JAR% -f %LOG% -k error -i
echo.
java -jar "%JAR%" -f "%LOG%" -k error -i
echo.
echo Expected: same as Test 1 (matches "ERROR" via case-insensitive "error")
echo.

REM ---- Test 4: Count only ----
echo ---- Test 4: Count only ----
echo Command: java -jar %JAR% -f %LOG% -k ERROR -c
echo.
java -jar "%JAR%" -f "%LOG%" -k ERROR -c
echo.
echo Expected: a single integer (number of ERROR lines)
echo.

REM ---- Test 5: Output to file ----
echo ---- Test 5: Output to file ----
echo Command: java -jar %JAR% -f %LOG% -k ERROR -o %OUTPUT%
echo.
java -jar "%JAR%" -f "%LOG%" -k ERROR -o "%OUTPUT%"
echo.
if exist "%OUTPUT%" (
    echo [PASS] Output file created. Contents:
    type "%OUTPUT%"
) else (
    echo [FAIL] Output file not created.
)
echo.

REM ---- Test 6: Help ----
echo ---- Test 6: Help ----
echo Command: java -jar %JAR% -h
echo.
java -jar "%JAR%" -h
echo.

REM ---- Test 7: Error - unknown option ----
echo ---- Test 7: Unknown option ----
echo Command: java -jar %JAR% -f %LOG% -k ERROR --unknown
echo.
java -jar "%JAR%" -f "%LOG%" -k ERROR --unknown 2^>^&1
echo.
echo Expected: Error message about unknown option
echo.

REM ---- Test 8: Error - missing required option ----
echo ---- Test 8: Missing required option ----
echo Command: java -jar %JAR% -k ERROR
echo.
java -jar "%JAR%" -k ERROR 2^>^&1
echo.
echo Expected: Error about missing --file
echo.

echo ========================================
echo Integration tests completed.
echo ========================================
