# 员工管理系统与薪资计算

基于 Maven + Java 11 + JUnit 5 构建的员工管理系统，模拟企业中部门管理、员工分类与薪资自动计算的完整流程。

## 📋 项目简介

本系统围绕"公司—部门—员工"三层结构，实现了以下核心能力：

- **部门管理**：创建部门、设置部门经理、维护部门员工名单
- **员工分类**：支持全职员工、兼职员工、经理三种类型
- **薪资计算**：每种员工类型有独立的薪资计算公式，通过多态自动分发
- **经理团队奖金**：经理薪资包含团队管理奖金（所辖全职员工个人奖金之和的 10%）
- **系统查询**：按部门编号查看员工、按编号/姓名搜索员工
- **薪资汇总**：一键计算公司总薪资开销

## 🛠 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 11 | 核心开发语言，使用 Java 8+ 新特性 |
| Maven | 3.x | 项目构建与依赖管理 |
| JUnit 5 | 5.10.2 | 单元测试框架 |
| 抽象类 / 接口 | — | 面向对象设计（继承 + 多态） |
| Stream API | — | 集合操作与薪资汇总 |
| Optional | — | 安全地处理查找结果 |

## 📂 项目结构

```
Employee_Management_System_and_Salary_Calculation/
├── pom.xml                                          # Maven 配置文件
├── README.md                                        # 项目说明文档
├── docs/
│   └── P2.2.2.md                                    # 实验需求文档
└── src/
    ├── main/java/com/ems/
    │   ├── BonusCalculable.java         # 奖金计算接口
    │   ├── Department.java              # 部门类
    │   ├── Employee.java                # 抽象员工基类
    │   ├── EmployeeManagementSystem.java # 系统管理类
    │   ├── FullTimeEmployee.java        # 全职员工类
    │   ├── Manager.java                 # 经理类（继承全职 + 实现奖金接口）
    │   ├── PartTimeEmployee.java        # 兼职员工类
    │   └── Main.java                    # 主程序入口
    └── test/java/com/ems/
        └── EmployeeManagementSystemTest.java  # JUnit 5 测试套件（34 个用例）
```

## 🚀 快速开始

### 环境要求

- JDK 11 或更高版本
- Maven 3.6+

### 构建项目

```bash
# 克隆仓库后，进入项目目录
cd Employee_Management_System_and_Salary_Calculation

# 编译项目
mvn compile

# 清理并重新编译
mvn clean compile
```

### 运行演示程序

```bash
mvn compile -q && java -cp target/classes com.ems.Main
```

输出示例：

```
╔══════════════════════════════════════════════════════╗
║      员工管理系统与薪资计算  —  功能演示             ║
╚══════════════════════════════════════════════════════╝

【第一步】环境初始化
✅ 部门创建成功：Department{deptId='D01', deptName='研发部', ...}
✅ 系统初始化完成。

【第二步】人员录入
✅ 已录入 4 名员工（2 名全职、1 名兼职、1 名经理）。

【功能1】按部门编号打印员工信息（D01）
========== 部门 [D01] 员工列表 ==========
员工编号：E001
姓名：张三
所属部门：研发部（D01）
员工类型：全职员工
基本工资：8000.0
个人奖金：2000.0
薪资合计：10000.0
──────────────────────────────
...
==========================================
该部门共 4 名员工。

【功能2a】按编号查找员工（E003 — 兼职员工王五）
✅ 查找成功，员工信息如下：...

【功能2b】按姓名查找员工（赵六 — 经理）
✅ 查找成功，员工信息如下：...

【功能3】公司总薪资开销
── 各员工薪资明细 ──
张三（全职）：10000.0 元
李四（全职）：9000.0 元
王五（兼职）：4000.0 元
赵六（经理）：17350.0 元
...
✅ 公司总薪资开销（系统计算）：40350.0 元
✅ 手动核算验证：✓ 一致
```

### 运行测试

```bash
# 运行全部测试
mvn test

# 运行指定测试类
mvn test -Dtest=EmployeeManagementSystemTest

# 查看测试报告
# 报告输出在 target/surefire-reports/ 目录下
```

预期输出：

```
[INFO] Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## 🏗 类设计与核心逻辑

### UML 类图

```
┌─────────────────┐       ┌──────────────────────┐
│   Department    │       │   BonusCalculable    │
│─────────────────│       │──────────────────────│
│ - deptId        │       │ + calculateBonus()   │
│ - deptName      │       └──────────────────────┘
│ - manager       │               △
│ - employeeList  │               │ 实现
└────────┬────────┘               │
         │                        │
         │ contains               │
┌────────┴────────┐    ┌──────────┴──────────┐
│    Employee     │    │      Manager        │
│ (abstract)      │◄───│ (extends            │
│─────────────────│    │  FullTimeEmployee)  │
│ - empId         │    │─────────────────────│
│ - empName       │    │ - teamManagementBonus│
│ - department    │    │ + calculateBonus()  │
│ + calcSalary()* │    │ + calculateSalary() │
│ + displayInfo() │    └─────────────────────┘
└────────┬────────┘
         △
         │ extends
    ┌────┴────────────┐
    │                 │
