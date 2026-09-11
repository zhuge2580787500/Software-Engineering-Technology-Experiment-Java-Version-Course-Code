/**
 * ============================================================
 * 控制台告警观察者 ({@link AlertObserver} 的具体实现)
 * ============================================================
 * <p>
 * 观察者模式的具体实现类之一。
 * 当辐射风暴危机被触发时，在控制台以高亮格式输出告警信息。
 * </p>
 * <p>
 * 输出格式：
 * </p>
 * <blockquote>{@code [CRITICAL] 探测车 R-02 遭遇辐射风暴！(时间: 2055-10-12T08:01:22Z)}</blockquote>
 * ============================================================
 */
package com.deepspace.observer;

import com.deepspace.factory.TelemetryRecord;

public class ConsoleAlertObserver implements AlertObserver {

    /** ANSI 颜色转义码 - 红色 (在支持 ANSI 的终端中显示高亮红色) */
    private static final String ANSI_RED = "[31m";

    /** ANSI 颜色转义码 - 粗体 */
    private static final String ANSI_BOLD = "[1m";

    /** ANSI 颜色转义码 - 重置 (恢复默认样式) */
    private static final String ANSI_RESET = "[0m";

    @Override
    public void onAlert(TelemetryRecord record, String message) {
        // 使用 ANSI 转义码输出高亮告警 (在不支持 ANSI 的终端中会显示原始字符)
        System.out.printf("%s%s[警告]%s %s%n",
                ANSI_BOLD, ANSI_RED, ANSI_RESET, message);
    }
}
