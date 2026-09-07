package com.ecommerce.model;

import com.ecommerce.exception.OutOfStockException;

/**
 * 商品抽象基类
 * 实现了 Discountable 接口，定义所有商品的通用属性和行为
 */
public abstract class Product implements Discountable {

    protected String productId;
    protected String name;
    protected double price;
    protected int stock;

    public Product(String productId, String name, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    /**
     * 减少商品库存（线程安全，支持进阶挑战）
     * @param quantity 要扣减的数量
     * @throws OutOfStockException 库存不足时抛出
     */
    public synchronized void decreaseStock(int quantity) throws OutOfStockException {
        if (stock < quantity) {
            throw new OutOfStockException(
                    "商品 [" + name + "] 库存不足，当前库存：" + stock + "，需求数量：" + quantity);
        }
        stock -= quantity;
    }

    /**
     * 获取折后价格（抽象方法，由子类实现具体的折扣逻辑）
     * @return 折后价格
     */
    public abstract double getDiscountedPrice();

    // ─── Getters / Setters ───────────────────────────────────────

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }
}
