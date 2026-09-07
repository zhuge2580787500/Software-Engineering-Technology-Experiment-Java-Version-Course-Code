package com.ecommerce.service;

import com.ecommerce.exception.CartEmptyException;
import com.ecommerce.exception.OutOfStockException;
import com.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户类
 * 管理用户的购物车和结账流程
 */
public class User {

    private String userId;
    private String userName;
    private List<Product> shoppingCart;

    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.shoppingCart = new ArrayList<>();
    }

    /**
     * 将商品加入购物车
     * 加入前先调用 decreaseStock() 扣减库存，若库存不足则提示失败
     * @param p 要加入购物车的商品
     */
    public void addToCart(Product p) {
        try {
            p.decreaseStock(1);
            shoppingCart.add(p);
            System.out.println("✅ 成功将 [" + p.getName() + "] 加入购物车，" +
                    "剩余库存：" + p.getStock());
        } catch (OutOfStockException e) {
            System.out.println("❌ 添加失败：" + e.getMessage());
        }
    }

    /**
     * 结账操作
     * 判断购物车是否为空，为空则抛出 CartEmptyException
     * 否则生成订单对象，清空购物车并返回订单
     * @return 生成的订单对象
     * @throws CartEmptyException 购物车为空时抛出
     */
    public Order checkout() throws CartEmptyException {
        if (shoppingCart.isEmpty()) {
            throw new CartEmptyException("购物车为空，无法结账！请先添加商品。");
        }

        // 使用 Stream API 复制当前购物车商品列表（用于创建订单）
        List<Product> orderProducts = shoppingCart.stream()
                .collect(Collectors.toList());

        Order order = new Order(orderProducts);

        // 清空购物车
        shoppingCart.clear();

        return order;
    }

    /**
     * 查看当前购物车中的商品
     */
    public void viewCart() {
        if (shoppingCart.isEmpty()) {
            System.out.println("🛒 购物车为空");
            return;
        }
        System.out.println("🛒 当前购物车商品：");
        shoppingCart.stream()
                .forEach(p -> System.out.printf("  - %s (%.2f 元)%n", p.getName(), p.getPrice()));
    }

    // ─── Getters / Setters ───────────────────────────────────────

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public List<Product> getShoppingCart() {
        return shoppingCart;
    }
}
