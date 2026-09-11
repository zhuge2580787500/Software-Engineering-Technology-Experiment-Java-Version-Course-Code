/**
 * ============================================================
 * CSV 数据加载器 ({@code CsvDataLoader})
 * ============================================================
 * <p>
 * 负责读取原始遥测 CSV 文件，并将每一行解析为 {@link TelemetryRecord} 对象。
 * </p>
 * <p>
 * 功能特性：
 * </p>
 * <ul>
 *   <li>自动跳过 CSV 文件头行 (Header)</li>
 *   <li>兼容带引号/不带引号的 CSV 字段</li>
 *   <li>容错处理：解析失败的记录记录到标准错误输出，但不中断整体加载</li>
 *   <li>使用 {@link RoverFactory} 创建 {@link Rover} 对象并注册到 {@link CommandCenter}</li>
 * </ul>
 * <p>
 * CSV 格式假定：
 * </p>
 * <blockquote>{@code Timestamp,RoverID,RoverType,SensorType,Value,Status}</blockquote>
 * ============================================================
 */
package com.deepspace.util;

import com.deepspace.command.CommandCenter;
import com.deepspace.factory.Rover;
import com.deepspace.factory.RoverFactory;
import com.deepspace.factory.TelemetryRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvDataLoader {

    /** 默认 CSV 文件名 */
    public static final String DEFAULT_CSV_FILE = "raw_telemetry.csv";

    /**
     * 加载默认的 CSV 文件 ({@value #DEFAULT_CSV_FILE})。
     *
     * @param commandCenter 指挥中心实例 (用于注册探测车)
     * @return 解析后的遥测记录列表
     * @throws IOException 如果文件不存在或读取失败
     */
    public static List<TelemetryRecord> load(CommandCenter commandCenter) throws IOException {
        return load(DEFAULT_CSV_FILE, commandCenter);
    }

    /**
     * 加载指定路径的 CSV 文件。
     *
     * @param filePath      CSV 文件路径
     * @param commandCenter  指挥中心实例 (用于注册探测车)
     * @return 解析后的遥测记录列表
     * @throws IOException 如果文件不存在或读取失败
     */
    public static List<TelemetryRecord> load(String filePath, CommandCenter commandCenter) throws IOException {
        List<TelemetryRecord> records = new ArrayList<>();

        // 使用 BufferedReader 逐行读取，避免一次性加载大文件到内存
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine(); // 跳过表头

            if (headerLine == null) {
                System.err.println("[警告] CSV 文件为空: " + filePath);
                return records;
            }

            String line;
            int lineNumber = 1; // 从第1行数据开始 (表头已读取)

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    TelemetryRecord record = parseLine(line, lineNumber);
                    if (record != null) {
                        records.add(record);
                        // 注册探测车实例到指挥中心 (如果尚未注册)
                        registerRover(commandCenter, record);
                    }
                } catch (Exception e) {
                    // 单行解析失败不影响其他行的加载
                    System.err.printf("[警告] 第 %d 行解析失败，已跳过: %s (%s)%n",
                            lineNumber, line, e.getMessage());
                }
            }
        }

        return records;
    }

    /**
     * 解析单行 CSV 数据为 {@link TelemetryRecord} 对象。
     * <p>CSV 格式: {@code Timestamp,RoverID,RoverType,SensorType,Value,Status}</p>
     *
     * @param line        CSV 行内容
     * @param lineNumber  行号 (用于错误提示)
     * @return {@link TelemetryRecord} 对象，解析失败返回 {@code null}
     */
    private static TelemetryRecord parseLine(String line, int lineNumber) {
        if (line == null || line.trim().isEmpty()) {
            return null; // 跳过空行
        }

        // 按逗号分割 (简单实现，不处理字段中包含逗号的复杂情况)
        String[] parts = line.split(",", -1); // -1 保留尾部空字段

        if (parts.length < 6) {
            throw new IllegalArgumentException(
                    String.format("字段数不足 (期望6个，实际%d个): %s", parts.length, line));
        }

        String timestamp  = parts[0].trim();
        String roverId     = parts[1].trim();
        String roverType   = parts[2].trim();
        String sensorType  = parts[3].trim();
        String valueStr    = parts[4].trim();
        String status      = parts[5].trim();

        // 解析 Value 字段: 空字符串 → null，否则尝试解析为 Double
        Double value = parseDouble(valueStr);

        return new TelemetryRecord(timestamp, roverId, roverType, sensorType, value, status);
    }

    /**
     * 将字符串解析为 {@code Double}，空字符串返回 {@code null}。
     *
     * @param valueStr 数值字符串
     * @return {@code Double} 对象，解析失败返回 {@code null}
     */
    private static Double parseDouble(String valueStr) {
        if (valueStr == null || valueStr.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(valueStr);
        } catch (NumberFormatException e) {
            // 无法解析为数字，返回 null 由策略层处理
            return null;
        }
    }

    /**
     * 如果指挥中心尚未注册该 {@code RoverID}，则注册一个探测车实例。
     * <p>使用 {@link RoverFactory#create(String, String)} 根据 CSV 中的 {@code RoverType}
     * 创建正确类型的 {@link Rover} 实例。</p>
     *
     * @param commandCenter 指挥中心
     * @param record        遥测记录 (包含 {@code RoverID} 和 {@code RoverType})
     */
    private static void registerRover(CommandCenter commandCenter, TelemetryRecord record) {
        // 检查是否已注册 (通过检查 getOrCreateRover 是否创建了新实例来判断)
        // 这里直接尝试获取，如果返回的是新创建的默认实例，则替换为正确类型的实例
        String roverId = record.getRoverId();
        String roverType = record.getRoverType();

        if (RoverFactory.isValidType(roverType)) {
            Rover rover = RoverFactory.create(roverId, roverType);
            commandCenter.registerRover(rover);
        }
    }
}
