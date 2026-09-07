package com.ecommerce.model;

/**
 * 服装类
 * 折扣规则：全场无门槛 8 折
 */
public class Clothing extends Product {

    public Clothing(String productId, String name, double price, int stock) {
        super(productId, name, price, stock);
    }

    /**
     * 服装折扣计算逻辑
     * @param originalPrice 原价
     * @return 折后价格（全场 8 折）
     */
    @Override
    public double calculateDiscountPrice(double originalPrice) {
        return originalPrice * 0.8;
    }

    /**
     * 获取当前商品的折后价格
     * @return 折后价格
     */
    @Override
    public double getDiscountedPrice() {
        return calculateDiscountPrice(price);
    }
}
