package banking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BankAccount 单元测试类
 * <p>
 * 使用 JUnit 5 框架进行全面的单元测试，
 * 充分利用 Java 8+ 的 Stream API、Lambda 表达式和函数式接口。
 * </p>
 *
 * @author wyh
 * @date 2026-09-07
 */
@DisplayName("BankAccount 银行账户类测试")
class BankAccountTest {

    private BankAccount account;

    // ========== 测试数据常量 ==========
    private static final String ACCOUNT_ID = "VIP666";
    private static final double INITIAL_BALANCE = 1000.0;
    private static final double INITIAL_RATE = 0.02;

    @BeforeEach
    void setUp() {
        account = new BankAccount(ACCOUNT_ID, INITIAL_BALANCE, INITIAL_RATE);
    }

    // ========== 构造方法测试 ==========

    @Nested
    @DisplayName("构造方法测试")
    class ConstructorTests {

        @Test
        @DisplayName("带参构造方法应正确初始化所有属性")
        void testParametrizedConstructor() {
            BankAccount acc = new BankAccount("ACC001", 500.0, 0.03);
            assertEquals("ACC001", acc.getAccountId());
            assertEquals(500.0, acc.getBalance(), 0.001);
            assertEquals(0.03, acc.getInterestRate(), 0.001);
        }

        @Test
        @DisplayName("无参构造方法应使用默认值")
        void testNoArgConstructor() {
            BankAccount acc = new BankAccount();
            assertEquals("", acc.getAccountId());
            assertEquals(0.0, acc.getBalance(), 0.001);
            assertEquals(0.01, acc.getInterestRate(), 0.001);
        }

        @Test
        @DisplayName("带参构造方法对负数余额应抛出异常")
        void testConstructorRejectsNegativeBalance() {
            assertThrows(IllegalArgumentException.class,
                    () -> new BankAccount("ACC", -100.0, 0.02));
        }

        @Test
        @DisplayName("带参构造方法对负利率应抛出异常")
        void testConstructorRejectsNegativeRate() {
            assertThrows(IllegalArgumentException.class,
                    () -> new BankAccount("ACC", 100.0, -0.01));
        }
    }

    // ========== 查询方法测试 ==========

    @Nested
    @DisplayName("查询方法测试")
    class GetterTests {

        @Test
        @DisplayName("getBalance 应返回正确的余额")
        void testGetBalance() {
            assertEquals(INITIAL_BALANCE, account.getBalance(), 0.001);
        }

        @Test
        @DisplayName("getInterestRate 应返回正确的利率")
        void testGetInterestRate() {
            assertEquals(INITIAL_RATE, account.getInterestRate(), 0.0001);
        }

        @Test
        @DisplayName("getAccountId 应返回正确的账号")
        void testGetAccountId() {
            assertEquals(ACCOUNT_ID, account.getAccountId());
        }
    }

    // ========== 存款方法测试 ==========

    @Nested
    @DisplayName("存款方法测试")
    class DepositTests {

        @Test
        @DisplayName("存入合法金额应增加余额")
        void testDepositValidAmount() {
            account.deposit(500.0);
            assertEquals(1500.0, account.getBalance(), 0.001);
        }

        @Test
        @DisplayName("存入 0 应失败")
        void testDepositZero() {
            double before = account.getBalance();
            account.deposit(0.0);
            assertEquals(before, account.getBalance(), 0.001);
        }

        @ParameterizedTest(name = "存款金额 {0} 应被拒绝")
        @ValueSource(doubles = {-100.0, -0.01, -1.0})
        @DisplayName("存入负数金额应被拒绝")
        void testDepositNegativeAmount(double amount) {
            double before = account.getBalance();
            account.deposit(amount);
            assertEquals(before, account.getBalance(), 0.001,
                    "余额不应因非法存款而改变");
        }

        @Test
        @DisplayName("多次存款应正确累加")
        void testMultipleDeposits() {
            // 使用 Stream API 模拟多次存款
            double[] amounts = {100.0, 200.0, 300.0};
            double totalDeposited = Arrays.stream(amounts).sum();

            Arrays.stream(amounts).forEach(account::deposit);

            assertEquals(INITIAL_BALANCE + totalDeposited, account.getBalance(), 0.001);
        }

        @Test
        @DisplayName("使用 Lambda 存款并验证结果")
        void testDepositWithLambda() {
            // 使用函数式接口 DoublePredicate 验证
            DoublePredicate isValidAmount = amount -> amount > 0;
            assertTrue(isValidAmount.test(500.0));

            account.deposit(500.0);
            assertEquals(1500.0, account.getBalance(), 0.001);
        }
    }

    // ========== 取款方法测试 ==========

    @Nested
    @DisplayName("取款方法测试")
    class WithdrawTests {

        @Test
        @DisplayName("取出合法金额应减少余额")
        void testWithdrawValidAmount() {
            account.withdraw(200.0);
            assertEquals(800.0, account.getBalance(), 0.001);
        }

