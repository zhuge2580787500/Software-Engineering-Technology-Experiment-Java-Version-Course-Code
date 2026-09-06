# 运行教程

## 环境要求

- Java 17（已安装在 `C:\Program Files\Java\jdk-17`）
- Maven 3.9.5（已安装在用户目录 `.m2` 下）
- Windows 10/11 PowerShell

## 第一步：配置环境变量（只需做一次）

打开 **PowerShell**（管理员或普通用户均可），粘贴以下命令：

```powershell
# 设置 JAVA_HOME
[Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Java\jdk-17', 'User')

# 将 Java 和 Maven 加入 PATH
$current = [Environment]::GetEnvironmentVariable('Path', 'User')
$java = 'C:\Program Files\Java\jdk-17\bin'
$maven = 'C:\Users\lenovo\.m2\wrapper\dists\apache-maven-3.9.5-bin\2adeog8mj13csp1uusqnc1f2mo\apache-maven-3.9.5\bin'
foreach ($item in @($java, $maven)) {
    if ($current -split ';' -notcontains $item) {
        $current = $current + ';' + $item
    }
}
[Environment]::SetEnvironmentVariable('Path', $current, 'User')
```

然后 **关闭 PowerShell，重新打开** 让配置生效。

验证配置：

```powershell
java -version
mvn --version
```

两个命令都应正常输出版本号。

## 第二步：进入项目目录

```powershell
cd C:\Users\lenovo\Desktop\软件工程技术实验\homework1.4\homework1.4
```

## 第三步：构建项目

```powershell
mvn clean package
```

构建成功后，可执行 JAR 文件在：

```
target\log-analyzer-1.0.0.jar
```

## 第四步：运行

### 基本语法

```powershell
java -jar target\log-analyzer-1.0.0.jar [参数]
```

### 参数一览

| 参数 | 全称 | 是否必须 | 说明 | 默认值 |
|------|------|----------|------|--------|
| `-f` | `--file` | 是 | 日志文件路径 | 无 |
| `-k` | `--keyword` | 是 | 关键词，多个用逗号分隔 | 无 |
| `-i` | `--ignore-case` | 否 | 忽略大小写 | false |
| `-c` | `--count` | 否 | 只输出匹配行数 | false |
| `-o` | `--output` | 否 | 结果输出到文件 | 标准输出 |
| `-h` | `--help` | 否 | 显示帮助信息 | - |

## 运行示例

### 示例 1：搜索 ERROR 行

```powershell
java -jar target\log-analyzer-1.0.0.jar -f src\test\resources\sample.log -k ERROR
```

输出：

```
5: 2024-01-01 10:00:04 ERROR Failed to connect to database: Connection refused
6: 2024-01-01 10:00:05 ERROR Retry attempt 1/3 failed for database connection
12: 2024-01-01 10:00:11 ERROR User authentication service threw an exception
```

### 示例 2：多关键词 + 仅计数

```powershell
java -jar target\log-analyzer-1.0.0.jar -f src\test\resources\sample.log -k ERROR,WARN -c
```

输出：

```
6
```

### 示例 3：忽略大小写

```powershell
java -jar target\log-analyzer-1.0.0.jar -f src\test\resources\sample.log -k error -i
```

使用小写 `error` 也能匹配到 `ERROR` 行。

### 示例 4：结果输出到文件

```powershell
java -jar target\log-analyzer-1.0.0.jar -f src\test\resources\sample.log -k WARN -o target\warn-result.txt
```

结果会写入 `target\warn-result.txt`，文件编码为 UTF-8。

### 示例 5：显示帮助

```powershell
java -jar target\log-analyzer-1.0.0.jar -h
```

## 退出码说明

| 退出码 | 含义 |
|--------|------|
| 0 | 成功 |
| 1 | 参数错误（未知参数、缺少必需参数） |
| 2 | 日志文件不存在 |
| 3 | 文件读取失败 |
| 4 | 输出文件写入失败 |
| 5 | 未预期异常 |
