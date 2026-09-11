/**
 * ============================================================
 * 深空探测舰队 - 异星环境分析控制台 V1.0
 * 主入口类 ({@code Main})
 * ============================================================
 * <p>
 * 本系统是深空探索舰队在 Kepler-452b 部署的异星环境数据分析系统，
 * 用于加载、清洗、分析探测车传回的遥测 CSV 数据，并在检测到辐射
 * 风暴危机时自动触发告警和指令下发。
 * </p>
 * <p>
 * 设计模式总览：
 * </p>
 * <ol>
 *   <li>{@code Factory Pattern}   : {@code RoverFactory} + {@code Rover} 及其子类</li>
 *   <li>{@code Strategy Pattern}  : {@code DataCleaningStrategy} + 两种清洗策略</li>
 *   <li>{@code Observer Pattern}  : {@code DataAnalyzer} (Subject) + 三个 {@code AlertObserver}</li>
 *   <li>{@code Command Pattern}   : {@code RoverCommand} + {@code ShieldCommand}/{@code MoveCommand}</li>
 * </ol>
 *
 * <h3>编译与运行：</h3>
 * <pre>
 *   mvn clean compile
 *   mvn exec:java -Dexec.mainClass="com.deepspace.Main"
 * </pre>
 * ============================================================
 */
package com.deepspace;

