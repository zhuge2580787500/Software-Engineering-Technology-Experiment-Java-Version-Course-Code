package com.ems;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 员工管理系统（EmployeeManagementSystem）
 * <p>
 * 用于统一调度和管理系统中所有员工的类，提供人员录入、薪资汇总、
 * 部门员工查询、员工搜索等功能。
 * </p>
 *
 * @author wyh
 * @since 1.0
 */
public class EmployeeManagementSystem {

    /** 系统总员工集合 */
    private final List<Employee> allEmployees;

    /**
     * 构造方法：初始化空的员工列表。
     */
    public EmployeeManagementSystem() {
        this.allEmployees = new java.util.ArrayList<>();
    }

    // ─── 核心方法 ────────────────────────────────────────────────

    /**
     * 将员工加入系统，同时如果员工有部门，调用部门的 addEmployee 方法
     * 将员工加入部门名单，保持系统与部门数据的一致性。
     *
     * @param emp 待录入的员工
     */
    public void addEmployee(Employee emp) {
        Objects.requireNonNull(emp, "员工不能为 null");
        if (!allEmployees.contains(emp)) {
            allEmployees.add(emp);
            // 如果员工有部门，同时加入部门名单
            if (emp.getDepartment() != null) {
                emp.getDepartment().addEmployee(emp);
            }
        }
    }

    /**
     * 遍历系统全体员工，利用多态调用各自的 calculateSalary() 方法，
     * 计算并返回公司总薪资开销。
     * <p>
     * 使用 Java 8 Stream API 实现，展示函数式编程风格。
     * </p>
     *
     * @return 公司总薪资开销
     */
    public double calculateTotalSalary() {
        return allEmployees.stream()
                .mapToDouble(Employee::calculateSalary)   // 方法引用，多态调用
                .sum();
    }

    /**
     * 根据部门编号，遍历打印该部门本级全体员工的信息。
     *
     * @param deptId 部门编号
     */
    public void printDeptEmployees(String deptId) {
        Objects.requireNonNull(deptId, "部门编号不能为 null");

        List<Employee> deptEmployees = allEmployees.stream()
                .filter(e -> e.getDepartment() != null)
                .filter(e -> deptId.equals(e.getDepartment().getDeptId()))
                .collect(Collectors.toList());

        if (deptEmployees.isEmpty()) {
            System.out.println("【" + deptId + "】部门暂无员工记录。");
            return;
        }

        System.out.println("========== 部门 [" + deptId + "] 员工列表 ==========");
        deptEmployees.forEach(emp -> {
            System.out.println("──────────────────────────────");
            emp.displayInfo();
        });
        System.out.println("==========================================");
        System.out.println("该部门共 " + deptEmployees.size() + " 名员工。\n");
    }

    /**
     * 根据员工编号或姓名进行查找，若找到则调用 displayInfo()，
     * 未找到则提示"查无此人"。
     * <p>
     * 使用 Java 8 Stream API + Optional 实现，展示函数式风格。
     * </p>
     *
     * @param keyword 员工编号或姓名的关键词
     */
    public void findAndPrintEmployee(String keyword) {
        Objects.requireNonNull(keyword, "搜索关键词不能为 null");

        Optional<Employee> result = allEmployees.stream()
                .filter(e -> keyword.equalsIgnoreCase(e.getEmpId())
                        || keyword.equalsIgnoreCase(e.getEmpName()))
                .findFirst();

        if (result.isPresent()) {
            System.out.println("✅ 查找成功，员工信息如下：");
            System.out.println("──────────────────────────────");
            result.get().displayInfo();
            System.out.println("──────────────────────────────\n");
        } else {
            System.out.println("❌ 查无此人：未找到编号或姓名包含 \"" + keyword + "\" 的员工。\n");
        }
    }

    // ─── Java 8+ Stream API 扩展方法 ─────────────────────────────

    /**
     * 统计系统中全职员工的数量（使用 Stream API）。
     *
     * @return 全职员工数量
     */
    public long countFullTimeEmployees() {
        return allEmployees.stream()
                .filter(e -> e instanceof FullTimeEmployee)
                .count();
    }

    /**
     * 统计系统中兼职员工的数量（使用 Stream API）。
     *
     * @return 兼职员工数量
     */
    public long countPartTimeEmployees() {
        return allEmployees.stream()
                .filter(e -> e instanceof PartTimeEmployee)
                .count();
    }

    /**
     * 获取系统中所有员工的姓名列表（使用 Stream API + 方法引用）。
     *
     * @return 员工姓名列表
     */
    public List<String> getAllEmployeeNames() {
        return allEmployees.stream()
                .map(Employee::getEmpName)
                .collect(Collectors.toList());
    }

    /**
     * 获取系统中员工数量。
     *
     * @return 员工总数
     */
    public int getEmployeeCount() {
        return allEmployees.size();
    }

    /**
     * 获取所有员工列表（返回不可修改视图）。
     *
     * @return 不可修改的员工列表
     */
    public List<Employee> getAllEmployees() {
        return java.util.Collections.unmodifiableList(allEmployees);
    }
}
