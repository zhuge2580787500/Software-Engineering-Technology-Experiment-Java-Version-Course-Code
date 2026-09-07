package com.ecommerce;

/**
 * 打折策略（策略模式 - 具体策略角色）
 * 按传入的比例打折（如传入 0.8 表示 8 折）
 */
public class PercentageDiscountStrategy implements DiscountStrategy {

    /**
     * 折扣比例（如 0.8 表示 8 折）
     */
    private final double percentage;

    /**
     * @param percentage 折扣比例，范围 (0, 1]
     */
    public PercentageDiscountStrategy(double percentage) {
        if (percentage <= 0 || percentage > 1) {
            throw new IllegalArgumentException("折扣比例必须在 (0, 1] 范围内，当前传入: " + percentage);
        }
        this.percentage = percentage;
    }

    @Override
    public double calculate(double originalPrice) {
        double discountPrice = originalPrice * percentage;
        double saved = originalPrice - discountPrice;
        int discountPercent = (int) (percentage * 100);
        System.out.println("[" + discountPercent + "折策略] 原价 " + String.format("%.2f", originalPrice)
                + " 元，优惠 " + String.format("%.2f", saved) + " 元，折后 " + String.format("%.2f", discountPrice) + " 元");
        return discountPrice;
    }
}
