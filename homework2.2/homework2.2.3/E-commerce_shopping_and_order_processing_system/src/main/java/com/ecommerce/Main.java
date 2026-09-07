package com.ecommerce;

import com.ecommerce.exception.CartEmptyException;
import com.ecommerce.exception.OutOfStockException;
import com.ecommerce.model.Clothing;
import com.ecommerce.model.Electronics;
import com.ecommerce.model.Product;
import com.ecommerce.service.Order;
import com.ecommerce.service.User;
import com.ecommerce.thread.OrderProcessThread;

/**
 * 电商购物与订单处理系统 - 主程序
 * 模拟一次完整的用户购物体验
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    欢迎使用电商购物与订单处理系统");
        System.out.println("========================================\n");

        // 1. 初始化商品数据（一部手机库存2，一件外套库存1）
        Electronics phone = new Electronics("E001", "智能手机", 5999.0, 2);
        Clothing jacket = new Clothing("C001", "时尚外套", 200.0, 1);
        // 额外创建一件库存为 0 的商品，用于演示异常
        Clothing soldOutShirt = new Clothing("C002", "限量T恤", 299.0, 0);

        System.out.println("📦 商品初始化完成：");
        System.out.println("  " + phone);
        System.out.println("  " + jacket);
        System.out.println("  " + soldOutShirt + " (已售罄)\n");

        // 2. 创建用户对象
        User user = new User("U001", "张三");

        // 3. 尝试加入库存为 0 的商品，演示异常被捕获
        System.out.println("--- 步骤1：尝试购买已售罄商品 ---");
        user.addToCart(soldOutShirt);

        // 4. 用户正常添加手机和外套到购物车
        System.out.println("\n--- 步骤2：正常添加商品到购物车 ---");
        user.addToCart(phone);      // 库存 2 → 1
        user.addToCart(phone);      // 库存 1 → 0
        user.addToCart(jacket);     // 库存 1 → 0

        // 查看购物车
        user.viewCart();

        // 5. 用户执行结账 (checkout)，捕获并处理可能发生的异常
        System.out.println("\n--- 步骤3：执行结账 ---");
        try {
            Order order = user.checkout();
            System.out.println("🎉 结账成功！订单已生成。\n");

            // 6. 打印订单详情
            order.printOrderInfo();

            // 7. 启动多线程：将订单交给 OrderProcessThread 并在新线程中启动
            System.out.println("--- 步骤4：启动异步订单处理 ---");
            OrderProcessThread processTask = new OrderProcessThread(order);
            Thread processThread = new Thread(processTask, "OrderProcessor");
            processThread.start();

            // 8. 主线程打印提示，验证多线程异步不阻塞
            System.out.println("主线程：用户已支付，正在浏览其他商品...");
            System.out.println("主线程：主线程继续执行，未被后台处理阻塞 ✅\n");

            // 等待后台线程结束（演示用，实际业务中不需要）
            try {
                processThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        } catch (CartEmptyException e) {
            System.out.println("❌ 结账失败：" + e.getMessage());
        }

        System.out.println("\n========================================");
        System.out.println("    购物流程结束，感谢使用！");
        System.out.println("========================================");
    }
}
