# 测试用例

## 一、单元测试（JUnit 5）

测试文件位置：`src\test\java\com\loganalyzer\`

### 1. CliOptionsTest（4 个测试）

| # | 测试方法 | 测试内容 | 预期结果 |
|---|----------|----------|----------|
| 1 | `builder_setsAllFields` | 设置所有字段后读取 | 值完全一致 |
| 2 | `builder_defaultValues` | 不设置任何字段 | 全部为默认值（null/false/empty） |
| 3 | `builder_keywordsAreUnmodifiable` | 获取 keywords 列表后尝试修改 | 抛出 `UnsupportedOperationException` |
| 4 | `builder_keywordsSplitByComma` | 输入 `"ERROR, WARN, INFO"` | 拆分为 `["ERROR", "WARN", "INFO"]` |

### 2. LogAnalyzerTest（7 个测试）

#### 关键词匹配逻辑

| # | 测试方法 | 输入 | 预期结果 |
|---|----------|------|----------|
| 5 | `match_caseSensitive_found` | 行含 `ERROR`，关键词 `ERROR`，大小写敏感 | 匹配成功 |
| 6 | `match_caseSensitive_notFound` | 行含 `error`，关键词 `ERROR`，大小写敏感 | 不匹配 |
| 7 | `match_caseInsensitive_found` | 行含 `error`，关键词 `ERROR`，忽略大小写 | 匹配成功 |
| 8 | `match_multipleKeywords_anyMatch` | 关键词 `ERROR,WARN`，行分别含 `ERROR`/`WARN`/`INFO` | 前两个匹配，第三个不匹配 |
| 9 | `match_substring` | 关键词 `NullPointer`/`time` | 子串匹配成功 |
| 10 | `match_emptyKeywords_noMatch` | 关键词列表为空 | 不匹配 |

#### 文件分析

| # | 测试方法 | 测试内容 | 预期结果 |
|---|----------|----------|----------|
| 11 | `analyze_largeFile_streaming` | 生成 10,000 行文件，搜索 `ERROR` | 退出码 0，不崩溃 |
| 12 | `analyze_fileNotFound` | 指定不存在的文件 | 退出码 2 |
| 13 | `analyze_helpFlag` | 传入 `-h` | 退出码 0，显示帮助 |
| 14 | `analyze_countOnly` | 5 行文件中搜索 `ERROR` | 匹配行数为 2 |

### 运行单元测试

```powershell
mvn test
```

预期输出：

```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 二、集成测试（命令行实际运行）

### 前置条件

```powershell
# 1. 进入项目目录
cd C:\Users\lenovo\Desktop\软件工程技术实验\homework1.4\homework1.4

# 2. 确保已构建
mvn clean package
```

### 测试用例

#### TC-01：基本关键词搜索

```powershell
java -jar target\log-analyzer-1.0.0.jar -f src\test\resources\sample.log -k ERROR
```

- **预期输出**：3 行，每行格式为 `行号: 原始日志内容`
- **验证点**：行号正确（5, 6, 12），内容不变

#### TC-02：多关键词（OR 逻辑）

```powershell
java -jar target\log-analyzer-1.0.0.jar -f src\test\resources\sample.log -k ERROR,WARN
```

- **预期输出**：6 行（3 行 ERROR + 3 行 WARN）
- **验证点**：只要包含任意一个关键词就命中

#### TC-03：忽略大小写

```powershell
java -jar target\log-analyzer-1.0.0.jar -f src\test\resources\sample.log -k error -i
```

- **预期输出**：与 TC-01 相同（3 行）
- **验证点**：小写 `error` 能匹配到 `ERROR`

#### TC-04：仅输出计数

```powershell
java -jar target\log-analyzer-1.0.0.jar -f src\test\resources\sample.log -k ERROR -c
```

- **预期输出**：单个整数 `3`
- **验证点**：不输出具体行，只输出数字

#### TC-05：输出到文件

```powershell
java -jar target\log-analyzer-1.0.0.jar -f src\test\resources\sample.log -k WARN -o target\warn-result.txt
type target\warn-result.txt
```

- **预期输出**：3 行 WARN，写入文件
- **验证点**：文件编码 UTF-8，内容与 TC-02 中 WARN 部分一致

#### TC-06：显示帮助

```powershell
java -jar target\log-analyzer-1.0.0.jar -h
```

- **预期输出**：用法说明、参数列表、示例命令
- **验证点**：包含 `-f`、`-k`、`-i`、`-c`、`-o`、`-h` 六个参数说明

#### TC-07：未知参数（错误处理）

```powershell
java -jar target\log-analyzer-1.0.0.jar -f src\test\resources\sample.log -k ERROR --unknown
```

- **预期 stderr**：`Error: Unknown option '--unknown'`
- **预期退出码**：1

#### TC-08：缺少必需参数（错误处理）

```powershell
java -jar target\log-analyzer-1.0.0.jar -k ERROR
```

- **预期 stderr**：`Error: Missing required option --file`
- **预期退出码**：1

#### TC-09：文件不存在（错误处理）

```powershell
java -jar target\log-analyzer-1.0.0.jar -f C:\nonexistent\file.log -k ERROR
```

- **预期 stderr**：`Error: Log file not found: C:\nonexistent\file.log`
- **预期退出码**：2

---

## 三、快速验证脚本

将以下命令粘贴到 PowerShell 中即可一键验证：

```powershell
cd C:\Users\lenovo\Desktop\软件工程技术实验\homework1.4\homework1.4
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:Path = 'C:\Program Files\Java\jdk-17\bin;' + $env:Path
$jar = 'target\log-analyzer-1.0.0.jar'
$log = 'src\test\resources\sample.log'

Write-Host "=== 1. 单元测试 ===" -ForegroundColor Cyan
mvn test -q

Write-Host "`n=== 2. 基本搜索 ===" -ForegroundColor Cyan
java -jar $jar -f $log -k ERROR

Write-Host "`n=== 3. 多关键词 + 计数 ===" -ForegroundColor Cyan
java -jar $jar -f $log -k ERROR,WARN -c

Write-Host "`n=== 4. 忽略大小写 ===" -ForegroundColor Cyan
java -jar $jar -f $log -k error -i

Write-Host "`n=== 5. 输出到文件 ===" -ForegroundColor Cyan
java -jar $jar -f $log -k WARN -o target\test-output.txt
Get-Content target\test-output.txt

Write-Host "`n=== 6. 错误处理 ===" -ForegroundColor Cyan
java -jar $jar -k ERROR 2>&1
java -jar $jar -f $log --badopt 2>&1

Write-Host "`n=== 全部验证完成 ===" -ForegroundColor Green
```
