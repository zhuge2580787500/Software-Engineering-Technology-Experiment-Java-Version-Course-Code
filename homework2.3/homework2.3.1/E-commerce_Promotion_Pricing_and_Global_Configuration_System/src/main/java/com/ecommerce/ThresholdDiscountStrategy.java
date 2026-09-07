package com.ecommerce;

/**
 * 满减策略（策略模式 - 具体策略角色）
 * 达到指定门槛金额后减免固定金额
 */
public class ThresholdDiscountStrategy implements DiscountStrategy {

    /**
     * 满减门槛金额
     */
    private final double threshold;

    /**
     * 减免金额
     */
    private final double reduction;

    /**
     * @param threshold 满减门槛金额
     * @param reduction 满足门槛后的减免金额
     */
    public ThresholdDiscountStrategy(double threshold, double reduction) {
        if (threshold <= 0 || reduction <= 0) {
            throw new IllegalArgumentException("门槛金额和减免金额都必须为正数");
        }
        if (reduction >= threshold) {
            throw new IllegalArgumentException("减免金额不能大于或等于门槛金额");
        }
        this.threshold = threshold;
        this.reduction = reduction;
    }

    @Override
    public double calculate(double originalPrice) {
        if (originalPrice >= threshold) {
            double discountPrice = originalPrice - reduction;
            System.out.println("[满减策略 满" + String.format("%.0f", threshold) + "减"
                    + String.format("%.0f", reduction) + "] 原价 " + String.format("%.2f", originalPrice)
                    + " 元，优惠 " + String.format("%.2f", reduction) + " 元，折后 "
                    + String.format("%.2f", discountPrice) + " 元");
            return discountPrice;
        } else {
            System.out.println("[满减策略 满" + String.format("%.0f", threshold) + "减"
                    + String.format("%.0f", reduction) + "] 原价 " + String.format("%.2f", originalPrice)
                    + " 元，未达到门槛，无减免");
            return originalPrice;
        }
    }
}
