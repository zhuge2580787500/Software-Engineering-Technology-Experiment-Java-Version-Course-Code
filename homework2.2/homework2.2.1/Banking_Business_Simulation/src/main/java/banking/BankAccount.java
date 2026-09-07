package banking;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 银行账户类 - 模拟现实世界中的银行账户
 * <p>
 * 严格遵循面向对象封装原则，所有属性私有化，
 * 通过公共方法提供安全的访问和操作接口。
 * </p>
 *
 * @author wyh
 * @date 2026-09-07
 */
public class BankAccount {

    /**
     * 账号
     */
    private String accountId;

    /**
     * 账户余额
     */
    private double balance;

    /**
     * 年利率（例如 0.03 代表 3%）
     */
    private double interestRate;

    // ========== 构造方法 ==========

    /**
     * 无参构造方法
     * 默认余额为 0，利率为 0.01（1%）
     */
    public BankAccount() {
        this("", 0.0, 0.01);
    }

    /**
     * 带参构造方法
     *
     * @param accountId 账号
     * @param balance   初始余额（不能为负数）
     * @param interestRate 年利率（不能为负数）
     */
    public BankAccount(String accountId, double balance, double interestRate) {
        if (balance < 0) {
            throw new IllegalArgumentException("初始余额不能为负数: " + balance);
        }
        if (interestRate < 0) {
            throw new IllegalArgumentException("利率不能为负数: " + interestRate);
        }
        this.accountId = accountId;
        this.balance = balance;
        this.interestRate = interestRate;
    }

    // ========== 查询方法 ==========

    /**
     * 查询余额
     *
     * @return 当前账户余额
     */
    public double getBalance() {
        return balance;
    }

    /**
     * 查询利率
     *
     * @return 当前年利率
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * 查询账号
     *
     * @return 账号
     */
    public String getAccountId() {
        return accountId;
    }

    // ========== 修改方法 ==========

    /**
     * 设置利率
     *
     * @param rate 新的年利率（不能为负数）
     */
    public void setInterestRate(double rate) {
        if (rate < 0) {
            System.out.println("设置利率失败：利率不能为负数！");
            return;
        }
        this.interestRate = rate;
    }

    /**
     * 存款
     *
     * @param amount 存款金额（必须大于 0）
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("存款失败：存款金额必须大于0！");
            return;
        }
        this.balance += amount;
    }

    /**
     * 取款
     *
     * @param amount 取款金额（必须大于 0 且不超过当前余额）
     */
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("取款失败：取款金额必须大于0！");
            return;
        }
        if (amount > this.balance) {
            System.out.printf("取款失败：余额不足！当前余额仅为: %.2f 元%n", this.balance);
            return;
        }
        this.balance -= amount;
    }

    // ========== 其他业务方法 ==========

    /**
     * 计算利息并将利息存入余额
     *
     * @return 利息金额
     */
    public double calculateInterest() {
        double interest = this.balance * this.interestRate;
        this.balance += interest;
        return interest;
    }

    /**
     * 打印账户信息
     */
    public void printAccountInfo() {
        System.out.printf("【账户信息】账号: %s | 余额: %.2f 元 | 当前利率: %.2f%%%n",
                accountId, balance, interestRate * 100);
    }

    // ========== 工具方法 ==========

    /**
     * 将 double 金额格式化为两位小数的字符串
     * 使用 BigDecimal 避免浮点数精度问题
     *
     * @param amount 金额
     * @return 格式化为两位小数的字符串
     */
    public static String formatAmount(double amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP)
                .toString();
    }

    /**
     * 将利率格式化为百分比字符串（保留两位小数）
     *
     * @param rate 利率
     * @return 百分比字符串，如 "2.00%"
     */
    public static String formatRate(double rate) {
        return String.format("%.2f%%", rate * 100);
    }
}
