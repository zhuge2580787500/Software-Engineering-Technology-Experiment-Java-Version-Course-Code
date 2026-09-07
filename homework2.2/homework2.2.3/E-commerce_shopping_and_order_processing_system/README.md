# 电商购物与订单处理系统

基于 Maven + Java 17 构建的电商购物与订单处理系统，完整演示了面向对象设计、异常处理、多线程异步处理以及 JUnit 5 单元测试。

---

## 📋 技术栈

| 技术 | 版本/说明 |
|---|---|
| Java | 17 |
| Maven | 3.x |
| JUnit | 5.10.2 |
| Java 8+ 特性 | Stream API、Lambda、`java.time` 日期时间 API |

---

## 🏗️ 项目结构

```
E-commerce_shopping_and_order_processing_system/
├── pom.xml
├── README.md
├── docs/
│   └── P2.2.3.md                          # 实验需求文档
└── src/
    ├── main/java/com/ecommerce/
    │   ├── Main.java                      # 程序入口
    │   ├── model/
    │   │   ├── Discountable.java          # 折扣策略接口
    │   │   ├── Product.java               # 商品抽象基类
    │   │   ├── Electronics.java           # 电子产品（满5000打9折）
    │   │   └── Clothing.java              # 服装（全场8折）
    │   ├── exception/
    │   │   ├── CartEmptyException.java    # 购物车为空异常
    │   │   └── OutOfStockException.java   # 库存不足异常
    │   ├── service/
    │   │   ├── User.java                  # 用户类（购物车 / 结账）
    │   │   └── Order.java                 # 订单类
    │   └── thread/
    │       └── OrderProcessThread.java    # 异步订单处理线程
    └── test/java/com/ecommerce/
        └── ECommerceSystemTest.java       # JUnit 5 单元测试（9 cases）
```

---

## 🚀 快速开始

### 环境要求

- JDK 17 或更高版本
- Maven 3.8+

### 编译

```bash
mvn compile
```

### 运行主程序

```bash
mvn exec:java -Dexec.mainClass=com.ecommerce.Main
```

### 运行测试

```bash
mvn test
```

---

## 🎯 核心设计

### 类图

```
Discountable (接口)
  └── Product (抽象类)
        ├── Electronics  (满5000打9折)
        └── Clothing     (全场8折)

User ──adds to cart──► List<Product>
User ──creates──► Order ──contains──► List<Product>
OrderProcessThread ──processes──► Order
```

### 关键特性

| 特性 | 说明 |
|---|---|
| **多态折扣** | `Electronics` 和 `Clothing` 各自实现 `calculateDiscountPrice`，通过 `getDiscountedPrice()` 多态调用 |
| **Stream API** | `Order.calculateTotal()` 使用 `stream().mapToDouble().sum()` 计算总价 |
| **自定义异常** | `CartEmptyException`（空购物车结账）、`OutOfStockException`（库存不足） |
| **Java 时间 API** | `Order.createTime` 使用 `LocalDateTime` + `DateTimeFormatter` 格式化输出 |
| **线程安全** | `decreaseStock()` 使用 `synchronized` 关键字防止并发超卖 |
| **异步处理** | `OrderProcessThread` 模拟后台 3 秒处理，主线程不被阻塞 |

### 折扣规则

- **电子产品**：原价满 5000 元打 9 折，否则原价
- **服装**：全场无门槛 8 折

---

## 🧪 测试覆盖

`ECommerceSystemTest` 共 **9 个测试用例**，全部通过：

| 测试方法 | 说明 |
|---|---|
| `testElectronicsDiscount` | 6000元手机折后 = 5400元 |
| `testElectronicsNoDiscount` | 3000元耳机不打折 |
| `testClothingDiscount` | 200元外套折后 = 160元 |
| `testOrderTotalCalculation` | 订单总价 = 5400 + 160 = 5560 |
| `testEmptyOrderTotal` | 空订单总价 = 0 |
| `testCartEmptyException` | 空购物车结账抛出 `CartEmptyException` |
| `testOutOfStockException` | 库存为0时 `decreaseStock` 抛出 `OutOfStockException` |
| `testAddToCartDecreasesStock` | 加入购物车后库存减少 |
| `testPolymorphicDiscount` | 验证多态调用各自折扣策略 |

运行结果：

```
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0  ✅
```

---

## 📝 实验要求对照

| 实验要求 | 实现文件 |
|---|---|
| 接口 `Discountable` | `model/Discountable.java` |
| 抽象类 `Product` + `decreaseStock()` | `model/Product.java` |
| 具体商品类 `Electronics` / `Clothing` | `model/Electronics.java`, `model/Clothing.java` |
| 自定义异常 `OutOfStockException` / `CartEmptyException` | `exception/` 包 |
| 订单类 `Order`（`LocalDateTime` + `DateTimeFormatter`） | `service/Order.java` |
| 用户类 `User`（`addToCart` / `checkout`） | `service/User.java` |
| 多线程 `OrderProcessThread` | `thread/OrderProcessThread.java` |
| JUnit 5 单元测试 | `ECommerceSystemTest.java` |
| 场景模拟程序 `Main` | `Main.java` |
| Stream API 计算总价 | `Order.calculateTotal()` |
| 线程安全（进阶） | `Product.decreaseStock()` 加 `synchronized` |

---