import com.deepspace.command.CommandCenter;
import com.deepspace.command.ShieldCommand;
import com.deepspace.observer.AlertObserver;
import com.deepspace.observer.CommandCenterObserver;
import com.deepspace.observer.ConsoleAlertObserver;
import com.deepspace.observer.DataAnalyzer;
import com.deepspace.observer.LogFileObserver;
import com.deepspace.strategy.DataCleaningStrategy;
import com.deepspace.strategy.DropInvalidStrategy;
import com.deepspace.strategy.SmartFillStrategy;
import com.deepspace.util.CsvDataLoader;
import com.deepspace.util.JsonUtil;
import com.deepspace.factory.TelemetryRecord;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    // ========== 系统状态 ==========

    /** 清洗后的有效数据 (加载后非 null) */
    private static List<TelemetryRecord> cleanedRecords;

    /** 原始数据总行数 (包含表头，加载后赋值) */
    private static int totalRawLines = 0;

    /** 当前激活的清洗策略 (可动态切换) */
    private static DataCleaningStrategy currentStrategy = new DropInvalidStrategy();

    // ========== 核心组件 (系统启动时初始化) ==========

    /** 指挥中心：管理探测车实例和指令日志 */
    private static final CommandCenter commandCenter = new CommandCenter();

    /** 数据分析器：扫描数据并触发告警 (Subject) */
    private static final DataAnalyzer dataAnalyzer = new DataAnalyzer();

    /** 扫描器：用于控制台输入 */
    private static final Scanner scanner = new Scanner(System.in);

    // ========== 程序入口 ==========

    /**
     * 主函数：启动深空探测舰队异星环境分析控制台。
     *
     * @param args 命令行参数 (未使用)
     */
    public static void main(String[] args) {
        // 初始化观察者模式：注册三个告警观察者
        initializeObservers();

        // 进入交互式主菜单循环 (直到用户选择退出)
        runMainLoop();
    }

    // ========== 初始化方法 ==========

    /**
     * 初始化观察者模式：向 {@link DataAnalyzer} 注册三个告警观察者。
     * <ol>
     *   <li>{@link ConsoleAlertObserver} - 在控制台输出高亮告警</li>
     *   <li>{@link LogFileObserver} - 将告警追加写入 {@code alerts.log}</li>
     *   <li>{@link CommandCenterObserver} - 通知指挥中心下发危机指令</li>
     * </ol>
     */
    private static void initializeObservers() {
        // 观察者1: 控制台输出告警
        AlertObserver consoleObserver = new ConsoleAlertObserver();
        dataAnalyzer.addObserver(consoleObserver);

        // 观察者2: 追加写入 alerts.log
        AlertObserver logObserver = new LogFileObserver();
        dataAnalyzer.addObserver(logObserver);

        // 观察者3: 指挥中心 (触发命令模式执行危机指令)
        AlertObserver commandObserver = new CommandCenterObserver(commandCenter);
        dataAnalyzer.addObserver(commandObserver);
    }

    // ========== 主菜单循环 ==========

    /**
     * 运行交互式主菜单的无限循环。
     * 每次执行完用户选择的操作后，自动返回主菜单继续等待输入。
     * 用户选择 0 时退出循环。
     */
    private static void runMainLoop() {
        while (true) {
            printHeader();

            System.out.print("请输入您的操作指令 (0-5): ");
            String input = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[错误] 请输入有效的数字 (0-5)！");
                continue;
            }

            switch (choice) {
                case 0 -> handleExit();
                case 1 -> handleLoadAndClean();
                case 2 -> handleChangeStrategy();
                case 3 -> handleRunAnalysis();
                case 4 -> handleViewCommandLogs();
                case 5 -> handleExportJson();
                default -> System.out.println("[错误] 无效的选项，请输入 0-5 之间的数字！");
            }

            System.out.print("> 按任意键返回主菜单...");
            scanner.nextLine();  // 等待用户按下回车
        }
    }

    // ========== 菜单头部打印 ==========

    /**
     * 打印菜单头部（包含动态状态栏）。
     * 状态栏实时反映当前数据加载状态和清洗策略。
     */
    private static void printHeader() {
        // 构建数据状态描述
        String dataStatus;
        if (cleanedRecords == null) {
            dataStatus = "未加载数据";
        } else {
            dataStatus = String.format("已加载 %d 条", cleanedRecords.size());
        }

        String strategyName = currentStrategy.getStrategyName();

        System.out.println();
        System.out.println("======================================================");
        System.out.println("      深空探测舰队 - 异星环境分析控制台 V1.0");
        System.out.println("======================================================");
        System.out.printf("[ 数据状态: %s | 当前策略: %s ]%n", dataStatus, strategyName);
        System.out.println();
        System.out.println("  1. 加载原始数据并执行清洗 (Load & Clean)");
        System.out.println("  2. 切换数据清洗策略 (Change Strategy)");
        System.out.println("  3. 执行辐射风暴危机扫描 (Run Analysis)");
        System.out.println("  4. 查看探测车指令执行记录 (View Command Logs)");
        System.out.println("  5. 导出清洗后的结构化数据 (Export JSON)");
        System.out.println("  0. 退出系统 (Exit)");
        System.out.println("======================================================");
    }

    // ========== 各菜单选项的处理方法 ==========

    /**
     * 选项0：退出系统。
     * 打印退出信息并终止 JVM。
     */
    private static void handleExit() {
        System.out.println();
        System.out.println("> 正在关闭指挥中心系统...");
        System.out.println("> 通讯链路已断开，再见。");
        System.exit(0);
    }

    /**
     * 选项1：加载原始数据并执行清洗。
     * 提示用户输入 CSV 文件名，读取后应用当前策略清洗数据。
     */
    private static void handleLoadAndClean() {
        System.out.println();
        System.out.println("> 请输入 CSV 文件名 (按回车默认: " + CsvDataLoader.DEFAULT_CSV_FILE + "): ");

        String fileName = scanner.nextLine().trim();
        if (fileName.isEmpty()) {
            fileName = CsvDataLoader.DEFAULT_CSV_FILE;
        }

        try {
            System.out.println("> 正在读取并清洗数据...");

            // 步骤1: 从 CSV 文件加载原始数据
            List<TelemetryRecord> rawRecords = CsvDataLoader.load(fileName, commandCenter);

            if (rawRecords.isEmpty()) {
                System.out.println("[警告] 文件为空或没有有效数据！");
                return;
            }

            // 记录原始数据总行数 (用于统计)
            totalRawLines = countCsvLines(fileName);

            // 步骤2: 应用当前策略进行数据清洗
            long startTime = System.currentTimeMillis();
            cleanedRecords = currentStrategy.clean(rawRecords);
            long elapsed = System.currentTimeMillis() - startTime;

            // 步骤3: 输出清洗统计信息
            int originalCount = totalRawLines - 1; // 减去表头行
            int cleanedCount = cleanedRecords.size();
            int removedOrFixed = originalCount - cleanedCount;

            System.out.println("> 处理完成！");
            System.out.printf("> 原始数据总行数: %d 行%n", originalCount);
            System.out.printf("> 剔除/修复异常数据: %d 行%n", removedOrFixed);
            System.out.printf("> 最终有效数据保留: %d 行%n", cleanedCount);
            System.out.printf("> 清洗耗时: %d ms%n", elapsed);

        } catch (IOException e) {
            System.out.printf("[错误] 无法读取文件 %s: %s%n", fileName, e.getMessage());
        } catch (Exception e) {
            System.out.printf("[错误] 数据处理异常: %s%n", e.getMessage());
        }
    }

    /**
     * 选项2：切换数据清洗策略。
     * 提供子菜单让用户选择 A 或 B，切换后更新状态栏。
     */
    private static void handleChangeStrategy() {
        System.out.println();
        System.out.println("> 请选择新的清洗策略：");
        System.out.println("  A. 严格丢弃策略 (DropInvalidStrategy)");
        System.out.println("  B. 智能填充与修正策略 (SmartFillStrategy)");
        System.out.print("> 请输入 A 或 B: ");

        String input = scanner.nextLine().trim().toUpperCase();

        switch (input) {
            case "A" -> {
                currentStrategy = new DropInvalidStrategy();
                System.out.println("> 策略已切换为: " + currentStrategy.getStrategyName() + "。");
            }
            case "B" -> {
                currentStrategy = new SmartFillStrategy();
                System.out.println("> 策略已切换为: " + currentStrategy.getStrategyName() + "。");
            }
            default -> {
                System.out.println("[错误] 无效的选择，请输入 A 或 B！");
                return;
            }
        }

        // 提示用户需要重新加载数据才能生效
        System.out.println("> (提示：请重新执行菜单 [1] 以应用新策略重新加载数据)");
    }

    /**
     * 选项3：执行辐射风暴危机扫描。
     * 防呆设计：如果未加载数据则拦截。
     * 使用 DataAnalyzer 遍历有效数据，检测连续高辐射事件。
     */
    private static void handleRunAnalysis() {
        // 防呆：检查数据是否已加载
        if (!isDataLoaded()) {
            return;
        }

        System.out.println();
        System.out.println("> 正在进行全舰队环境数据扫描...");

        // 清空之前的指令日志 (避免累积)
        commandCenter.clearLogs();

        // 执行扫描，获取危机事件数
        int alertCount = dataAnalyzer.scan(cleanedRecords);

        if (alertCount > 0) {
            System.out.printf("> 扫描完毕。共发现 %d 起极度危险事件，已记录至 alerts.log，并交由指挥中心处理。%n",
                    alertCount);
        } else {
            System.out.println("> 扫描完毕。未发现辐射风暴危机，舰队状态安全。");
        }
    }

    /**
     * 选项4：查看探测车指令执行记录。
     * 展示 CommandCenter 中记录的危机指令执行日志。
     */
    private static void handleViewCommandLogs() {
        System.out.println();
        System.out.println("> === 舰队指令下发与执行记录 ===");

        List<String> logs = commandCenter.getAllCommandLogs();

        if (logs.isEmpty()) {
            System.out.println("> (暂无指令执行记录，请先执行菜单 [3] 进行危机扫描)");
        } else {
            for (String log : logs) {
                System.out.println("> " + log);
            }
        }

        System.out.println("> === 记录结束 ===");
    }

    /**
     * 选项5：导出清洗后的结构化数据。
     * 防呆设计：如果未加载数据则拦截。
     * 使用 Jackson 将清洗后的数据序列化为标准 JSON 文件。
     */
    private static void handleExportJson() {
        // 防呆：检查数据是否已加载
        if (!isDataLoaded()) {
            return;
        }

        System.out.println();
        System.out.println("> 正在生成 JSON 文件...");

        try {
            String outputPath = "clean_telemetry.json";
            JsonUtil.writeToFile(cleanedRecords, outputPath);
            System.out.println("> 导出成功！文件已保存至: ./" + outputPath);
        } catch (IOException e) {
            System.out.printf("[错误] JSON 导出失败: %s%n", e.getMessage());
        } catch (Exception e) {
            System.out.printf("[错误] 导出过程异常: %s%n", e.getMessage());
        }
    }

    // ========== 辅助方法 ==========

    /**
     * 检查数据是否已加载到内存中。
     * 如果未加载，打印提示信息并返回 false。
     *
     * @return true 表示数据已加载
     */
    private static boolean isDataLoaded() {
        if (cleanedRecords == null) {
            System.out.println();
            System.out.println("[错误] 内存中无有效数据，请先执行 [1] 加载数据！");
            return false;
        }
        return true;
    }

    /**
     * 统计 CSV 文件的总行数 (包括表头)。
     * 用于在加载数据后输出统计信息。
     *
     * @param filePath CSV 文件路径
     * @return 文件总行数
     * @throws IOException 如果文件无法读取
     */
    private static int countCsvLines(String filePath) throws IOException {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filePath))) {
            int count = 0;
            while (reader.readLine() != null) {
                count++;
            }
            return count;
        }
    }
}
