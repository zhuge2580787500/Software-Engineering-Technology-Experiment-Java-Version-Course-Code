# 日志关键词分析工具

一个 Java 17 命令行工具，用于从日志文件中提取包含指定关键词的行并输出统计信息。

## 功能特性

- 基于流式读取大文件（BufferedReader，UTF-8 编码）
- 支持区分大小写或不区分大小写的关键词匹配
- 支持多个关键词，逗号分隔，OR 逻辑
- 结果输出到控制台或文件
- 仅计数模式（只输出匹配行数）
- 清晰的错误提示，每种错误对应不同的退出码

## 构建

```bash
mvn clean package
```

构建成功后，可执行 JAR 文件位于 `target/log-analyzer-1.0.0.jar`。

## 运行方式

```bash
java -jar target/log-analyzer-1.0.0.jar [参数]
```

## 参数说明

| 参数 | 全称 | 是否必需 | 默认值 | 说明 |
|------|------|----------|--------|------|
| `-f` | `--file` | 是 | — | 日志文件路径 |
| `-k` | `--keyword` | 是 | — | 搜索关键词，多个用逗号分隔 |
| `-i` | `--ignore-case` | 否 | `false` | 匹配时忽略大小写 |
| `-c` | `--count` | 否 | `false` | 只输出匹配行数，不输出具体行 |
| `-o` | `--output` | 否 | 标准输出 | 将结果输出到指定文件 |
| `-h` | `--help` | 否 | — | 显示帮助信息 |

支持 `-f=value` 和 `-f value` 两种写法。

## 使用示例

### 1. 搜索 ERROR 行（区分大小写）

```bash
java -jar target/log-analyzer-1.0.0.jar -f app.log -k ERROR
```

输出：
```
1: 2024-01-01 00:00:01 ERROR Failed to connect
3: 2024-01-01 00:00:05 ERROR Timeout waiting for response
```

### 2. 多关键词 + 忽略大小写

```bash
java -jar target/log-analyzer-1.0.0.jar -f app.log -k "error,warn" -i
```

### 3. 仅输出匹配行数

```bash
java -jar target/log-analyzer-1.0.0.jar -f app.log -k ERROR -c
```

输出：
```
2
```

### 4. 结果写入文件

```bash
java -jar target/log-analyzer-1.0.0.jar -f app.log -k ERROR -o result.txt
```

## 退出码说明

| 退出码 | 含义 |
|--------|------|
| 0 | 成功 |
| 1 | 参数解析错误（未知参数、缺少必需参数） |
| 2 | 日志文件不存在 |
| 3 | 文件读取失败（权限不足、IO 异常） |
| 4 | 输出文件写入失败 |
| 5 | 未预期的异常 |

## 项目结构

```
homework1.4/
├── pom.xml
├── README.md
├── docs/
│   ├── design.md          # 设计文档
│   ├── HOW_TO_RUN.md      # 运行教程
│   └── TEST_CASES.md      # 测试用例
├── src/
│   ├── main/java/com/loganalyzer/
│   │   ├── Main.java          # 入口类，负责参数解析
│   │   ├── LogAnalyzer.java   # 核心分析类，负责读取、匹配、输出
│   │   └── CliOptions.java    # 参数解析结果封装类（不可变对象）
│   └── test/
│       ├── java/com/loganalyzer/
│       │   ├── CliOptionsTest.java
│       │   ├── LogAnalyzerTest.java
│       │   └── LogAnalyzerTestHelper.java
│       └── resources/
│           ├── sample.log
│           ├── integration-test.bat   # Windows 集成测试
│           └── integration-test.sh    # Linux/macOS 集成测试
```

## 运行测试

```bash
mvn test
```

## 运行集成测试

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

## 已知限制

- 关键词匹配为子串匹配（非正则表达式或单词匹配）。
- 输出文件若已存在则会被覆盖。
- 日志文件需使用 UTF-8 编码。
