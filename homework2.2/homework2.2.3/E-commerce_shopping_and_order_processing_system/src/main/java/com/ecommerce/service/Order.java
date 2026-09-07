package com.ecommerce.service;

import com.ecommerce.model.Product;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 订单类
 * 记录用户一次结账的所有商品信息、总金额和创建时间
 */
public class Order {

    private String orderId;
    private List<Product> productList;
    private double totalAmount;
    private LocalDateTime createTime;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Order(List<Product> productList) {
        this.orderId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.productList = productList;
        this.createTime = LocalDateTime.now();
        this.totalAmount = calculateTotal();
    }

    /**
     * 使用 Java 8 Stream API 计算订单总金额
     * 遍历商品列表，累加每个商品的折后价格
     * @return 订单总金额
     */
    public double calculateTotal() {
        return productList.stream()
                .mapToDouble(Product::getDiscountedPrice)
                .sum();
    }

    /**
     * 打印订单详情（含格式化时间戳）
     */
    public void printOrderInfo() {
        System.out.println("========== 订单详情 ==========");
        System.out.println("订单号     : " + orderId);
        System.out.println("下单时间   : " + createTime.format(FORMATTER));
        System.out.println("商品列表   :");
        for (Product p : productList) {
            System.out.printf("  - %s | 原价: %.2f | 折后价: %.2f%n",
                    p.getName(), p.getPrice(), p.getDiscountedPrice());
        }
        System.out.printf("订单总金额 : %.2f%n", totalAmount);
        System.out.println("===============================");
    }

    // ─── Getters ─────────────────────────────────────────────────

    public String getOrderId() {
        return orderId;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }
}
