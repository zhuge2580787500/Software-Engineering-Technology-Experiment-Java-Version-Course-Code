# 电商促销计价与全局配置系统

**软件工程实验 — 第二讲实践任务 2.3.1**

---

## 项目概述

某电商平台需要一个订单计价模块，支持多种促销折扣规则，且未来可能不断增加新规则。同时需要一个全局唯一的配置管理器来读取平台的全局参数。本项目使用 **策略模式（Strategy）+ 单例模式（Singleton）** 构建，严格遵循面向对象设计原则，核心业务逻辑不依赖 if-else 或 switch-case 分支判断。

---

## 设计模式应用

### 1. 策略模式（Strategy）

解决多种折扣规则的动态切换与扩展问题。

| 角色 | 类 | 职责 |
|------|-----|------|
| **抽象策略** | `DiscountStrategy` | 定义 `calculate(double originalPrice)` 统一接口 |
| **具体策略** | `NoDiscountStrategy` | 无折扣，原价返回 |
| **具体策略** | `PercentageDiscountStrategy` | 按比例打折（如 8 折） |
| **具体策略** | `ThresholdDiscountStrategy` | 满减策略（如满 500 减 50） |
| **上下文** | `Order` | 持有策略引用，委托策略执行计价，结账时组合策略与税率 |

**设计优势**：新增折扣策略只需增加一个实现 `DiscountStrategy` 接口的类，无需修改 `Order` 或已有策略的任何代码（开闭原则）。运行时可通过 `setDiscountStrategy()` 动态切换策略。

### 2. 单例模式（Singleton）

全局配置管理器使用 **Bill Pugh 静态内部类**方式实现单例：

- **线程安全**：JVM 类加载机制保证初始化线程安全，无需 `synchronized`
- **懒加载**：只有首次调用 `getInstance()` 时才创建实例
- **序列化安全**：可天然抵御序列化/反序列化带来的多实例问题（通过 `readResolve` 可进一步增强）

---

## 项目结构

```
src/main/java/com/ecommerce/
├── DiscountStrategy.java             # 策略接口
├── NoDiscountStrategy.java           # 无折扣策略
├── PercentageDiscountStrategy.java   # 打折策略
├── ThresholdDiscountStrategy.java    # 满减策略
├── GlobalConfigManager.java          # 单例 — 全局配置管理器
├── Order.java                        # 上下文 — 订单
└── Main.java                         # 测试入口
```

---

## 核心流程

```
客户下单 → 创建 Order（绑定策略）
         → 调用 checkout()
             ├─ ① 委托 DiscountStrategy.calculate() 计算折后价
             ├─ ② 调用 GlobalConfigManager.getInstance() 获取税率
             └─ ③ 含税价 = 折后价 × (1 + 税率)，打印结账明细
```

---

## 编译与运行

本项目使用 Maven 构建，`pom.xml` 已配置编码为 UTF-8。

```bash
# 编译
mvn compile

# 运行（指定主类）
mvn exec:java -Dexec.mainClass="com.ecommerce.Main"
```

> 需要先安装 Maven 并确保 `mvn` 命令在 PATH 中可用。

---

## 测试场景

`Main` 类覆盖以下测试场景：

| # | 场景 | 说明 |
|---|------|------|
| 1 | 单例唯一性 | 三次调用 `getInstance()` 验证引用相同 |
| 2 | 无折扣策略 | 原价 600 元，折后价 = 600 元 |
| 3 | 8 折策略 | 原价 600 元，折后价 = 480 元 |
| 4 | 满 500 减 50 | 原价 600 元，满足门槛，折后价 = 550 元 |
| 5 | 满 500 减 50 — 未达门槛 | 原价 300 元，不满足门槛，折后价 = 300 元 |
| 6 | 运行时策略切换 | 同一订单从无折扣切换为 8 折 |
| 7 | 75 折策略 | 原价 1000 元，折后价 = 750 元 |

---

## 设计原则遵循

| 原则 | 说明 |
|------|------|
| **开闭原则** | 新增策略无需修改已有代码 |
| **单一职责** | 每个策略类只负责一种计价逻辑 |
| **里氏替换** | 任何 `DiscountStrategy` 实现可替换而客户端无感知 |
| **依赖倒置** | `Order` 依赖抽象接口而非具体策略类 |
| **接口隔离** | `DiscountStrategy` 接口最小化，仅含一个方法 |

---

## 扩展建议

如需新增折扣策略（如"买一送一"、"组合优惠"等），只需：

```java
public class BuyOneGetOneStrategy implements DiscountStrategy {
    // 实现 calculate() ...
}

// 使用
Order order = new Order(200.0, new BuyOneGetOneStrategy());
order.checkout();
```

无需修改 `Order`、`GlobalConfigManager` 或任何已有策略的代码。
