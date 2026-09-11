/**
 * ============================================================
 * 严格丢弃策略 ({@link DataCleaningStrategy} 的实现类)
 * ============================================================
 * <p>
 * 策略 A：对不合格的数据记录执行整行丢弃。
 * </p>
 * <p>
 * 丢弃规则 (满足任意一条即丢弃)：
 * </p>
 * <ol>
 *   <li>{@code Value} 字段为空 ({@code null} 或空字符串)</li>
 *   <li>{@code Value} 字段无法解析为有效数字</li>
 *   <li>{@code Status} 字段值为 {@code "ERROR"}</li>
 * </ol>
 * <p>
 * 该策略不修改任何记录，仅过滤掉不符合条件的数据行。
 * </p>
 * ============================================================
 */
package com.deepspace.strategy;

import com.deepspace.factory.TelemetryRecord;

import java.util.ArrayList;
import java.util.List;

public class DropInvalidStrategy implements DataCleaningStrategy {

    @Override
    public String getStrategyName() {
        return "严格丢弃策略 (DropInvalidStrategy)";
    }

    /**
     * 执行严格丢弃策略：遍历所有记录，将不合格的记录剔除。
     *
     * @param rawRecords 原始记录列表
     * @return 清洗后的有效记录列表
     */
    @Override
    public List<TelemetryRecord> clean(List<TelemetryRecord> rawRecords) {
        List<TelemetryRecord> cleaned = new ArrayList<>();

        for (TelemetryRecord record : rawRecords) {
            if (isValid(record)) {
                cleaned.add(record);
            }
            // else: 丢弃该记录 (不加入 cleaned 列表)
        }

        return cleaned;
    }

    /**
     * 判断单条记录是否满足有效性条件。
     * <p>
     * 有效条件：
     * </p>
     * <ul>
     *   <li>{@code Value} 非空且可解析为有效数字</li>
     *   <li>{@code Status} 不等于 {@code "ERROR"}</li>
     * </ul>
     * <p>
     * 注：如果 {@code Value} 已经能解析为 {@code Double}，则说明是有效数字
     * (因为 {@link TelemetryRecord} 的 {@code value} 字段本身就是 {@code Double} 类型，
     * 解析工作由 {@link CsvDataLoader} 完成，此处仅做 {@code null} 检查即可)
     * </p>
     *
     * @param record 待校验的记录
     * @return {@code true} 表示记录有效，应保留
     */
    private boolean isValid(TelemetryRecord record) {
        // 规则1: Value 不能为空
        if (record.getValue() == null) {
            return false;
        }

        // 规则2: Status 不能为 "ERROR"
        if ("ERROR".equalsIgnoreCase(record.getStatus())) {
            return false;
        }

        // 注：如果 Value 已经能解析为 Double，则说明是有效数字
        // (因为 TelemetryRecord 的 value 字段本身就是 Double 类型，
        //  解析工作由 CsvDataLoader 完成，此处仅做 null 检查即可)

        return true;
    }
}
