package com.ecommerce;

/**
 * 折扣策略接口（策略模式抽象角色）
 * 定义了所有具体折扣策略必须实现的计价行为
 */
public interface DiscountStrategy {

    /**
     * 根据原价计算折后价
     *
     * @param originalPrice 订单原价
     * @return 折后价格
     */
    double calculate(double originalPrice);
}
