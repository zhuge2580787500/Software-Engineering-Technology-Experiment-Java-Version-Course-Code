package com.ems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 部门类（Department）
 * <p>
 * 表示公司内的一个部门，包含部门编号、名称、部门经理以及该部门的全体员工列表。
 * </p>
 *
 * @author wyh
 * @since 1.0
 */
public class Department {

    /** 部门编号 */
    private final String deptId;

    /** 部门名称 */
    private String deptName;

    /** 部门经理 */
    private Manager manager;

    /** 本部门全体员工列表 */
    private final List<Employee> employeeList;

    /**
     * 构造方法：初始化部门编号和名称，并实例化员工列表。
     *
     * @param deptId   部门编号
     * @param deptName 部门名称
     */
    public Department(String deptId, String deptName) {
        this.deptId = deptId;
        this.deptName = deptName;
        this.employeeList = new ArrayList<>();
    }

    // ─── Getter / Setter ────────────────────────────────────────

    public String getDeptId() {
        return deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Manager getManager() {
        return manager;
    }

    /**
     * 设置部门经理。
     * <p>
     * 设置经理时会自动将经理的所属部门关联到本部门，同时将经理加入部门员工列表，
     * 保证双向关联的一致性。
     * </p>
     *
     * @param manager 经理对象
     */
    public void setManager(Manager manager) {
        this.manager = manager;
        if (manager != null) {
            manager.setDepartment(this);
            // 确保经理也在部门员工列表中（避免 calculateBonus 遍历时漏掉）
            if (!employeeList.contains(manager)) {
                employeeList.add(manager);
            }
        }
    }

    /**
     * 获取员工列表的不可修改视图，防止外部直接修改部门内部列表。
     *
     * @return 不可修改的员工列表
     */
    public List<Employee> getEmployeeList() {
        return Collections.unmodifiableList(employeeList);
    }

    // ─── 业务方法 ────────────────────────────────────────────────

    /**
     * 将员工加入部门名单。
     *
     * @param emp 待添加的员工
     */
    public void addEmployee(Employee emp) {
        Objects.requireNonNull(emp, "员工不能为 null");
        if (!employeeList.contains(emp)) {
            employeeList.add(emp);
        }
    }

    // ─── Java 8+ Stream API 扩展方法 ─────────────────────────────

    /**
     * 使用 Stream API 统计部门中全职员工的人数。
     *
     * @return 全职员工数量
     */
    public long countFullTimeEmployees() {
        return employeeList.stream()
                .filter(e -> e instanceof FullTimeEmployee)
                .count();
    }

    /**
     * 使用 Stream API 获取部门中所有全职员工的个人奖金之和。
     *
     * @return 个人奖金总和
     */
    public double sumPersonalBonus() {
        return employeeList.stream()
                .filter(e -> e instanceof FullTimeEmployee)
                .mapToDouble(e -> ((FullTimeEmployee) e).getPersonalBonus())
                .sum();
    }

    /**
     * 使用 Stream API 获取部门中所有员工的姓名列表。
     *
     * @return 员工姓名列表
     */
    public List<String> getAllEmployeeNames() {
        return employeeList.stream()
                .map(Employee::getEmpName)
                .collect(Collectors.toList());
    }

    // ─── toString ───────────────────────────────────────────────

    @Override
    public String toString() {
        String mgr = (manager != null) ? manager.getEmpName() : "未设置";
        return "Department{" +
                "deptId='" + deptId + '\'' +
                ", deptName='" + deptName + '\'' +
                ", manager=" + mgr +
                ", employeeCount=" + employeeList.size() +
                '}';
    }
}
