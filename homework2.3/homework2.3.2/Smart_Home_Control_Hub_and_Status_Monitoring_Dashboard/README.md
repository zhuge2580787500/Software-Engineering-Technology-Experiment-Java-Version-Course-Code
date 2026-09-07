# 智能家居控制中枢与状态监控看板

> **考察设计模式：命令模式 (Command) + 观察者模式 (Observer)**

---

## 项目概述

本项目模拟了一款智能家居手机 App 控制端的核心功能：

- **控制中枢**：App 界面上有多个按钮，点击按钮向智能电器（电灯、空调）发送指令。
- **状态看板**：首页实时展示所有智能设备的最新状态，任何设备状态变化时自动更新。

项目严格遵循面向对象设计原则，核心架构采用 **命令模式** 与 **观察者模式** 解耦调用者与接收者，实现请求与执行的分离。

---

## 设计模式详解

### 1. 命令模式 (Command Pattern)

将"请求"封装为独立对象，使请求的发送者与执行者解耦。

```
Client → Invoker (SmartAppController) → Command → Receiver (Light / AirConditioner)
```

| 角色 | 类 | 职责 |
|------|----|------|
| Command（命令接口） | `Command` | 声明 `execute()` 执行方法 |
| ConcreteCommand（具体命令） | `LightOnCommand`、`LightOffCommand`、`ACTurnOnCommand`、`ACSetTemperatureCommand` | 持有 Receiver 引用，实现 `execute()` |
| Invoker（调用者） | `SmartAppController` | 拥有多个按钮插槽（slot），调用 `buttonWasPressed(slot)` 触发命令 |
| Receiver（接收者） | `Light`、`AirConditioner` | 执行实际业务逻辑（开/关、调温） |

**优势**：
- 请求与执行解耦：`SmartAppController` 不依赖具体设备类
- 支持命令排队、撤销/重做（可扩展）
- 新增命令无需修改调用者，符合开闭原则

### 2. 观察者模式 (Observer Pattern)

设备（被观察者）状态变化时，自动通知所有已注册的观察者（看板）。

```
Subject (DeviceSubject) ←→ Observer (DeviceObserver)
         ↑                           ↑
   Light / AirConditioner     HomeDashboard
```

| 角色 | 类 | 职责 |
|------|----|------|
| Subject（主题接口） | `DeviceSubject` | 声明 `registerObserver()`、`removeObserver()`、`notifyObservers()` |
| ConcreteSubject（具体主题） | `Light`、`AirConditioner` | 维护观察者列表，状态变化时调用 `stateChanged()` 触发通知 |
| Observer（观察者接口） | `DeviceObserver` | 声明 `update(String message)` 方法 |
| ConcreteObserver（具体观察者） | `HomeDashboard` | 实现 `update()`，在控制台打印状态更新 |

**优势**：
- 设备与看板解耦：设备不依赖具体看板实现
- 支持一对多依赖关系：一个设备可被多个观察者监听
- 新增观察者无需修改设备代码

---

## 项目结构

```
src/main/java/com/smarthome/
├── Main.java                              # 客户端测试入口
├── subject/
│   └── DeviceSubject.java                 # 被观察者接口（Subject）
├── observer/
│   ├── DeviceObserver.java                # 观察者接口（Observer）
│   └── HomeDashboard.java                 # 具体观察者：家庭状态看板
├── receiver/
│   ├── SmartDevice.java                   # 智能设备抽象基类
│   ├── Light.java                         # 具体接收者：电灯
│   └── AirConditioner.java                # 具体接收者：空调
└── command/
    ├── Command.java                       # 命令接口
    ├── LightOnCommand.java                # 具体命令：开灯
    ├── LightOffCommand.java               # 具体命令：关灯
    ├── ACTurnOnCommand.java               # 具体命令：开空调
    └── ACSetTemperatureCommand.java       # 具体命令：设置空调温度
```

---

