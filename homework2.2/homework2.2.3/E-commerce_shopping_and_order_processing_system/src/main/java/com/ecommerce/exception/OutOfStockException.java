package com.ecommerce.exception;

/**
 * 库存不足异常
 * 当商品库存不足以满足购买需求时抛出
 */
public class OutOfStockException extends Exception {

    public OutOfStockException() {
        super();
    }

    public OutOfStockException(String message) {
        super(message);
    }
}
