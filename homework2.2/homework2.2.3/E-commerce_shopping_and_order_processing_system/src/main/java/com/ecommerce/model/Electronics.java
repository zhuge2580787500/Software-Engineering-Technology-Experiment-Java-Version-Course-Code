package com.ecommerce.model;

/**
 * 电子产品类
 * 折扣规则：原价满 5000 元打 9 折，否则不打折
 */
public class Electronics extends Product {

    public Electronics(String productId, String name, double price, int stock) {
        super(productId, name, price, stock);
    }

    /**
     * 电子产品折扣计算逻辑
     * @param originalPrice 原价
     * @return 折后价格（满 5000 打 9 折）
     */
    @Override
    public double calculateDiscountPrice(double originalPrice) {
        return originalPrice >= 5000 ? originalPrice * 0.9 : originalPrice;
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
