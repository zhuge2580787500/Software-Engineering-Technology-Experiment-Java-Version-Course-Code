package com.ecommerce.model;

/**
 * 折扣策略接口
 * 定义商品折扣价格计算的标准方法
 */
public interface Discountable {

    /**
     * 根据原价计算折后价格
     * @param originalPrice 商品原价
     * @return 折后价格
     */
    double calculateDiscountPrice(double originalPrice);
}
