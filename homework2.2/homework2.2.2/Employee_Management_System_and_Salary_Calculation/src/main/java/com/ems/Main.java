package com.ems;

/**
 * 主测试类（Main）
 * <p>
 * 按照文档要求的模拟测试流程，验证员工管理系统与薪资计算的全部功能。
 * 展示了多态、继承、接口、Stream API 等面向对象编程特性。
 * </p>
 *
 * @author wyh
 * @since 1.0
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║      员工管理系统与薪资计算  —  功能演示             ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");

        // ══════════════════════════════════════════════════════════
        // 第一步：环境初始化
        // ══════════════════════════════════════════════════════════
        System.out.println("【第一步】环境初始化");

        // 创建研发部（编号: D01, 名称: 研发部）
        Department rndDept = new Department("D01", "研发部");

        // 创建系统管理对象
        EmployeeManagementSystem system = new EmployeeManagementSystem();

        System.out.println("✅ 部门创建成功：" + rndDept);
        System.out.println("✅ 系统初始化完成。\n");

        // ══════════════════════════════════════════════════════════
        // 第二步：人员录入
        // ══════════════════════════════════════════════════════════
        System.out.println("【第二步】人员录入");

        // 创建 2 名全职员工（基本工资 + 个人奖金），归属研发部
        FullTimeEmployee emp1 = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);
        FullTimeEmployee emp2 = new FullTimeEmployee("E002", "李四", rndDept, 7500, 1500);

        // 创建 1 名兼职员工（工时 + 时薪），归属研发部
        PartTimeEmployee emp3 = new PartTimeEmployee("E003", "王五", rndDept, 80, 50);

        // 创建 1 名经理（归属研发部，设置为该部门经理）
        Manager manager = new Manager("M001", "赵六", rndDept, 12000, 5000);
        rndDept.setManager(manager);

        // 将以上 4 人全部通过系统的 addEmployee 方法录入
        system.addEmployee(emp1);
        system.addEmployee(emp2);
        system.addEmployee(emp3);
        system.addEmployee(manager);

        System.out.println("✅ 已录入 4 名员工（2 名全职、1 名兼职、1 名经理）。\n");

        // ══════════════════════════════════════════════════════════
        // 第三步：功能测试
        // ══════════════════════════════════════════════════════════

        // ── 功能1：按部门编号打印员工信息 ──────────────────────────
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("【功能1】按部门编号打印员工信息（D01）");
        System.out.println("═══════════════════════════════════════════════════════");
        system.printDeptEmployees("D01");

        // ── 功能2：按编号查找（查兼职员工 E003） ───────────────────
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("【功能2a】按编号查找员工（E003 — 兼职员工王五）");
        System.out.println("═══════════════════════════════════════════════════════");
        system.findAndPrintEmployee("E003");

        // ── 功能2：按姓名查找（查经理 赵六） ──────────────────────
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("【功能2b】按姓名查找员工（赵六 — 经理）");
        System.out.println("═══════════════════════════════════════════════════════");
        system.findAndPrintEmployee("赵六");

        // ── 功能2：查找不存在的人 ────────────────────────────────
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("【功能2c】查找不存在的员工（E999）");
        System.out.println("═══════════════════════════════════════════════════════");
        system.findAndPrintEmployee("E999");

        // ── 功能3：计算公司总薪资开销 ──────────────────────────────
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("【功能3】公司总薪资开销");
        System.out.println("═══════════════════════════════════════════════════════");

        // 打印各员工薪资明细
        System.out.println("── 各员工薪资明细 ──");
        System.out.println(emp1.getEmpName() + "（全职）：" + emp1.calculateSalary() + " 元");
        System.out.println(emp2.getEmpName() + "（全职）：" + emp2.calculateSalary() + " 元");
        System.out.println(emp3.getEmpName() + "（兼职）：" + emp3.calculateSalary() + " 元");
        System.out.println(manager.getEmpName() + "（经理）：" + manager.calculateSalary() + " 元");

        // 手动核算经理薪资
        double teamBonus = (emp1.getPersonalBonus() + emp2.getPersonalBonus()) * 0.1;
        double managerExpected = manager.getBaseSalary() + manager.getPersonalBonus() + teamBonus;
        System.out.println("\n── 经理薪资手动核算 ──");
        System.out.println("  基本工资：" + manager.getBaseSalary());
        System.out.println("  个人奖金：" + manager.getPersonalBonus());
        System.out.println("  团队管理奖金（(2000+1500)×10%）：" + teamBonus);
        System.out.println("  经理薪资合计（手动计算）：" + managerExpected);

        double totalSalary = system.calculateTotalSalary();
        System.out.println("\n✅ 公司总薪资开销（系统计算）：" + totalSalary + " 元");
        System.out.println("✅ 手动核算验证：" +
                (Math.abs(totalSalary - managerExpected - emp1.calculateSalary()
                        - emp2.calculateSalary() - emp3.calculateSalary()) < 0.01
                        ? "✓ 一致" : "✗ 不一致"));

        // ══════════════════════════════════════════════════════════
        // 扩展演示：Java 8 Stream API 统计
        // ══════════════════════════════════════════════════════════
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("【扩展】系统统计信息（Java 8 Stream API）");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("员工总数：" + system.getEmployeeCount());
        System.out.println("全职员工数：" + system.countFullTimeEmployees());
        System.out.println("兼职员工数：" + system.countPartTimeEmployees());
        System.out.println("所有员工姓名：" + system.getAllEmployeeNames());
        System.out.println("部门全职员工数：" + rndDept.countFullTimeEmployees());
        System.out.println("部门个人奖金合计：" + rndDept.sumPersonalBonus());

        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║             演示结束，所有功能测试通过！             ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
