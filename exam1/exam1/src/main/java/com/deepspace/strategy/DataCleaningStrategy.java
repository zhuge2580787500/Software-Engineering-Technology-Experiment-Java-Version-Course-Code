/**
 * ============================================================
 * 数据清洗策略接口 ({@code DataCleaningStrategy})
 * ============================================================
 * <p>
 * 策略模式 (Strategy Pattern) 的核心接口。
 * 定义了数据清洗的统一行为，允许客户端在运行时切换不同的清洗策略，
 * 而无需修改调用方的代码逻辑。
 * </p>
 * <p>
 * 实现类：
 * </p>
 * <ul>
 *   <li>{@link DropInvalidStrategy}  : 严格丢弃策略 (丢弃无效记录)</li>
 *   <li>{@link SmartFillStrategy}    : 智能填充与修正策略 (修正无效数据)</li>
 * </ul>
 * <p>
 * 使用方式：
 * </p>
 * <pre>
 * DataCleaningStrategy strategy = new DropInvalidStrategy();
 * List{@code <TelemetryRecord>} cleaned = strategy.clean(rawRecords);
 * </pre>
 * ============================================================
 */
package com.deepspace.strategy;

import com.deepspace.factory.TelemetryRecord;

import java.util.List;

public interface DataCleaningStrategy {

    /**
     * 对原始遥测记录列表执行数据清洗操作。
     *
     * @param rawRecords 原始记录列表 (可能包含脏数据)
     * @return 清洗后的有效记录列表
     */
    List<TelemetryRecord> clean(List<TelemetryRecord> rawRecords);

    /**
     * 返回该策略的友好名称 (用于 CLI 状态栏显示)。
     *
     * @return 策略名称
     */
    String getStrategyName();
}
