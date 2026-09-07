package com.ems;

import java.util.Objects;

/**
 * 经理类（Manager）
 * <p>
 * 继承自 {@link FullTimeEmployee}，同时实现 {@link BonusCalculable} 接口。
 * <p>
 * 薪资计算规则：
 * <ul>
 *   <li>总奖金 = 个人奖金 + 团队管理奖金</li>
 *   <li>团队管理奖金 = 所管理部门本级全体员工（不含经理自己）个人奖金之和 × 10%</li>
 * </ul>
 * 经理薪资 = 基本工资 + 总奖金。
 * </p>
 *
 * @author wyh
 * @since 1.0
 */
public class Manager extends FullTimeEmployee implements BonusCalculable {

    /** 团队管理奖金 */
    private double teamManagementBonus;

    /**
     * 构造方法：初始化经理的员工编号、姓名、部门、基本工资和个人奖金。
     * 团队管理奖金初始为 0，将在首次调用 calculateBonus() 时动态计算。
     *
     * @param empId        员工编号
     * @param empName      姓名
     * @param department   所属部门
     * @param baseSalary   基本工资
     * @param personalBonus 个人奖金
     */
    public Manager(String empId, String empName, Department department,
                   double baseSalary, double personalBonus) {
        super(empId, empName, department, baseSalary, personalBonus);
        this.teamManagementBonus = 0.0;
    }

    // ─── Getter / Setter ────────────────────────────────────────

    public double getTeamManagementBonus() {
        return teamManagementBonus;
    }

    public void setTeamManagementBonus(double teamManagementBonus) {
        this.teamManagementBonus = teamManagementBonus;
    }

    // ─── BonusCalculable 接口实现 ───────────────────────────────

    /**
     * 计算总奖金。
     * <p>
     * 核心逻辑：
     * <ol>
     *   <li>遍历经理所管理部门的所有员工</li>
     *   <li>使用 instanceof 判断员工是否为 FullTimeEmployee（排除经理自己和兼职员工）</li>
     *   <li>累加所有全职员工的个人奖金</li>
     *   <li>团队管理奖金 = 累加结果 × 10%</li>
     *   <li>总奖金 = 个人奖金（继承自 FullTimeEmployee）+ 团队管理奖金</li>
     * </ol>
     * </p>
     *
     * @return 总奖金
     */
    @Override
    public double calculateBonus() {
        if (getDepartment() == null || getDepartment().getEmployeeList().isEmpty()) {
            this.teamManagementBonus = 0.0;
            return getPersonalBonus();
        }

        // 使用 Stream API + instanceof 过滤出部门中的全职员工（排除经理自己）
        // 安全向下转型：先判断 instanceof，再转换为 FullTimeEmployee
        double totalPersonalBonus = getDepartment().getEmployeeList().stream()
                .filter(e -> e != this)                            // 排除经理自己
                .filter(e -> e instanceof FullTimeEmployee)       // instanceof 安全检查
                .mapToDouble(e -> ((FullTimeEmployee) e).getPersonalBonus()) // 安全向下转型
                .sum();

        this.teamManagementBonus = totalPersonalBonus * 0.1;
        return getPersonalBonus() + this.teamManagementBonus;
    }

    // ─── 重写薪资计算 ────────────────────────────────────────────

    /**
     * 重写薪资计算：经理薪资 = 基本工资 + 总奖金（通过 calculateBonus() 计算）。
     *
     * @return 薪资总额
     */
    @Override
    public double calculateSalary() {
        return getBaseSalary() + calculateBonus();
    }

    // ─── 重写信息打印 ────────────────────────────────────────────

    /**
     * 打印员工信息：先打印基础信息，再打印全职专属信息，最后打印经理专属的奖金信息。
     */
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("员工类型：经理");
        System.out.println("个人奖金：" + getPersonalBonus());
        System.out.println("团队管理奖金：" + String.format("%.2f", teamManagementBonus));
        System.out.println("总奖金：" + String.format("%.2f", calculateBonus()));
        System.out.println("薪资合计：" + calculateSalary());
    }

    // ─── toString ───────────────────────────────────────────────

    @Override
    public String toString() {
        return "Manager{" +
                "empId='" + getEmpId() + '\'' +
                ", empName='" + getEmpName() + '\'' +
                ", baseSalary=" + getBaseSalary() +
                ", personalBonus=" + getPersonalBonus() +
                ", teamManagementBonus=" + String.format("%.2f", teamManagementBonus) +
                ", salary=" + calculateSalary() +
                '}';
    }
}
