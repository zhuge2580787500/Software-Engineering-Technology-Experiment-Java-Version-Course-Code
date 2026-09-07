package com.ems;

import java.util.Objects;

/**
 * 抽象员工类（Employee）
 * <p>
 * 所有具体员工类型的基类，定义了员工共有的属性和行为。
 * 采用抽象类设计，因为：
 * <ul>
 *   <li>所有员工都有 empId、empName、department 等共同属性，适合抽取到父类</li>
 *   <li>calculateSalary() 的计算方式因员工类型而异，适合定义为抽象方法</li>
 *   <li>displayInfo() 的基础打印逻辑可以统一实现，子类可在此基础上扩展</li>
 * </ul>
 * </p>
 *
 * @author wyh
 * @since 1.0
 */
public abstract class Employee {

    /** 员工编号 */
    private final String empId;

    /** 姓名 */
    private String empName;

    /** 所属部门 */
    private Department department;

    /**
     * 构造方法：接收并初始化员工编号、姓名和所属部门。
     *
     * @param empId     员工编号
     * @param empName   姓名
     * @param department 所属部门
     */
    public Employee(String empId, String empName, Department department) {
        this.empId = empId;
        this.empName = empName;
        this.department = department;
    }

    // ─── Getter / Setter ────────────────────────────────────────

    public String getEmpId() {
        return empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    // ─── 抽象方法 ────────────────────────────────────────────────

    /**
     * 计算薪资（抽象方法，由各子类根据自身规则实现）。
     *
     * @return 薪资总额
     */
    public abstract double calculateSalary();

    // ─── 具体方法 ────────────────────────────────────────────────

    /**
     * 打印员工基础信息，格式为"属性名：属性值"，每个属性占一行。
     * 子类可通过 super.displayInfo() 调用此方法后再打印专属信息。
     */
    public void displayInfo() {
        System.out.println("员工编号：" + empId);
        System.out.println("姓名：" + empName);
        if (department != null) {
            System.out.println("所属部门：" + department.getDeptName() + "（" + department.getDeptId() + "）");
        } else {
            System.out.println("所属部门：未分配");
        }
    }

    // ─── equals / hashCode ──────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee that = (Employee) o;
        return Objects.equals(empId, that.empId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId='" + empId + '\'' +
                ", empName='" + empName + '\'' +
                ", department=" + (department != null ? department.getDeptName() : "null") +
                '}';
    }
}
