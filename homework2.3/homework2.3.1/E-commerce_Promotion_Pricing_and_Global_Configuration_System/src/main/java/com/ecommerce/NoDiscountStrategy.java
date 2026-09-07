package com.ecommerce;

/**
 * 无折扣策略（策略模式 - 具体策略角色）
 * 原价返回，不做任何减免
 */
public class NoDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculate(double originalPrice) {
        System.out.println("[无折扣策略] 原价 " + String.format("%.2f", originalPrice) + " 元，无减免");
        return originalPrice;
    }
}
