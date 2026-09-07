package com.ems;

import java.util.Objects;

/**
 * 兼职员工类（PartTimeEmployee）
 * <p>
 * 继承自 {@link Employee}，代表兼职员工，薪资 = 工作时间 × 每小时工资。
 * </p>
 *
 * @author wyh
 * @since 1.0
 */
public class PartTimeEmployee extends Employee {

    /** 工作时间（小时） */
    private int workHours;

    /** 每小时工资 */
    private double hourlyWage;

    /**
     * 构造方法：初始化兼职员工的编号、姓名、部门、工作时间和时薪。
     *
     * @param empId      员工编号
     * @param empName    姓名
     * @param department 所属部门
     * @param workHours  工作时间（小时）
     * @param hourlyWage 每小时工资
     */
    public PartTimeEmployee(String empId, String empName, Department department,
                            int workHours, double hourlyWage) {
        super(empId, empName, department);
        this.workHours = workHours;
        this.hourlyWage = hourlyWage;
    }

    // ─── Getter / Setter ────────────────────────────────────────

    public int getWorkHours() {
        return workHours;
    }

    public void setWorkHours(int workHours) {
        this.workHours = workHours;
    }

    public double getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(double hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    // ─── 业务方法 ────────────────────────────────────────────────

    /**
     * 计算薪资：薪资 = 工作时间 × 每小时工资。
     *
     * @return 薪资总额
     */
    @Override
    public double calculateSalary() {
        return workHours * hourlyWage;
    }

    /**
     * 打印员工信息：先打印基础信息，再打印兼职专属的工作时间和时薪信息。
     */
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("员工类型：兼职员工");
        System.out.println("工作时间：" + workHours + " 小时");
        System.out.println("时薪：" + hourlyWage + " 元/小时");
        System.out.println("薪资合计：" + calculateSalary());
    }

    // ─── toString ───────────────────────────────────────────────

    @Override
    public String toString() {
        return "PartTimeEmployee{" +
                "empId='" + getEmpId() + '\'' +
                ", empName='" + getEmpName() + '\'' +
                ", workHours=" + workHours +
                ", hourlyWage=" + hourlyWage +
                ", salary=" + calculateSalary() +
                '}';
    }
}
