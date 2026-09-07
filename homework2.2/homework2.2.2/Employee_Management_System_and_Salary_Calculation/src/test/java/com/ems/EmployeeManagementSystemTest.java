package com.ems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 员工管理系统与薪资计算 — JUnit 5 测试套件
 * <p>
 * 覆盖以下测试场景：
 * <ul>
 *   <li>部门创建与属性验证</li>
 *   <li>员工添加与双向关联</li>
 *   <li>各类型员工薪资计算（全职、兼职、经理）</li>
 *   <li>经理团队管理奖金计算（含 instanceof 安全向下转型验证）</li>
 *   <li>系统总薪资汇总</li>
 *   <li>部门员工查询</li>
 *   <li>员工搜索（按编号/按姓名/查无此人）</li>
 * </ul>
 * </p>
 *
 * @author wyh
 * @since 1.0
 */
@DisplayName("员工管理系统与薪资计算")
class EmployeeManagementSystemTest {

    private Department rndDept;
    private EmployeeManagementSystem system;

    @BeforeEach
    void setUp() {
        // 每个测试前重新创建部门与系统，保证测试隔离
        rndDept = new Department("D01", "研发部");
        system = new EmployeeManagementSystem();
    }

    // ════════════════════════════════════════════════════════════
    // 部门类测试
    // ════════════════════════════════════════════════════════════

    @Test
    @DisplayName("部门创建：属性和 toString 正确")
    void testDepartmentCreation() {
        assertEquals("D01", rndDept.getDeptId());
        assertEquals("研发部", rndDept.getDeptName());
        assertNull(rndDept.getManager());
        assertTrue(rndDept.getEmployeeList().isEmpty());

        String deptStr = rndDept.toString();
        assertTrue(deptStr.contains("D01"));
        assertTrue(deptStr.contains("研发部"));
    }

    @Test
    @DisplayName("部门 addEmployee：员工加入部门列表")
    void testDepartmentAddEmployee() {
        FullTimeEmployee emp = new FullTimeEmployee("E001", "张三", rndDept, 5000, 1000);
        rndDept.addEmployee(emp);

        assertEquals(1, rndDept.getEmployeeList().size());
        assertSame(emp, rndDept.getEmployeeList().get(0));
    }

    @Test
    @DisplayName("部门 getEmployeeList 返回不可修改视图")
    void testDepartmentGetEmployeeListIsUnmodifiable() {
        FullTimeEmployee emp = new FullTimeEmployee("E001", "张三", rndDept, 5000, 1000);
        rndDept.addEmployee(emp);

        List<Employee> list = rndDept.getEmployeeList();
        assertThrows(UnsupportedOperationException.class, () -> list.add(emp));
    }

    @Test
    @DisplayName("部门 Stream API：统计全职员工数和奖金总和")
    void testDepartmentStreamMethods() {
        FullTimeEmployee emp1 = new FullTimeEmployee("E001", "张三", rndDept, 5000, 1000);
        FullTimeEmployee emp2 = new FullTimeEmployee("E002", "李四", rndDept, 6000, 2000);
        PartTimeEmployee emp3 = new PartTimeEmployee("E003", "王五", rndDept, 40, 80);

        rndDept.addEmployee(emp1);
        rndDept.addEmployee(emp2);
        rndDept.addEmployee(emp3);

        assertEquals(2, rndDept.countFullTimeEmployees());
        assertEquals(3000.0, rndDept.sumPersonalBonus(), 0.001);

        List<String> names = rndDept.getAllEmployeeNames();
        assertIterableEquals(List.of("张三", "李四", "王五"), names);
    }

    @Test
    @DisplayName("部门经理设置：双向关联一致性")
    void testDepartmentSetManager() {
        Manager mgr = new Manager("M001", "赵六", rndDept, 10000, 3000);
        rndDept.setManager(mgr);

        assertSame(mgr, rndDept.getManager());
        assertSame(rndDept, mgr.getDepartment());
    }

    // ════════════════════════════════════════════════════════════
    // 全职员工测试
    // ════════════════════════════════════════════════════════════