┌───┴─────┐   ┌──────┴──────────┐
│FullTime │   │ PartTimeEmployee│
│Employee │   │──────────────────│
│─────────│   │ - workHours      │
│-baseSal │   │ - hourlyWage     │
│-bonus   │   │ + calcSalary()   │
│+calcSal │   │ + displayInfo()  │
└─────────┘   └──────────────────┘
```

### 薪资计算公式

| 员工类型 | 计算公式 |
|---------|---------|
| 全职员工 | 基本工资 + 个人奖金 |
| 兼职员工 | 工作时间 × 每小时工资 |
| 经理 | 基本工资 + 个人奖金 + (所辖全职员工个人奖金之和 × 10%) |

### 核心特性说明

#### 1. 多态（Polymorphism）

`EmployeeManagementSystem.calculateTotalSalary()` 通过统一调用 `Employee::calculateSalary` 完成薪资汇总，JVM 在运行时根据对象的实际类型自动调用对应的子类实现，无需手动判断类型。

```java
public double calculateTotalSalary() {
    return allEmployees.stream()
            .mapToDouble(Employee::calculateSalary)  // 多态调用
            .sum();
}
```

#### 2. 接口与抽象类的选择

- `Employee` 设计为**抽象类**：所有员工共享 `empId`、`empName`、`department` 等共同属性和 `displayInfo()` 行为，适合抽取到父类
- `BonusCalculable` 设计为**接口**："可计算奖金"是一种能力，不是一种"是员工"的关系。并非所有员工都能计算奖金（如兼职员工），但经理和全职员工可以

#### 3. 安全向下转型（instanceof）

经理计算团队奖金时，使用 `instanceof` 进行安全检查，避免对兼职员工进行不安全的向下转型：

```java
double totalPersonalBonus = getDepartment().getEmployeeList().stream()
        .filter(e -> e != this)                        // 排除经理自己
        .filter(e -> e instanceof FullTimeEmployee)    // instanceof 安全检查
        .mapToDouble(e -> ((FullTimeEmployee) e).getPersonalBonus()) // 安全向下转型
        .sum();
```

#### 4. Java 8+ Stream API 应用

- **薪资汇总**：`mapToDouble(Employee::calculateSalary).sum()`
- **员工搜索**：`stream().filter(...).findFirst()` 返回 `Optional<Employee>`
- **统计查询**：`stream().filter(e -> e instanceof FullTimeEmployee).count()`
- **数据转换**：`stream().map(Employee::getEmpName).collect(Collectors.toList())`

## 🧪 测试覆盖

测试类 `EmployeeManagementSystemTest` 共 **34 个测试用例**，覆盖以下场景：

| 测试类别 | 用例数 | 说明 |
|---------|--------|------|
| 部门类测试 | 5 | 创建、添加员工、不可修改视图、Stream 统计、经理设置 |
| 全职员工测试 | 3 | 薪资计算、getter/setter、displayInfo |
| 兼职员工测试 | 3 | 薪资计算、零工时、setter |
| 经理类测试 | 5 | 薪资含团队奖金、团队奖金计算、排除兼职、无部门安全、displayInfo |
| 系统管理测试 | 4 | 添加员工、去重、null 校验、总薪资 |
| 部门查询测试 | 3 | 按编号查询、不存在部门、null 参数 |
| 员工搜索测试 | 5 | 按编号、按姓名、不存在、空系统、不区分大小写 |
| Stream 统计测试 | 3 | 全职人数、兼职人数、姓名列表 |
| 综合场景测试 | 1 | 完整业务流程端到端验证 |
| 边界情况 | 2 | 空系统薪资（0）、经理无部门 |

## 📝 实验要求对照

| 需求 | 实现位置 | 状态 |
|------|---------|------|
| Department 类（属性 + addEmployee） | `Department.java` | ✅ |
| Employee 抽象类（抽象方法 + 具体方法） | `Employee.java` | ✅ |
| FullTimeEmployee 继承 Employee | `FullTimeEmployee.java` | ✅ |
| PartTimeEmployee 继承 Employee | `PartTimeEmployee.java` | ✅ |
| BonusCalculable 接口 | `BonusCalculable.java` | ✅ |
| Manager 继承 FullTimeEmployee + 实现 BonusCalculable | `Manager.java` | ✅ |
| Manager 团队管理奖金（10%）| `Manager.calculateBonus()` | ✅ |
| EmployeeManagementSystem（4个核心方法） | `EmployeeManagementSystem.java` | ✅ |
| Main 模拟测试流程 | `Main.java` | ✅ |
| Maven 构建 | `pom.xml` | ✅ |
| JUnit 测试用例 | `EmployeeManagementSystemTest.java` | ✅ |
| Java 8+ 新特性 | Stream API、方法引用、Optional 等 | ✅ |


