# Log Analyzer

A Java 17 command-line tool for analyzing log files. Extracts lines containing
specified keywords and outputs matching statistics.

## Features

- Stream-based large file reading (BufferedReader, UTF-8)
- Case-sensitive or case-insensitive keyword matching
- Multiple keywords with OR logic (comma-separated)
- Output to stdout or to a file
- Count-only mode (outputs just the number of matching lines)
- Clear error messages with distinct exit codes

## Build

```bash
mvn clean package
```

The executable JAR will be generated at `target/log-analyzer-1.0.0.jar`.

## Usage

```bash
java -jar target/log-analyzer-1.0.0.jar [options]
```

## Parameters

| Short | Long               | Required | Default     | Description                                      |
|-------|--------------------|----------|-------------|--------------------------------------------------|
| `-f`  | `--file`           | Yes      | —           | Path to the log file                             |
| `-k`  | `--keyword`        | Yes      | —           | Keywords to search, comma-separated              |
| `-i`  | `--ignore-case`    | No       | `false`     | Case-insensitive matching                        |
| `-c`  | `--count`          | No       | `false`     | Output match count only                          |
| `-o`  | `--output`         | No       | stdout      | Write results to file                            |
| `-h`  | `--help`           | No       | —           | Display help message                             |

Both `-f=value` and `-f value` argument styles are supported.

## Examples

### 1. Search for ERROR lines (case-sensitive)

```bash
java -jar target/log-analyzer-1.0.0.jar -f app.log -k ERROR
```

Output:
```
1: 2024-01-01 00:00:01 ERROR Failed to connect
3: 2024-01-01 00:00:05 ERROR Timeout waiting for response
```

### 2. Multiple keywords with case-insensitive matching

```bash
java -jar target/log-analyzer-1.0.0.jar -f app.log -k "error,warn" -i
```

### 3. Count matches only

```bash
java -jar target/log-analyzer-1.0.0.jar -f app.log -k ERROR -c
```

Output:
```
2
```

### 4. Write results to a file

```bash
java -jar target/log-analyzer-1.0.0.jar -f app.log -k ERROR -o result.txt
```

## Exit Codes

| Code | Meaning                                      |
|------|----------------------------------------------|
| 0    | Success                                      |
| 1    | Argument parsing error                       |
| 2    | Log file not found                           |
| 3    | File read error (permissions, I/O)           |
| 4    | Output file write error                      |
| 5    | Unexpected error                             |

## Project Structure

```
homework1.4/
├── pom.xml
├── README.md
├── docs/
│   └── design.md
├── src/
│   ├── main/java/com/loganalyzer/
│   │   ├── Main.java          # Entry point, argument parser
│   │   ├── LogAnalyzer.java   # Core analysis engine
│   │   └── CliOptions.java    # Immutable options holder
│   └── test/
│       ├── java/com/loganalyzer/
│       │   ├── CliOptionsTest.java
│       │   ├── LogAnalyzerTest.java
│       │   └── LogAnalyzerTestHelper.java
│       └── resources/
│           ├── sample.log
│           ├── integration-test.bat   # Windows integration tests
│           └── integration-test.sh    # Linux/macOS integration tests
```

## Running Tests

```bash
mvn test
```

## Running Integration Tests

### Windows

```cmd
mvn clean package
src\test\resources\integration-test.bat
```

### Linux / macOS

```bash
mvn clean package
chmod +x src/test/resources/integration-test.sh
./src/test/resources/integration-test.sh
```

## Known Limitations

- Keywords are matched as substrings (not regular expressions or whole-word matches).
- Output file is overwritten if it already exists.
- Log file must be readable with UTF-8 encoding.