    @Test
    @DisplayName("全职员工：薪资 = 基本工资 + 个人奖金")
    void testFullTimeEmployeeSalary() {
        FullTimeEmployee emp = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);

        assertEquals(8000, emp.getBaseSalary());
        assertEquals(2000, emp.getPersonalBonus());
        assertEquals(10000.0, emp.calculateSalary(), 0.001);
    }

    @Test
    @DisplayName("全职员工：属性 getter/setter 正确")
    void testFullTimeEmployeeGettersSetters() {
        FullTimeEmployee emp = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);

        emp.setBaseSalary(9000);
        emp.setPersonalBonus(2500);
        emp.setEmpName("张三丰");

        assertEquals(9000, emp.getBaseSalary());
        assertEquals(2500, emp.getPersonalBonus());
        assertEquals("张三丰", emp.getEmpName());
    }

    @Test
    @DisplayName("全职员工：displayInfo 不抛异常")
    void testFullTimeEmployeeDisplayInfo() {
        FullTimeEmployee emp = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);
        assertDoesNotThrow(emp::displayInfo);
    }

    // ════════════════════════════════════════════════════════════
    // 兼职员工测试
    // ════════════════════════════════════════════════════════════

    @Test
    @DisplayName("兼职员工：薪资 = 工作时间 × 时薪")
    void testPartTimeEmployeeSalary() {
        PartTimeEmployee emp = new PartTimeEmployee("E003", "王五", rndDept, 80, 50);

        assertEquals(80, emp.getWorkHours());
        assertEquals(50.0, emp.getHourlyWage(), 0.001);
        assertEquals(4000.0, emp.calculateSalary(), 0.001);
    }

    @Test
    @DisplayName("兼职员工：零工时薪资为 0")
    void testPartTimeEmployeeZeroHours() {
        PartTimeEmployee emp = new PartTimeEmployee("E003", "王五", rndDept, 0, 50);
        assertEquals(0.0, emp.calculateSalary(), 0.001);
    }

    @Test
    @DisplayName("兼职员工：setter 设置正确")
    void testPartTimeEmployeeSetters() {
        PartTimeEmployee emp = new PartTimeEmployee("E003", "王五", rndDept, 80, 50);

        emp.setWorkHours(100);
        emp.setHourlyWage(60);

        assertEquals(100, emp.getWorkHours());
        assertEquals(60.0, emp.getHourlyWage(), 0.001);
        assertEquals(6000.0, emp.calculateSalary(), 0.001);
    }

    @Test
    @DisplayName("兼职员工：displayInfo 不抛异常")
    void testPartTimeEmployeeDisplayInfo() {
        PartTimeEmployee emp = new PartTimeEmployee("E003", "王五", rndDept, 80, 50);
        assertDoesNotThrow(emp::displayInfo);
    }

    // ════════════════════════════════════════════════════════════
    // 经理类测试
    // ════════════════════════════════════════════════════════════

    @Test
    @DisplayName("经理：薪资 = 基本工资 + 个人奖金 + 团队管理奖金(10%)")
    void testManagerSalaryCalculation() {
        // 创建部门经理（基本工资12000，个人奖金5000）
        Manager mgr = new Manager("M001", "赵六", rndDept, 12000, 5000);

        // 手动添加经理到部门（模拟 addEmployee 的行为）
        rndDept.addEmployee(mgr);
        rndDept.setManager(mgr);

        // 经理薪资 = 12000 + 5000 + (0 × 10%) = 17000
        // （部门中只有经理自己，无其他全职员工，团队奖金为 0）
        assertEquals(17000.0, mgr.calculateSalary(), 0.001);
    }

    @Test
    @DisplayName("经理：团队管理奖金 = 团队成员个人奖金之和 × 10%")
    void testManagerTeamBonus() {
        // 创建经理
        Manager mgr = new Manager("M001", "赵六", rndDept, 12000, 5000);

        // 创建 2 名全职员工（E001 奖金2000，E002 奖金1500）并加入部门
        FullTimeEmployee emp1 = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);
        FullTimeEmployee emp2 = new FullTimeEmployee("E002", "李四", rndDept, 7500, 1500);

        rndDept.addEmployee(emp1);
        rndDept.addEmployee(emp2);
        rndDept.addEmployee(mgr);
        rndDept.setManager(mgr);

        // 手动核算：
        // 团队个人奖金合计 = 2000 + 1500 = 3500
        // 团队管理奖金 = 3500 × 10% = 350
        // 经理总奖金 = 5000 + 350 = 5350
        // 经理薪资 = 12000 + 5350 = 17350

        // 必须先调用 calculateBonus() 触发团队奖金计算，再读取 teamManagementBonus 字段
        double bonus = mgr.calculateBonus();
        double salary = mgr.calculateSalary();

        assertEquals(350.0, mgr.getTeamManagementBonus(), 0.001,
                "团队管理奖金应为 350.0（(2000+1500)×10%）");
        assertEquals(5350.0, bonus, 0.001,
                "总奖金应为 5350.0（5000+350）");
        assertEquals(17350.0, salary, 0.001,
                "经理薪资应为 17350.0（12000+5350）");
    }

    @Test
    @DisplayName("经理：兼职员工不影响团队奖金计算（instanceof 安全）")
    void testManagerBonusExcludesPartTime() {
        Manager mgr = new Manager("M001", "赵六", rndDept, 12000, 5000);

        FullTimeEmployee emp1 = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);
        PartTimeEmployee emp2 = new PartTimeEmployee("E003", "王五", rndDept, 80, 50);

        rndDept.addEmployee(emp1);
        rndDept.addEmployee(emp2);
        rndDept.addEmployee(mgr);
        rndDept.setManager(mgr);

        // 团队奖金只来自全职员工（2000），兼职员工不计入
        // 团队管理奖金 = 2000 × 10% = 200
        // 必须先调用 calculateBonus() 触发计算
        double bonus = mgr.calculateBonus();

        assertEquals(200.0, mgr.getTeamManagementBonus(), 0.001,
                "团队管理奖金应为 200.0（兼职员工不计入）");
        assertEquals(5200.0, bonus, 0.001,
                "总奖金应为 5200.0（5000+200）");
    }

    @Test
    @DisplayName("经理：无部门时不计算团队奖金，不抛异常")
    void testManagerWithoutDepartment() {
        Manager mgr = new Manager("M001", "赵六", null, 10000, 3000);

        // 没有部门的经理，calculateBonus 应安全返回个人奖金
        assertEquals(3000.0, mgr.calculateBonus(), 0.001);
        assertEquals(13000.0, mgr.calculateSalary(), 0.001);
    }

    @Test
    @DisplayName("经理：displayInfo 不抛异常")
    void testManagerDisplayInfo() {
        Manager mgr = new Manager("M001", "赵六", rndDept, 12000, 5000);
        FullTimeEmployee emp = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);
        rndDept.addEmployee(emp);
        rndDept.addEmployee(mgr);
        rndDept.setManager(mgr);

        assertDoesNotThrow(mgr::displayInfo);
    }

    // ════════════════════════════════════════════════════════════
    // 系统管理类测试
    // ════════════════════════════════════════════════════════════

    @Test
    @DisplayName("系统：addEmployee 添加员工并同步到部门")
    void testSystemAddEmployee() {
        FullTimeEmployee emp = new FullTimeEmployee("E001", "张三", rndDept, 5000, 1000);

        system.addEmployee(emp);

        assertEquals(1, system.getEmployeeCount());
        assertEquals(1, rndDept.getEmployeeList().size());
        assertSame(emp, system.getAllEmployees().get(0));
    }

    @Test
    @DisplayName("系统：重复添加同一员工不重复录入")
    void testSystemAddDuplicateEmployee() {
        FullTimeEmployee emp = new FullTimeEmployee("E001", "张三", rndDept, 5000, 1000);

        system.addEmployee(emp);
        system.addEmployee(emp); // 重复添加

        assertEquals(1, system.getEmployeeCount());
        assertEquals(1, rndDept.getEmployeeList().size());
    }

    @Test
    @DisplayName("系统：addEmployee null 值抛出 NullPointerException")
    void testSystemAddNullEmployee() {
        assertThrows(NullPointerException.class, () -> system.addEmployee(null));
    }

    @Test
    @DisplayName("系统：calculateTotalSalary 计算公司总薪资（多态验证）")
    void testSystemCalculateTotalSalary() {
        FullTimeEmployee emp1 = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);
        FullTimeEmployee emp2 = new FullTimeEmployee("E002", "李四", rndDept, 7500, 1500);
        PartTimeEmployee emp3 = new PartTimeEmployee("E003", "王五", rndDept, 80, 50);
        Manager mgr = new Manager("M001", "赵六", rndDept, 12000, 5000);

        rndDept.setManager(mgr);

        system.addEmployee(emp1);
        system.addEmployee(emp2);
        system.addEmployee(emp3);
        system.addEmployee(mgr);

        // 预期总薪资 = 10000 + 9000 + 4000 + 17350 = 40350
        double expected = 10000 + 9000 + 4000 + 17350;
        assertEquals(expected, system.calculateTotalSalary(), 0.001,
                "公司总薪资应为 40350.0");
    }

    @Test
    @DisplayName("系统：calculateTotalSalary 空系统返回 0")
    void testSystemCalculateTotalSalaryEmpty() {
        assertEquals(0.0, system.calculateTotalSalary(), 0.001);
    }

    // ════════════════════════════════════════════════════════════
    // 部门查询测试
    // ════════════════════════════════════════════════════════════

    @Test
    @DisplayName("系统：printDeptEmployees 按部门编号查询员工")
    void testPrintDeptEmployees() {
        FullTimeEmployee emp1 = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);
        FullTimeEmployee emp2 = new FullTimeEmployee("E002", "李四", rndDept, 7500, 1500);
        PartTimeEmployee emp3 = new PartTimeEmployee("E003", "王五", rndDept, 80, 50);
        Manager mgr = new Manager("M001", "赵六", rndDept, 12000, 5000);

        rndDept.setManager(mgr);

        system.addEmployee(emp1);
        system.addEmployee(emp2);
        system.addEmployee(emp3);
        system.addEmployee(mgr);

        // 验证 D01 部门有 4 名员工
        assertEquals(4, system.getEmployeeCount());
        // 不抛异常即视为通过
        assertDoesNotThrow(() -> system.printDeptEmployees("D01"));
    }

    @Test
    @DisplayName("系统：printDeptEmployees 查询不存在的部门")
    void testPrintDeptEmployeesNotFound() {
        assertDoesNotThrow(() -> system.printDeptEmployees("D99"));
    }

    @Test
    @DisplayName("系统：printDeptEmployees null 参数抛出异常")
    void testPrintDeptEmployeesNull() {
        assertThrows(NullPointerException.class, () -> system.printDeptEmployees(null));
    }

    // ════════════════════════════════════════════════════════════
    // 员工搜索测试
    // ════════════════════════════════════════════════════════════

    @Test
    @DisplayName("系统：findAndPrintEmployee 按编号查找")
    void testFindByEmpId() {
        FullTimeEmployee emp = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);
        system.addEmployee(emp);

        // 通过编号查找应成功
        assertDoesNotThrow(() -> system.findAndPrintEmployee("E001"));
    }

    @Test
    @DisplayName("系统：findAndPrintEmployee 按姓名查找")
    void testFindByEmpName() {
        FullTimeEmployee emp = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);
        system.addEmployee(emp);

        assertDoesNotThrow(() -> system.findAndPrintEmployee("张三"));
    }

    @Test
    @DisplayName("系统：findAndPrintEmployee 查找不存在的员工")
    void testFindNonExistent() {
        assertDoesNotThrow(() -> system.findAndPrintEmployee("E999"));
    }

    @Test
    @DisplayName("系统：findAndPrintEmployee 空系统查找")
    void testFindInEmptySystem() {
        assertDoesNotThrow(() -> system.findAndPrintEmployee("E001"));
    }

    @Test
    @DisplayName("系统：findAndPrintEmployee 不区分大小写")
    void testFindCaseInsensitive() {
        Manager mgr = new Manager("M001", "赵六", rndDept, 12000, 5000);
        system.addEmployee(mgr);

        // 小写也应该能找到
        assertDoesNotThrow(() -> system.findAndPrintEmployee("m001"));
        assertDoesNotThrow(() -> system.findAndPrintEmployee("赵"));
    }

    // ════════════════════════════════════════════════════════════
    // Stream API 统计方法测试
    // ════════════════════════════════════════════════════════════

    @Test
    @DisplayName("系统：countFullTimeEmployees Stream API 统计")
    void testCountFullTimeEmployees() {
        system.addEmployee(new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000));
        system.addEmployee(new FullTimeEmployee("E002", "李四", rndDept, 7500, 1500));
        system.addEmployee(new PartTimeEmployee("E003", "王五", rndDept, 80, 50));
        system.addEmployee(new Manager("M001", "赵六", rndDept, 12000, 5000));

        assertEquals(3, system.countFullTimeEmployees());
    }

    @Test
    @DisplayName("系统：countPartTimeEmployees Stream API 统计")
    void testCountPartTimeEmployees() {
        system.addEmployee(new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000));
        system.addEmployee(new PartTimeEmployee("E003", "王五", rndDept, 80, 50));

        assertEquals(1, system.countPartTimeEmployees());
    }

    @Test
    @DisplayName("系统：getAllEmployeeNames Stream API 方法引用")
    void testGetAllEmployeeNames() {
        FullTimeEmployee emp1 = new FullTimeEmployee("E001", "张三", rndDept, 8000, 2000);
        FullTimeEmployee emp2 = new FullTimeEmployee("E002", "李四", rndDept, 7500, 1500);
        system.addEmployee(emp1);
        system.addEmployee(emp2);

        List<String> names = system.getAllEmployeeNames();
        assertIterableEquals(List.of("张三", "李四"), names);
    }

    // ════════════════════════════════════════════════════════════
    // 综合场景测试
    // ════════════════════════════════════════════════════════════

    @Test
    @DisplayName("综合场景：完整业务流程验证")
    void testFullWorkflow() {
        // 1. 创建部门和系统
        Department hrDept = new Department("D02", "人力资源部");
        EmployeeManagementSystem hrSystem = new EmployeeManagementSystem();

        // 2. 录入员工
        FullTimeEmployee hr1 = new FullTimeEmployee("E101", "小明", hrDept, 6000, 1000);
        PartTimeEmployee hr2 = new PartTimeEmployee("E102", "小红", hrDept, 60, 40);
        Manager hrMgr = new Manager("M101", "老张", hrDept, 10000, 3000);

        hrDept.setManager(hrMgr);
        hrSystem.addEmployee(hr1);
        hrSystem.addEmployee(hr2);
        hrSystem.addEmployee(hrMgr);

        // 3. 验证部门数据
        assertEquals(3, hrSystem.getEmployeeCount());
        assertEquals(2, hrDept.countFullTimeEmployees());
        assertEquals(1, hrSystem.countPartTimeEmployees());

        // 4. 验证薪资
        assertEquals(7000.0, hr1.calculateSalary(), 0.001);   // 6000+1000
        assertEquals(2400.0, hr2.calculateSalary(), 0.001);   // 60×40
        // 经理：10000 + 3000 + (1000 × 10%) = 13100
        assertEquals(13100.0, hrMgr.calculateSalary(), 0.001);

        // 5. 验证总薪资 = 7000 + 2400 + 13100 = 22500
        assertEquals(22500.0, hrSystem.calculateTotalSalary(), 0.001);

        // 6. 验证部门查询
        assertDoesNotThrow(() -> hrSystem.printDeptEmployees("D02"));

        // 7. 验证搜索
        assertDoesNotThrow(() -> hrSystem.findAndPrintEmployee("E102"));
        assertDoesNotThrow(() -> hrSystem.findAndPrintEmployee("老张"));
    }
}
