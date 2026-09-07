package com.ecommerce.thread;

import com.ecommerce.service.Order;

/**
 * 订单异步处理线程
 * 实现 Runnable 接口，模拟后台处理订单（如发货准备、扣款等）
 */
public class OrderProcessThread implements Runnable {

    private Order order;

    public OrderProcessThread(Order order) {
        this.order = order;
    }

    /**
     * 线程执行逻辑
     * 模拟耗时 3 秒的后台处理过程
     */
    @Override
    public void run() {
        try {
            System.out.println("⏳ [后台线程] 正在处理订单 [" + order.getOrderId() + "]...");
            Thread.sleep(3000);
            System.out.println("【系统通知】订单 [" + order.getOrderId() + "] 已异步处理完毕，进入发货流程...");
        } catch (InterruptedException e) {
            System.out.println("⚠️ 订单 [" + order.getOrderId() + "] 处理被中断！");
            Thread.currentThread().interrupt();
        }
    }
}
