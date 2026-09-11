/**
 * ============================================================
 * 智能填充与修正策略 ({@link DataCleaningStrategy} 的实现类)
 * ============================================================
 * <p>
 * 策略 B：对不合格的数据记录进行智能修正而非丢弃。
 * </p>
 * <p>
 * 修正规则：
 * </p>
 * <p>
 * <b>规则1 - 修正非数字/为空：</b>
 * </p>
 * <ul>
 *   <li>{@code Temperature} → {@value #DEFAULT_TEMPERATURE}</li>
 *   <li>{@code Radiation}   → {@value #DEFAULT_RADIATION}</li>
 *   <li>{@code Atmosphere}  → {@value #DEFAULT_ATMOSPHERE}</li>
 * </ul>
 * <p>
 * <b>规则2 - 修正越界值 (超出物理极限)：</b>
 * </p>
 * <ul>
 *   <li>{@code Temperature}: &lt; {@value #TEMP_MIN} 或 &gt; {@value #TEMP_MAX}      → {@value #DEFAULT_TEMPERATURE}</li>
 *   <li>{@code Radiation}:   &lt; {@value #RAD_MIN} 或 &gt; {@value #RAD_MAX}        → {@value #DEFAULT_RADIATION}</li>
 *   <li>{@code Atmosphere}:  &lt; {@value #ATM_MIN} 或 &gt; {@value #ATM_MAX}        → {@value #DEFAULT_ATMOSPHERE}</li>
 * </ul>
 * <p>
 * <b>规则3 - 状态重置：</b>
 * </p>
 * <ul>
 *   <li>被修正的记录，{@code Status} 强制修改为 {@code "REPAIRED"}</li>
 * </ul>
 * <p>
 * 该策略保留所有记录，仅修正其中的脏数据。
 * </p>
 * ============================================================
 */
package com.deepspace.strategy;

import com.deepspace.factory.TelemetryRecord;

import java.util.ArrayList;
import java.util.List;

public class SmartFillStrategy implements DataCleaningStrategy {

    // ========== 各传感器类型的安全默认值 ==========

    /** 温度传感器的安全默认值 (用于修正缺失或越界的温度读数) */
    private static final double DEFAULT_TEMPERATURE = -50.0;

    /** 辐射传感器的安全默认值 (用于修正缺失或越界的辐射读数) */
    private static final double DEFAULT_RADIATION = 100.0;

    /** 大气传感器的安全默认值 (用于修正缺失或越界的大气读数) */
    private static final double DEFAULT_ATMOSPHERE = 95.0;

    // ========== 各传感器的物理极限范围 ==========

    /** 温度范围下限 (单位: 摄氏度) */
    private static final double TEMP_MIN = -150.0;

    /** 温度范围上限 (单位: 摄氏度) */
    private static final double TEMP_MAX = 150.0;

    /** 辐射范围下限 (单位: 未指定) */
    private static final double RAD_MIN = 0.0;

    /** 辐射范围上限 (单位: 未指定) */
    private static final double RAD_MAX = 5000.0;

    /** 大气范围下限 (百分比) */
    private static final double ATM_MIN = 0.0;

    /** 大气范围上限 (百分比) */
    private static final double ATM_MAX = 100.0;

    @Override
    public String getStrategyName() {
        return "智能填充与修正策略 (SmartFillStrategy)";
    }

    /**
     * 执行智能填充与修正策略：遍历所有记录，修正脏数据并保留所有记录。
     *
     * @param rawRecords 原始记录列表
     * @return 修正后的记录列表 (与输入列表大小相同)
     */
    @Override
    public List<TelemetryRecord> clean(List<TelemetryRecord> rawRecords) {
        List<TelemetryRecord> cleaned = new ArrayList<>();

        for (TelemetryRecord record : rawRecords) {
            cleaned.add(repairIfNeeded(record));
        }

        return cleaned;
    }

    /**
     * 判断单条记录是否需要修正。
     *
     * 需要修正的条件：
     *   - Value 为 null (缺失值)
     *   - Value 为 NaN 或 Infinite
     *   - Value 超出对应传感器类型的物理极限范围
     *
     * @param record 待校验的记录
     * @return true 表示需要修正
     */
    private boolean needsRepair(TelemetryRecord record) {
        Double value = record.getValue();

        // 情况1: Value 为 null (CSV 中该字段为空)
        if (value == null) {
            return true;
        }

        // 情况2: Value 不是有效数字 (NaN 或 Infinite)
        if (value.isNaN() || value.isInfinite()) {
            return true;
        }

        // 情况3: Value 超出对应传感器类型的物理极限范围
        return isOutOfRange(record.getSensorType(), value);
    }

    /**
     * 判断数值是否超出指定传感器类型的物理极限范围。
     *
     * @param sensorType 传感器类型
     * @param value      待检查的数值
     * @return true 表示超出范围
     */
    private boolean isOutOfRange(String sensorType, double value) {
        return switch (sensorType) {
            case "Temperature" -> value < TEMP_MIN || value > TEMP_MAX;
            case "Radiation"   -> value < RAD_MIN  || value > RAD_MAX;
            case "Atmosphere"  -> value < ATM_MIN  || value > ATM_MAX;
            default -> false; // 未知传感器类型不做范围检查
        };
    }

    /**
     * 根据传感器类型获取安全默认值。
     *
     * @param sensorType 传感器类型
     * @return 安全默认数值
     */
    private double getDefaultValue(String sensorType) {
        return switch (sensorType) {
            case "Temperature" -> DEFAULT_TEMPERATURE;
            case "Radiation"   -> DEFAULT_RADIATION;
            case "Atmosphere"  -> DEFAULT_ATMOSPHERE;
            default -> 0.0; // 未知类型的兜底默认值
        };
    }

    /**
     * 如果记录需要修正，则生成修正后的新记录副本；否则返回原记录。
     * 注意：TelemetryRecord 是不可变的，因此总是返回新实例。
     *
     * @param record 原始记录
     * @return 修正后的记录 (或原记录的副本)
     */
    private TelemetryRecord repairIfNeeded(TelemetryRecord record) {
        if (!needsRepair(record)) {
            // 记录完好无损，直接返回原记录
            return record;
        }

        // 修正：填充安全默认值 + 状态改为 REPAIRED
        double fixedValue = getDefaultValue(record.getSensorType());
        return record.withValueAndStatus(fixedValue, "REPAIRED");
    }
}
