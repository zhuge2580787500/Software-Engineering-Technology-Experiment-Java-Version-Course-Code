/**
 * ============================================================
 * 日志文件观察者 ({@link AlertObserver} 的具体实现)
 * ============================================================
 * <p>
 * 观察者模式的具体实现类之一。
 * 当辐射风暴危机被触发时，将告警信息以追加方式写入 {@code alerts.log} 文件。
 * </p>
 * <p>
 * 输出格式 (每行一条)：
 * </p>
 * <blockquote>{@code [2055-10-12T08:01:22Z] ALARM: Rover R-02 encountered a radiation storm.}</blockquote>
 * <p>
 * 文件行为：
 * </p>
 * <ul>
 *   <li>使用追加模式 (Append)，每次运行不会覆盖历史记录</li>
 *   <li>自动创建文件 (如果不存在)</li>
 *   <li>线程安全：使用 {@code FileWriter} 的同步机制</li>
 * </ul>
 * ============================================================
 */
package com.deepspace.observer;

import com.deepspace.factory.TelemetryRecord;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogFileObserver implements AlertObserver {

    /** 告警日志文件路径 */
    private static final String ALERT_LOG_FILE = "alerts.log";

    /**
     * 当辐射风暴危机被触发时，将告警信息追加写入 {@value #ALERT_LOG_FILE}。
     * <p>格式: {@code [时间戳] ALARM: Rover {RoverID} encountered a radiation storm.}</p>
     *
     * @param record 触发危机的遥测记录
     * @param message 告警描述信息
     */
    @Override
    public void onAlert(TelemetryRecord record, String message) {
        // 使用 PrintWriter + FileWriter(append=true) 实现追加写入
        // true 参数表示以追加模式打开文件，不会覆盖已有内容
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(ALERT_LOG_FILE, true))) {

            // 格式: [时间戳] ALARM: Rover X encountered a radiation storm.
            String logLine = String.format("[%s] ALARM: Rover %s encountered a radiation storm.",
                    record.getTimestamp(), record.getRoverId());

            writer.println(logLine);

            // 确保数据立即写入磁盘 (不是仅留在缓冲区中)
            writer.flush();

        } catch (IOException e) {
            System.err.printf("[错误] 无法写入告警日志文件 %s: %s%n",
                    ALERT_LOG_FILE, e.getMessage());
        }
    }
}
