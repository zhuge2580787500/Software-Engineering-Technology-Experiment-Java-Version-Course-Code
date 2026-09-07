package com.ecommerce;

import com.ecommerce.model.Clothing;
import com.ecommerce.model.Electronics;
import com.ecommerce.model.Product;
import com.ecommerce.service.Order;
import com.ecommerce.service.User;
import com.ecommerce.exception.CartEmptyException;
import com.ecommerce.exception.OutOfStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 电商系统单元测试类
 * 使用 JUnit 5 对核心业务逻辑进行自动化测试
 */
@DisplayName("电商购物与订单处理系统 - 单元测试")
class ECommerceSystemTest {

    private Electronics phone;
    private Clothing jacket;

    @BeforeEach
    void setUp() {
        // 每
        phone = new Electronics("E001", "智能手机", 6000.0, 10);
        jacket = new Clothing("C001", "时尚外套", 200.0, 10);
    }

    // ──────────────────────────────────────────────────────────────
    // 测试 1：折扣逻辑
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("电子产品折扣：6000元手机应打9折 → 5400元")
    void testElectronicsDiscount() {
        // 电子产品原价 ≥ 5000，打 9 折
        assertEquals(5400.0, phone.getDiscountedPrice(), 0.001,
                "6000元的电子产品折后价应为 5400 元");
    }

    @Test
    @DisplayName("电子产品不打折：3000元耳机原价不变")
    void testElectronicsNoDiscount() {
        Electronics earphone = new Electronics("E002", "无线耳机", 3000.0, 10);
        assertEquals(3000.0, earphone.getDiscountedPrice(), 0.001,
                "3000元的电子产品未达5000门槛，不打折");
    }

    @Test
    @DisplayName("服装折扣：200元外套打8折 → 160元")
    void testClothingDiscount() {
        // 服装全场 8 折
        assertEquals(160.0, jacket.getDiscountedPrice(), 0.001,
                "200元的服装折后价应为 160 元");
    }

    // ──────────────────────────────────────────────────────────────
    // 测试 2：订单总金额计算
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("订单总金额：手机5400 + 外套160 = 5560")
    void testOrderTotalCalculation() {
        // 手动创建包含两件商品的商品列表
        List<Product> products = new ArrayList<>();
        products.add(phone);
        products.add(jacket);

        Order order = new Order(products);

        double expected = 5400.0 + 160.0; // 5560.0
        assertEquals(expected, order.getTotalAmount(), 0.001,
                "订单总金额应为 5400 + 160 = 5560 元");
    }

    @Test
    @DisplayName("空列表订单总金额为 0")
    void testEmptyOrderTotal() {
        List<Product> emptyList = new ArrayList<>();
        Order emptyOrder = new Order(emptyList);
        assertEquals(0.0, emptyOrder.getTotalAmount(), 0.001,
                "空订单的总金额应为 0");
    }

    // ──────────────────────────────────────────────────────────────
    // 测试 3：异常抛出
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("空购物车结账应抛出 CartEmptyException")
    void testCartEmptyException() {
        User user = new User("U_TEST", "测试用户");
        // 不添加任何商品，直接结账
        assertThrows(CartEmptyException.class, () -> user.checkout(),
                "空购物车结账应抛出 CartEmptyException");
    }

    @Test
    @DisplayName("库存为0时 addToCart 应抛出 OutOfStockException")
    void testOutOfStockException() {
        // 创建库存为 0 的商品
        Product outOfStock = new Clothing("C003", "缺货商品", 100.0, 0) {
            @Override
            public double getDiscountedPrice() {
                return calculateDiscountPrice(price);
            }
        };

        User user = new User("U_TEST2", "测试用户2");

        // 尝试将库存为0的商品加入购物车，应被捕获异常（addToCart 内部捕获）
        // 验证 decreaseStock 会抛出 OutOfStockException
        assertThrows(OutOfStockException.class,
                () -> outOfStock.decreaseStock(1),
                "库存为0时 decreaseStock 应抛出 OutOfStockException");
    }

    // ──────────────────────────────────────────────────────────────
    // 附加测试：addToCart 成功扣减库存
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("addToCart 应成功扣减库存")
    void testAddToCartDecreasesStock() {
        int initialStock = jacket.getStock();
        User user = new User("U_TEST3", "测试用户3");
        user.addToCart(jacket);

        assertEquals(initialStock - 1, jacket.getStock(),
                "加入购物车后库存应减少1");
        assertEquals(1, user.getShoppingCart().size(),
                "购物车中应有1件商品");
    }

    // ──────────────────────────────────────────────────────────────
    // 附加测试：calculateDiscountPrice 被正确调用（多态验证）
    // ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("多态验证：不同商品类型调用各自的折扣策略")
    void testPolymorphicDiscount() {
        // 电子产品 > 5000 → 9折
        assertEquals(5400.0, phone.calculateDiscountPrice(6000.0), 0.001);

        // 服装 → 8折
        assertEquals(160.0, jacket.calculateDiscountPrice(200.0), 0.001);

        // 电子产品 < 5000 → 不打折
        Electronics cheap = new Electronics("E003", "充电器", 99.0, 100);
        assertEquals(99.0, cheap.calculateDiscountPrice(99.0), 0.001);
    }
}
