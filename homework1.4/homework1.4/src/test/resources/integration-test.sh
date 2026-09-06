#!/usr/bin/env bash
set -euo pipefail

# ============================================================
# Integration test script for Log Analyzer
# Run from project root directory:
#   chmod +x src/test/resources/integration-test.sh
#   ./src/test/resources/integration-test.sh
# ============================================================

JAR="target/log-analyzer-1.0.0.jar"
LOG="src/test/resources/sample.log"
OUTPUT="target/test-output.txt"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# ---- Check JAR exists ----
if [ ! -f "$JAR" ]; then
    echo -e "${RED}[FAIL]${NC} JAR not found. Run 'mvn clean package' first."
    exit 1
fi
echo -e "${GREEN}[PASS]${NC} JAR found: $JAR"
echo

# ---- Test 1: Basic keyword search ----
echo "===== Test 1: Basic keyword search ====="
echo "Command: java -jar $JAR -f $LOG -k ERROR"
java -jar "$JAR" -f "$LOG" -k ERROR
echo

# ---- Test 2: Multiple keywords (OR logic) ----
echo "===== Test 2: Multiple keywords ====="
echo "Command: java -jar $JAR -f $LOG -k ERROR,WARN"
java -jar "$JAR" -f "$LOG" -k ERROR,WARN
echo

# ---- Test 3: Case-insensitive matching ----
echo "===== Test 3: Case-insensitive matching ====="
echo "Command: java -jar $JAR -f $LOG -k error -i"
java -jar "$JAR" -f "$LOG" -k error -i
echo

# ---- Test 4: Count only ----
echo "===== Test 4: Count only ====="
echo "Command: java -jar $JAR -f $LOG -k ERROR -c"
java -jar "$JAR" -f "$LOG" -k ERROR -c
echo

# ---- Test 5: Output to file ----
echo "===== Test 5: Output to file ====="
echo "Command: java -jar $JAR -f $LOG -k ERROR -o $OUTPUT"
java -jar "$JAR" -f "$LOG" -k ERROR -o "$OUTPUT"
if [ -f "$OUTPUT" ]; then
    echo -e "${GREEN}[PASS]${NC} Output file created. Contents:"
    cat "$OUTPUT"
else
    echo -e "${RED}[FAIL]${NC} Output file not created."
fi
echo

# ---- Test 6: Help ----
echo "===== Test 6: Help ====="
echo "Command: java -jar $JAR -h"
java -jar "$JAR" -h
echo

# ---- Test 7: Error — unknown option ----
echo "===== Test 7: Unknown option ====="
echo "Command: java -jar $JAR -f $LOG -k ERROR --unknown"
if java -jar "$JAR" -f "$LOG" -k ERROR --unknown 2>&1 | grep -q "Unknown option"; then
    echo -e "${GREEN}[PASS]${NC} Correctly rejected unknown option"
else
    echo -e "${YELLOW}[WARN]${NC} Expected 'Unknown option' in error output"
fi
echo

# ---- Test 8: Error — missing required option ----
echo "===== Test 8: Missing required option ====="
echo "Command: java -jar $JAR -k ERROR"
if java -jar "$JAR" -k ERROR 2>&1 | grep -qi "missing"; then
    echo -e "${GREEN}[PASS]${NC} Correctly rejected missing --file"
else
    echo -e "${YELLOW}[WARN]${NC} Expected 'Missing' in error output"
fi
echo

echo "===== All integration tests completed ====="
