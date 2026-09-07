package com.ecommerce;

import java.text.DecimalFormat;

/**
 * 订单类（策略模式 - 上下文角色 Context）
 *
 * <p>持有订单总金额和一个 DiscountStrategy 实例。
 * 结账时委托策略计算折后价，再结合全局税率计算含税最终价。
 */
public class Order {

    /**
     * 订单原价（商品合计，未打折）
     */
    private final double originalPrice;

    /**
     * 绑定的折扣策略（可在创建后通过 setter 切换，实现运行时策略互换）
     */
    private DiscountStrategy discountStrategy;

    /**
     * @param originalPrice 订单原价
     */
    public Order(double originalPrice) {
        if (originalPrice < 0) {
            throw new IllegalArgumentException("订单金额不能为负数");
        }
        this.originalPrice = originalPrice;
        // 默认使用无折扣策略
        this.discountStrategy = new NoDiscountStrategy();
    }

    /**
     * @param discountStrategy 折扣策略
     * @param originalPrice    订单原价
     */
    public Order(double originalPrice, DiscountStrategy discountStrategy) {
        if (originalPrice < 0) {
            throw new IllegalArgumentException("订单金额不能为负数");
        }
        if (discountStrategy == null) {
            throw new IllegalArgumentException("折扣策略不能为空");
        }
        this.originalPrice = originalPrice;
        this.discountStrategy = discountStrategy;
    }

    // ---- Setter：允许运行时切换策略 ----
    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        if (discountStrategy == null) {
            throw new IllegalArgumentException("折扣策略不能为空");
        }
        this.discountStrategy = discountStrategy;
    }

    // ---- 结账方法 ----
    public void checkout() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        GlobalConfigManager config = GlobalConfigManager.getInstance();

        System.out.println("\n========== " + config.getPlatformName() + " 订单结账 ==========");
        System.out.println("订单原价  : " + df.format(originalPrice) + " 元");

        // 第一步：通过策略模式计算折后价
        double discountedPrice = discountStrategy.calculate(originalPrice);

        // 第二步：从单例获取税率，计算含税价
        double tax = discountedPrice * config.getTaxRate();
        double finalPrice = discountedPrice + tax;

        System.out.println("折后价    : " + df.format(discountedPrice) + " 元");
        System.out.println("税率      : " + String.format("%.1f%%", config.getTaxRate() * 100));
        System.out.println("税费      : " + df.format(tax) + " 元");
        System.out.println("含税最终价: " + df.format(finalPrice) + " 元");
        System.out.println("============================================");
    }
}