        @Test
        @DisplayName("取出 0 应失败")
        void testWithdrawZero() {
            double before = account.getBalance();
            account.withdraw(0.0);
            assertEquals(before, account.getBalance(), 0.001);
        }

        @Test
        @DisplayName("取出超过余额的金额应失败")
        void testWithdrawOverdraft() {
            double before = account.getBalance();
            account.withdraw(5000.0);
            assertEquals(before, account.getBalance(), 0.001);
        }

        @Test
        @DisplayName("取出恰好等于余额的金额应成功")
        void testWithdrawExactBalance() {
            account.withdraw(INITIAL_BALANCE);
            assertEquals(0.0, account.getBalance(), 0.001);
        }

        @Test
        @DisplayName("负数取款金额应失败")
        void testWithdrawNegativeAmount() {
            double before = account.getBalance();
            account.withdraw(-100.0);
            assertEquals(before, account.getBalance(), 0.001);
        }

        @Test
        @DisplayName("使用 Stream 进行批量取款模拟")
        void testBatchWithdrawals() {
            // 先存款足够金额
            account.deposit(2000.0);

            // 使用 Stream API 模拟批量取款
            double[] withdrawals = {300.0, 400.0, 200.0};
            Arrays.stream(withdrawals).forEach(account::withdraw);

            assertEquals(INITIAL_BALANCE + 2000.0 - (300.0 + 400.0 + 200.0),
                    account.getBalance(), 0.001);
        }
    }

    // ========== 利率设置测试 ==========

    @Nested
    @DisplayName("利率设置测试")
    class InterestRateTests {

        @Test
        @DisplayName("设置合法利率应成功")
        void testSetInterestRateValid() {
            account.setInterestRate(0.035);
            assertEquals(0.035, account.getInterestRate(), 0.0001);
        }

        @Test
        @DisplayName("设置负利率应失败且不改变原利率")
        void testSetInterestRateNegative() {
            double before = account.getInterestRate();
            account.setInterestRate(-0.01);
            assertEquals(before, account.getInterestRate(), 0.0001);
        }

        @ParameterizedTest(name = "设置利率 {0} 应成功")
        @ValueSource(doubles = {0.0, 0.01, 0.05, 0.10})
        @DisplayName("边界利率值应被正确设置")
        void testSetBoundaryInterestRate(double rate) {
            account.setInterestRate(rate);
            assertEquals(rate, account.getInterestRate(), 0.0001);
        }
    }

    // ========== 利息计算测试（进阶功能） ==========

    @Nested
    @DisplayName("利息计算测试（进阶功能）")
    class InterestCalculationTests {

        @Test
        @DisplayName("calculateInterest 应正确计算并累加利息")
        void testCalculateInterest() {
            // 预期利息 = 1000.0 * 0.02 = 20.0
            double expectedInterest = 20.0;
            double actualInterest = account.calculateInterest();
            assertEquals(expectedInterest, actualInterest, 0.001);
            assertEquals(1020.0, account.getBalance(), 0.001);
        }

        @Test
        @DisplayName("零余额不应产生利息")
        void testCalculateInterestWithZeroBalance() {
            BankAccount zeroAccount = new BankAccount("ZERO", 0.0, 0.05);
            assertEquals(0.0, zeroAccount.calculateInterest(), 0.001);
        }
    }

    // ========== 格式化工具方法测试 ==========

    @Nested
    @DisplayName("格式化工具方法测试")
    class FormattingTests {

        @Test
        @DisplayName("formatAmount 应正确格式化金额为两位小数")
        void testFormatAmount() {
            assertEquals("1000.00", BankAccount.formatAmount(1000.0));
            assertEquals("1500.50", BankAccount.formatAmount(1500.5));
            assertEquals("0.01", BankAccount.formatAmount(0.01));
        }

        @Test
        @DisplayName("formatRate 应正确格式化为百分比字符串")
        void testFormatRate() {
            assertEquals("2.00%", BankAccount.formatRate(0.02));
            assertEquals("3.50%", BankAccount.formatRate(0.035));
            assertEquals("0.00%", BankAccount.formatRate(0.0));
        }
    }

    // ========== 综合业务场景测试（使用 Java 8+ 特性） ==========

    @Nested
    @DisplayName("综合业务场景测试")
    class IntegrationTests {

        @Test
        @DisplayName("完整业务流程：开户→存款→取款→改利率")
        void testFullBusinessFlow() {
            // 开户
            BankAccount acc = new BankAccount("TEST01", 1000.0, 0.02);
            assertEquals(1000.0, acc.getBalance(), 0.001);

            // 存款
            acc.deposit(500.0);
            acc.deposit(300.0);
            assertEquals(1800.0, acc.getBalance(), 0.001);

            // 取款
            acc.withdraw(200.0);
            acc.withdraw(100.0);
            assertEquals(1500.0, acc.getBalance(), 0.001);

            // 修改利率
            acc.setInterestRate(0.03);
            assertEquals(0.03, acc.getInterestRate(), 0.0001);

            // 结算利息
            double interest = acc.calculateInterest();
            assertEquals(45.0, interest, 0.001);
            assertEquals(1545.0, acc.getBalance(), 0.001);
        }

