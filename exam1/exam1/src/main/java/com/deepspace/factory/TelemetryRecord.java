/**
 * ============================================================
 * 遥测记录类 ({@code TelemetryRecord})
 * ============================================================
 * <p>
 * 封装从 CSV 文件中读取的一行遥测数据。
 * 包含时间戳、探测车ID、探测车类型、传感器类型、
 * 传感器数值以及数据状态。
 * </p>
 * <p>
 * 设计说明：
 * </p>
 * <ul>
 *   <li>使用不可变设计：字段通过构造器注入，仅提供 Getter</li>
 *   <li>提供 {@link #withValueAndStatus(Double, String)} 方法，便于策略类快速创建修正后的对象</li>
 *   <li>重写 {@link #equals(Object)} 和 {@link #hashCode()} 以便在集合操作中正确比较</li>
 * </ul>
 * ============================================================
 */
package com.deepspace.factory;

import java.util.Objects;

public class TelemetryRecord {

    /** 数据采集时间戳 (ISO-8601 格式, 如 2055-10-12T08:00:00Z) */
    private final String timestamp;

    /** 探测车唯一标识符 (如 R-01, R-02 等) */
    private final String roverId;

    /** 探测车类型 (Tracked / Hover / Walker) */
    private final String roverType;

    /** 传感器类型 (Temperature / Radiation / Atmosphere) */
    private final String sensorType;

    /** 传感器采集的数值 (Double 类型, 允许为 null 表示缺失值) */
    private final Double value;

    /** 数据状态 (OK / WARNING / ERROR / REPAIRED) */
    private final String status;

    /**
     * 构造一个遥测记录对象。
     *
     * @param timestamp   时间戳
     * @param roverId     探测车 ID
     * @param roverType   探测车类型
     * @param sensorType  传感器类型
     * @param value       传感器数值 (可为 null)
     * @param status      数据状态
     */
    public TelemetryRecord(String timestamp, String roverId, String roverType,
                           String sensorType, Double value, String status) {
        this.timestamp = timestamp;
        this.roverId = roverId;
        this.roverType = roverType;
        this.sensorType = sensorType;
        this.value = value;
        this.status = status;
    }

    // ---------- Getter 方法 ----------

    /**
     * 获取数据采集时间戳 (ISO-8601 格式)。
     *
     * @return 时间戳字符串，如 {@code "2055-10-12T08:00:00Z"}
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * 获取探测车的唯一标识符。
     *
     * @return 探测车 ID，如 {@code "R-01"}, {@code "R-02"}
     */
    public String getRoverId() {
        return roverId;
    }

    /**
     * 获取探测车类型。
     *
     * @return 探测车类型 ({@code "Tracked"} / {@code "Hover"} / {@code "Walker"})
     */
    public String getRoverType() {
        return roverType;
    }

    /**
     * 获取传感器类型。
     *
     * @return 传感器类型 ({@code "Temperature"} / {@code "Radiation"} / {@code "Atmosphere"})
     */
    public String getSensorType() {
        return sensorType;
    }

    /**
     * 获取传感器采集的数值。
     *
     * @return 传感器数值，可能为 {@code null} 表示缺失值
     */
    public Double getValue() {
        return value;
    }

    /**
     * 获取数据状态。
     *
     * @return 数据状态 ({@code "OK"} / {@code "WARNING"} / {@code "ERROR"} / {@code "REPAIRED"})
     */
    public String getStatus() {
        return status;
    }

    /**
     * 创建一个被修复后的新记录副本。
     * <p>用于 {@link SmartFillStrategy} 修正数据后生成新对象 (保持不可变性)。</p>
     *
     * @param newValue  修正后的数值
     * @param newStatus 修正后的状态 (通常为 {@code "REPAIRED"})
     * @return 新的 {@code TelemetryRecord} 实例
     */
    public TelemetryRecord withValueAndStatus(Double newValue, String newStatus) {
        return new TelemetryRecord(timestamp, roverId, roverType, sensorType, newValue, newStatus);
    }

    /**
     * 返回遥测记录的字符串描述。
     *
     * @return 包含所有字段信息的格式化字符串
     */
    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | %s | %s",
                timestamp, roverId, roverType, sensorType,
                value != null ? value.toString() : "null", status);
    }

    /**
     * 比较两个遥测记录是否相等。
     * <p>基于 {@code timestamp}、{@code roverId} 和 {@code sensorType} 三个字段判断相等。</p>
     *
     * @param o 待比较的对象
     * @return {@code true} 表示两个记录相等
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TelemetryRecord)) return false;
        TelemetryRecord that = (TelemetryRecord) o;
        return Objects.equals(timestamp, that.timestamp)
                && Objects.equals(roverId, that.roverId)
                && Objects.equals(sensorType, that.sensorType);
    }

    /**
     * 返回遥测记录的哈希码。
     * <p>基于 {@code timestamp}、{@code roverId} 和 {@code sensorType} 计算。</p>
     *
     * @return 哈希码值
     */
    @Override
    public int hashCode() {
        return Objects.hash(timestamp, roverId, sensorType);
    }
}
