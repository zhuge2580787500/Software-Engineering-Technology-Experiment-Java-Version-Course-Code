package com.ems;

import java.util.Objects;

/**
 * 全职员工类（FullTimeEmployee）
 * <p>
 * 继承自 {@link Employee}，代表全职员工，薪资由基本工资和个人奖金组成。
 * </p>
 *
 * @author wyh
 * @since 1.0
 */
public class FullTimeEmployee extends Employee {

    /** 基本工资 */
    private double baseSalary;

    /** 个人奖金 */
    private double personalBonus;

    /**
     * 构造方法：初始化全职员工的编号、姓名、部门、基本工资和个人奖金。
     *
     * @param empId        员工编号
     * @param empName      姓名
     * @param department   所属部门
     * @param baseSalary   基本工资
     * @param personalBonus 个人奖金
     */
    public FullTimeEmployee(String empId, String empName, Department department,
                            double baseSalary, double personalBonus) {
        super(empId, empName, department);
        this.baseSalary = baseSalary;
        this.personalBonus = personalBonus;
    }

    // ─── Getter / Setter ────────────────────────────────────────

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    /**
     * 获取个人奖金。
     *
     * @return 个人奖金
     */
    public double getPersonalBonus() {
        return personalBonus;
    }

    public void setPersonalBonus(double personalBonus) {
        this.personalBonus = personalBonus;
    }

    // ─── 业务方法 ────────────────────────────────────────────────

    /**
     * 计算薪资：薪资 = 基本工资 + 个人奖金。
     *
     * @return 薪资总额
     */
    @Override
    public double calculateSalary() {
        return baseSalary + personalBonus;
    }

    /**
     * 打印员工信息：先打印基础信息，再打印全职专属的工资和奖金信息。
     */
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("员工类型：全职员工");
        System.out.println("基本工资：" + baseSalary);
        System.out.println("个人奖金：" + personalBonus);
        System.out.println("薪资合计：" + calculateSalary());
    }

    // ─── toString ───────────────────────────────────────────────

    @Override
    public String toString() {
        return "FullTimeEmployee{" +
                "empId='" + getEmpId() + '\'' +
                ", empName='" + getEmpName() + '\'' +
                ", baseSalary=" + baseSalary +
                ", personalBonus=" + personalBonus +
                ", salary=" + calculateSalary() +
                '}';
    }
}
