package com.ecommerce.exception;

/**
 * 购物车为空异常
 * 当用户尝试对空购物车进行结账操作时抛出
 */
public class CartEmptyException extends Exception {

    public CartEmptyException() {
        super();
    }

    public CartEmptyException(String message) {
        super(message);
    }
}
