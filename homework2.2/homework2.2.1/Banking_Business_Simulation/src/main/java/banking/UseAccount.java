package banking;

/**
 * 主测试类 - 银行业务模拟程序入口
 * <p>
 * 在 main 方法中模拟用户的真实银行操作流程，
 * 验证 {@link BankAccount} 类的正确性。
 * </p>
 *
 * @author wyh
 * @date 2026-09-07
 */
public class UseAccount {

    public static void main(String[] args) {
        System.out.println("--- 欢迎办理银行业务 ---");

        // ========== 1. 开户（使用带参构造方法创建账户） ==========
        BankAccount account = new BankAccount("VIP666", 1000.0, 0.02);

        // ========== 2. 打印初始账户信息 ==========
        account.printAccountInfo();

        System.out.println();

        // ========== 3. 存款测试 ==========
        System.out.println(">>> 尝试存款: 500.0 元");
        account.deposit(500.0);
        System.out.printf("存款成功！当前余额为: %.2f 元%n", account.getBalance());

        System.out.println();

        System.out.println(">>> 尝试存款: -100.0 元");
        account.deposit(-100.0);

        System.out.println();

        // ========== 4. 取款测试 ==========
        System.out.println(">>> 尝试取款: 200.0 元");
        account.withdraw(200.0);
        System.out.printf("取款成功！当前余额为: %.2f 元%n", account.getBalance());

        System.out.println();

        System.out.println(">>> 尝试取款: 5000.0 元");
        account.withdraw(5000.0);

        System.out.println();

        // ========== 5. 利率测试 ==========
        System.out.printf(">>> 查询利率: 当前利率为 %.2f%n", account.getInterestRate());
        System.out.println(">>> 设置新利率为: 0.035");
        account.setInterestRate(0.035);

        System.out.println();

        // ========== 6. 最终确认 ==========
        System.out.println("--- 最终账户状态 ---");
        account.printAccountInfo();
    }
}