## 核心类说明

### 智能设备（Receiver）

| 类 | 说明 |
|----|------|
| `SmartDevice` | 抽象基类，实现 `DeviceSubject` 接口，封装观察者列表维护与通知逻辑 |
| `Light` | 电灯，支持 `turnOn()` / `turnOff()` / `setBrightness(int)` |
| `AirConditioner` | 空调，支持 `turnOn()` / `turnOff()` / `setTemperature(int)` / `setMode(String)` |

### 命令（Command）

| 类 | 说明 |
|----|------|
| `Command` | 命令接口，定义 `execute()` |
| `LightOnCommand` | 调用 `Light.turnOn()` |
| `LightOffCommand` | 调用 `Light.turnOff()` |
| `ACTurnOnCommand` | 调用 `AirConditioner.turnOn()` |
| `ACSetTemperatureCommand` | 调用 `AirConditioner.setTemperature(int)` |

### 调用者（Invoker）

| 类 | 说明 |
|----|------|
| `SmartAppController` | App 控制器，7 个插槽，提供 `setCommand()` 和 `buttonWasPressed()` |

---

## 运行方式

### 编译并运行

```bash
# 使用 Maven
mvn compile exec:java -Dexec.mainClass="com.smarthome.Main"

# 或直接使用 javac + java
javac -d out -sourcepath src/main/java src/main/java/com/smarthome/*.java \
      src/main/java/com/smarthome/subject/*.java \
      src/main/java/com/smarthome/observer/*.java \
      src/main/java/com/smarthome/receiver/*.java \
      src/main/java/com/smarthome/command/*.java
java -cp out com.smarthome.Main
```

### 预期输出示例

```
==========================================================
      智能家居控制中枢与状态监控看板 - 测试开始
==========================================================

--- 第一步：实例化智能设备 ---

--- 第二步：创建家庭状态看板并注册设备 ---
【看板】已注册新设备：Light
【看板】已注册新设备：AirConditioner

========== 家庭状态看板 ==========
  客厅主灯 - 电源：关闭
  卧室床头灯 - 电源：关闭
  主卧空调 - 电源：关闭
====================================

--- 第三步：创建 App 控制器并绑定命令 ---
【控制器】已将命令绑定到客厅的插槽 1（客厅主灯开）
【控制器】已将命令绑定到客厅的插槽 2（客厅主灯关）

========== 客厅 - App 控制器按钮绑定情况 ==========
  插槽 1 [客厅主灯开]：已绑定 → LightOnCommand
  插槽 2 [客厅主灯关]：已绑定 → LightOffCommand
...

--- 第四步：模拟用户操作（按下 App 按钮）---

>>> 用户在客厅按下按钮：[客厅主灯开]（插槽 1）
[命令执行] 正在执行：打开客厅主灯……
【看板更新】客厅主灯已开启，亮度：80%

<<< 命令执行完毕

--- 第五步：查看最终看板状态 ---
========== 家庭状态看板 ==========
  客厅主灯 - 电源：开启，亮度：80%
  卧室床头灯 - 电源：开启，亮度：80%
  主卧空调 - 电源：开启，温度：24℃，模式：制冷
====================================
```

---

## 设计原则体现

| 原则 | 体现 |
|------|------|
| **单一职责** | 每个类只做一件事：设备控制、命令封装、通知分发等 |
| **开闭原则** | 新增命令/设备/观察者无需修改现有代码 |
| **里氏替换** | 所有具体命令可替换 `Command` 接口使用 |
| **接口隔离** | `DeviceSubject` 与 `DeviceObserver` 接口职责清晰 |
| **依赖倒置** | `SmartAppController` 依赖 `Command` 接口而非具体命令类 |

---

## 技术栈

- **语言**：Java 17+
- **设计模式**：Command Pattern + Observer Pattern
- **架构**：MVC 分层（Model：设备/命令，View：看板，Controller：App控制器）
