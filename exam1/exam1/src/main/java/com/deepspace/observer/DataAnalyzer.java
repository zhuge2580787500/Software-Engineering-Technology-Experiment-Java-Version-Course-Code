/**
 * ============================================================
 * 数据分析器 ({@code DataAnalyzer})
 * ============================================================
 * <p>
 * 观察者模式中的"主题" (Subject / Observable)。
 * 负责按时间顺序扫描清洗后的遥测数据，
 * 检测辐射风暴危机条件，并在条件满足时通知所有已注册的观察者。
 * </p>
 * <p>
 * 危机触发条件：
 * </p>
 * <blockquote>同一台探测车 ({@code RoverID}) 连续 2 次传回 {@code Radiation} 数值 &gt; 400.0</blockquote>
 * <p>
 * 设计说明：
 * </p>
 * <ul>
 *   <li>支持注册/注销多个 {@link AlertObserver}</li>
 *   <li>危机检测基于时间顺序的线性扫描</li>
 *   <li>使用 {@code Map} 记录每个探测车的"上一次辐射值"以判断连续性</li>
 * </ul>
 * ============================================================
 */
package com.deepspace.observer;

import com.deepspace.factory.TelemetryRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAnalyzer {

    /** 已注册的告警观察者列表 */
    private final List<AlertObserver> observers;

    /** 辐射风暴阈值: 超过此值视为危险 */
    private static final double RADIATION_THRESHOLD = 400.0;

    /**
     * 构造数据分析器，初始化空的观察者列表。
     */
    public DataAnalyzer() {
        this.observers = new ArrayList<>();
    }

    /**
     * 注册一个新的告警观察者。
     *
     * @param observer 待注册的观察者
     */
    public void addObserver(AlertObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * 注销一个已注册的观察者。
     *
     * @param observer 待注销的观察者
     */
    public void removeObserver(AlertObserver observer) {
        observers.remove(observer);
    }


    /**
     * 核心分析方法：按时间顺序扫描有效数据，检测辐射风暴危机。
     *
     * 算法逻辑：
     *   1. 遍历每条记录
     *   2. 只关注 Radiation 类型的记录
     *   3. 使用 Map 记录每个 Rover 上一次的 Radiation 值
     *   4. 当同一 Rover 连续两次 Radiation > 400.0 时，触发危机
     *   5. 危机触发后，通知所有已注册的观察者
     *
     * @param records 清洗后的有效记录列表 (假设已按时间排序)
     * @return 触发的危机事件总数
     */
    public int scan(List<TelemetryRecord> records) {
        // 记录每个探测车上一次的辐射值 (null 表示尚未有辐射记录)
        Map<String, Double> lastRadiationMap = new HashMap<>();

        int alertCount = 0;

        for (TelemetryRecord record : records) {
            // 只处理 Radiation 类型的记录
            if (!"Radiation".equalsIgnoreCase(record.getSensorType())) {
                continue;
            }

            String roverId = record.getRoverId();
            Double currentValue = record.getValue();

            // 获取该探测车上一次的辐射值
            Double lastValue = lastRadiationMap.get(roverId);

            // 判断是否满足危机条件：连续 2 次 Radiation > 400.0
            if (lastValue != null
                    && lastValue > RADIATION_THRESHOLD
                    && currentValue != null
                    && currentValue > RADIATION_THRESHOLD) {

                // 触发辐射风暴危机
                String message = String.format(
                        "探测车 %s 遭遇辐射风暴！(时间: %s)",
                        roverId, record.getTimestamp());

                // 通知所有观察者
                notifyObservers(record, message);
                alertCount++;
            }

            // 更新该探测车的最新辐射值
            if (currentValue != null) {
                lastRadiationMap.put(roverId, currentValue);
            }
        }

        return alertCount;
    }

    /**
     * 遍历所有已注册的观察者，逐一发送告警通知。
     *
     * @param record  触发危机的记录
     * @param message 告警描述
     */
    private void notifyObservers(TelemetryRecord record, String message) {
        for (AlertObserver observer : observers) {
            try {
                observer.onAlert(record, message);
            } catch (Exception e) {
                // 防止单个观察者的异常影响其他观察者的通知
                System.err.printf("[警告] 观察者 %s 执行异常: %s%n",
                        observer.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
