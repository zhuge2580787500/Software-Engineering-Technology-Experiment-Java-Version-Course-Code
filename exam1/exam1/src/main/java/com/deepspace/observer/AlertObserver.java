/**
 * ============================================================
 * 告警观察者接口 ({@code AlertObserver})
 * ============================================================
 * <p>
 * 观察者模式 (Observer Pattern) 的核心接口。
 * 定义了当辐射风暴危机被触发时，观察者需要响应的方法。
 * </p>
 * <p>
 * 三个具体观察者实现：
 * </p>
 * <ul>
 *   <li>{@link ConsoleAlertObserver}  : 在控制台输出高亮告警</li>
 *   <li>{@link LogFileObserver}       : 将告警追加写入 {@code alerts.log}</li>
 *   <li>{@link CommandCenterObserver} : 通知指挥中心下发危机指令</li>
 * </ul>
 * <p>
 * 注册/注销机制：
 * </p>
 * <ul>
 *   <li>{@link DataAnalyzer} (Subject) 负责维护观察者列表</li>
 *   <li>并在危机触发时通知所有已注册的观察者</li>
 * </ul>
 * ============================================================
 */
package com.deepspace.observer;

import com.deepspace.factory.TelemetryRecord;

public interface AlertObserver {

    /**
     * 当辐射风暴危机被触发时，由 DataAnalyzer 调用此方法。
     *
     * @param record 触发危机的遥测记录
     * @param message 告警描述信息
     */
    void onAlert(TelemetryRecord record, String message);
}
