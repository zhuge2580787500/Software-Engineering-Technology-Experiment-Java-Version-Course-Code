/**
 * ============================================================
 * 指挥中心观察者 ({@link AlertObserver} 的具体实现)
 * ============================================================
 * <p>
 * 观察者模式的具体实现类之一。
 * 当辐射风暴危机被触发时，此观察者负责通知指挥中心，
 * 指挥中心随即通过命令模式 (Command Pattern) 向涉事探测车下发危机指令。
 * </p>
 * <p>
 * 指令下发流程：
 * </p>
 * <ol>
 *   <li>收到 {@link #onAlert(TelemetryRecord, String)} 通知</li>
 *   <li>通过 {@link CommandCenter} 获取/创建对应探测车的指令队列</li>
 *   <li>自动创建 {@link ShieldCommand} (电磁防护罩) 并立即执行</li>
 *   <li>自动创建 {@link MoveCommand} (基地撤离) 并立即执行</li>
 *   <li>每次执行的日志被 {@link CommandCenter} 记录，供选项4查看</li>
 * </ol>
 * <p>
 * 指令日志格式：
 * </p>
 * <blockquote>{@code [R-02] 接收指令: ShieldCommand... 执行成功: 探测车 R-02 已开启电磁防护罩。}</blockquote>
 * ============================================================
 */
package com.deepspace.observer;

import com.deepspace.command.CommandCenter;
import com.deepspace.command.MoveCommand;
import com.deepspace.command.RoverCommand;
import com.deepspace.command.ShieldCommand;
import com.deepspace.factory.Rover;
import com.deepspace.factory.TelemetryRecord;

public class CommandCenterObserver implements AlertObserver {

    /** 指挥中心 (管理所有探测车的指令队列和执行日志) */
    private final CommandCenter commandCenter;

    /**
     * 构造指挥中心观察者。
     *
     * @param commandCenter 指挥中心实例 (由外部注入，便于测试)
     */
    public CommandCenterObserver(CommandCenter commandCenter) {
        this.commandCenter = commandCenter;
    }

    @Override
    public void onAlert(TelemetryRecord record, String message) {
        String roverId = record.getRoverId();

        // 1. 获取该探测车的实例 (用于执行指令并记录日志)
        Rover rover = commandCenter.getOrCreateRover(roverId);

        // 2. 创建并执行 ShieldCommand (开启电磁防护罩)
        RoverCommand shieldCmd = new ShieldCommand();
        rover.executeCommand(shieldCmd);

        // 3. 创建并执行 MoveCommand (向基地撤离)
        RoverCommand moveCmd = new MoveCommand();
        rover.executeCommand(moveCmd);
    }
}
