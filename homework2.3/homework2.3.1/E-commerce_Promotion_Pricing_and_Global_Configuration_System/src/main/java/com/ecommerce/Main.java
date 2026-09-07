package com.ecommerce;

/**
 * Main 测试类
 *
 * <p>验证单例唯一性 + 使用不同折扣策略进行订单计价测试。
 * 全程无 if-else / switch-case 堆砌业务逻辑，折扣行为完全由策略对象承担。
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  电商促销计价与全局配置系统 — 测试启动");
        System.out.println("========================================");

        // ---- 第一部分：验证单例唯一性 ----
        testSingleton();

        // ---- 第二部分：验证策略模式计价逻辑 ----
        testStrategies();
    }

    /**
     * 验证 GlobalConfigManager 单例的唯一性（线程安全保证）
     */
    private static void testSingleton() {
        System.out.println("\n--------- 测试 1：单例唯一性验证 ---------");

        // 连续多次获取实例，比较引用是否相同
        GlobalConfigManager config1 = GlobalConfigManager.getInstance();
        GlobalConfigManager config2 = GlobalConfigManager.getInstance();
        GlobalConfigManager config3 = GlobalConfigManager.getInstance();

        boolean same = (config1 == config2) && (config2 == config3);
        System.out.println("实例1 hashCode : " + System.identityHashCode(config1));
        System.out.println("实例2 hashCode : " + System.identityHashCode(config2));
        System.out.println("实例3 hashCode : " + System.identityHashCode(config3));
        System.out.println("三次获取实例是否为同一对象: " + (same ? "是 ✓" : "否 ✗"));
        System.out.println("平台名称: " + config1.getPlatformName());
        System.out.println("全局税率: " + String.format("%.1f%%", config1.getTaxRate() * 100));
        System.out.println("toString(): " + config1);
    }

    /**
     * 使用不同策略创建订单并结账，验证策略模式的解耦能力
     */
    private static void testStrategies() {
        System.out.println("\n--------- 测试 2：策略模式计价验证 ---------");

        // ---- 场景 A：无折扣策略 ----
        System.out.println("\n>>> 场景 A：无折扣策略（原价 600 元）");
        Order orderA = new Order(600.0, new NoDiscountStrategy());
        orderA.checkout();

        // ---- 场景 B：8 折策略 ----
        System.out.println("\n>>> 场景 B：8 折策略（原价 600 元）");
        Order orderB = new Order(600.0, new PercentageDiscountStrategy(0.8));
        orderB.checkout();

        // ---- 场景 C：满 500 减 50 ----
        System.out.println("\n>>> 场景 C：满 500 减 50（原价 600 元）");
        Order orderC = new Order(600.0, new ThresholdDiscountStrategy(500, 50));
        orderC.checkout();

        // ---- 场景 D：满 500 减 50 — 未达门槛 ----
        System.out.println("\n>>> 场景 D：满 500 减 50（原价 300 元，未达门槛）");
        Order orderD = new Order(300.0, new ThresholdDiscountStrategy(500, 50));
        orderD.checkout();

        // ---- 场景 E：运行时动态切换策略 ----
        System.out.println("\n>>> 场景 E：运行时动态切换策略（原价 400 元）");
        Order orderE = new Order(400.0, new NoDiscountStrategy());
        System.out.println("初始策略：无折扣");
        orderE.checkout();
        // 中途切换为 8 折策略，无需修改 Order 内部代码
        System.out.println("策略切换为：8 折策略");
        orderE.setDiscountStrategy(new PercentageDiscountStrategy(0.8));
        orderE.checkout();

        // ---- 场景 F：75 折策略 ----
        System.out.println("\n>>> 场景 F：75 折策略（原价 1000 元）");
        Order orderF = new Order(1000.0, new PercentageDiscountStrategy(0.75));
        orderF.checkout();

        System.out.println("\n========================================");
        System.out.println("  全部测试完成！");
        System.out.println("========================================");
    }
}