        @Test
        @DisplayName("使用 Stream API 模拟多账户批量操作")
        void testBatchAccountOperations() {
            // 创建多个账户
            List<BankAccount> accounts = Arrays.asList(
                    new BankAccount("A1", 1000.0, 0.02),
                    new BankAccount("A2", 2000.0, 0.03),
                    new BankAccount("A3", 500.0, 0.01)
            );

            // 使用 Stream API 批量操作所有账户
            accounts.forEach(acc -> {
                acc.deposit(100.0);
                acc.withdraw(50.0);
            });

            // 使用 Stream API 验证所有账户余额
            List<Double> balances = accounts.stream()
                    .map(BankAccount::getBalance)
                    .collect(Collectors.toList());

            assertEquals(1050.0, balances.get(0), 0.001);
            assertEquals(2050.0, balances.get(1), 0.001);
            assertEquals(550.0, balances.get(2), 0.001);
        }

        @Test
        @DisplayName("使用 Stream 和 Predicate 筛选高利率账户")
        void testFilterHighInterestAccounts() {
            List<BankAccount> accounts = Arrays.asList(
                    new BankAccount("LOW", 1000.0, 0.01),
                    new BankAccount("HIGH", 2000.0, 0.05),
                    new BankAccount("MED", 1500.0, 0.03)
            );

            // 使用 Predicate（函数式接口）筛选利率 >= 0.03 的账户
            Predicate<BankAccount> highRateFilter = acc -> acc.getInterestRate() >= 0.03;

            List<String> highRateIds = accounts.stream()
                    .filter(highRateFilter)
                    .map(BankAccount::getAccountId)
                    .collect(Collectors.toList());

            assertEquals(2, highRateIds.size());
            assertTrue(highRateIds.contains("HIGH"));
            assertTrue(highRateIds.contains("MED"));
        }

        @Test
        @DisplayName("使用 DoubleStream 计算所有账户的总余额")
        void testTotalBalanceWithDoubleStream() {
            List<BankAccount> accounts = Arrays.asList(
                    new BankAccount("A1", 1000.0, 0.02),
                    new BankAccount("A2", 2000.0, 0.03),
                    new BankAccount("A3", 500.0, 0.01)
            );

            // 使用 DoubleStream 计算总余额
            double totalBalance = accounts.stream()
                    .mapToDouble(BankAccount::getBalance)
                    .sum();

            assertEquals(3500.0, totalBalance, 0.001);
        }

        @Test
        @DisplayName("使用 Stream 验证所有账户状态一致性")
        void testAccountStateConsistency() {
            List<BankAccount> accounts = IntStream.range(0, 5)
                    .mapToObj(i -> new BankAccount("ACC" + i, 1000.0 * (i + 1), 0.01 * (i + 1)))
                    .collect(Collectors.toList());

            // 使用 allMatch（短路操作）验证所有账户余额 >= 1000
            boolean allHaveMinBalance = accounts.stream()
                    .allMatch(acc -> acc.getBalance() >= 1000.0);
            assertTrue(allHaveMinBalance);

            // 验证利率范围
            double maxRate = accounts.stream()
                    .mapToDouble(BankAccount::getInterestRate)
                    .max()
                    .orElse(0.0);
            assertEquals(0.05, maxRate, 0.0001);
        }
    }

    // ========== 边界条件测试 ==========

    @Nested
    @DisplayName("边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("零利率账户")
        void testZeroInterestRate() {
            BankAccount acc = new BankAccount("ZERO", 1000.0, 0.0);
            assertEquals(0.0, acc.getInterestRate(), 0.0001);
            double interest = acc.calculateInterest();
            assertEquals(0.0, interest, 0.001);
            assertEquals(1000.0, acc.getBalance(), 0.001);
        }

        @Test
        @DisplayName("大额交易")
        void testLargeTransaction() {
            account.deposit(1_000_000.0);
            assertEquals(1_001_000.0, account.getBalance(), 0.001);

            account.withdraw(500_000.0);
            assertEquals(501_000.0, account.getBalance(), 0.001);
        }

        @Test
        @DisplayName("极小利率")
        void testVerySmallInterestRate() {
            BankAccount acc = new BankAccount("TINY", 1000.0, 0.0001);
            double interest = acc.calculateInterest();
            assertEquals(0.1, interest, 0.001);
        }

        @Test
        @DisplayName("账户可为空账号")
        void testEmptyAccountId() {
            BankAccount acc = new BankAccount("", 100.0, 0.02);
            assertEquals("", acc.getAccountId());
            acc.printAccountInfo(); // 不应抛出异常
        }
    }
}
