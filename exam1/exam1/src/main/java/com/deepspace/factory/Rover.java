/**
 * ============================================================
 * 探测车抽象类 ({@code Rover})
 * ============================================================
 * <p>
 * 作为所有探测车类型的基类，定义了探测车的通用属性和行为。
 * 该抽象类本身不直接实例化，而是通过 {@link RoverFactory} 工厂类
 * 根据 CSV 中的 {@code RoverType} 字段创建对应的具体子类实例。
 * </p>
 * <p>
 * 支持的子类：
 * </p>
 * <ul>
 *   <li>{@link TrackedRover}   : 履带式探测车</li>
 *   <li>{@link HoverRover}     : 悬浮式探测车</li>
 *   <li>{@link WalkerRover}    : 步行式探测车</li>
 * </ul>
 * <p>
 * 设计模式：简单工厂模式
 * </p>
 * <ul>
 *   <li>客户端 ({@link RoverFactory}) 负责创建具体 {@code Rover} 实例</li>
 *   <li>客户端只需知道抽象 {@code Rover} 类型，无需关心具体实现</li>
 * </ul>
 * ============================================================
 */
package com.deepspace.factory;

import java.util.ArrayList;
import java.util.List;

public abstract class Rover {

    /** 探测车唯一标识符 */
    protected final String roverId;

    /** 探测车类型描述 */
    protected final String roverType;

    /** 该探测车接收到的指令执行日志 (按时间顺序记录) */
    protected final List<String> commandLog;

    /**
     * 构造一个探测车实例。
     *
     * @param roverId   探测车 ID
     * @param roverType 探测车类型名称
     */
    public Rover(String roverId, String roverType) {
        this.roverId = roverId;
        this.roverType = roverType;
        this.commandLog = new ArrayList<>();
    }

    // ---------- Getter 方法 ----------

    /**
     * 获取探测车的唯一标识符。
     *
     * @return 探测车 ID
     */
    public String getRoverId() {
        return roverId;
    }

    /**
     * 获取探测车的类型名称。
     *
     * @return 探测车类型 (如 {@code "Tracked"}, {@code "Hover"}, {@code "Walker"})
     */
    public String getRoverType() {
        return roverType;
    }

    /**
     * 获取该探测车的指令执行日志（返回不可修改副本）。
     *
     * @return 指令日志列表
     */
    public List<String> getCommandLog() {
        return new ArrayList<>(commandLog);
    }

    /**
     * 记录一条指令执行日志。
     *
     * @param logEntry 日志条目
     */
    protected void logCommand(String logEntry) {
        commandLog.add(logEntry);
    }

    /**
     * 执行探测车指令。
     * <p>各子类可实现特定于探测车类型的指令执行逻辑。</p>
     *
     * @param command 待执行的指令对象
     */
    public abstract void executeCommand(com.deepspace.command.RoverCommand command);

    /**
     * 返回探测车的字符串描述。
     *
     * @return 格式为 {@code "{RoverID} ({RoverType})"} 的字符串
     */
    @Override
    public String toString() {
        return String.format("%s (%s)", roverId, roverType);
    }
}
