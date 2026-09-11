/**
 * ============================================================
 * 履带式探测车 ({@link Rover} 的具体子类)
 * ============================================================
 * <p>
 * 适用于复杂地形的履带式探测车。
 * 具备较强的越野能力和承重能力，适合崎岖地表作业。
 * </p>
 * ============================================================
 */
package com.deepspace.factory;

import com.deepspace.command.RoverCommand;

public class TrackedRover extends Rover {

    /**
     * 构造履带式探测车。
     *
     * @param roverId 探测车 ID
     */
    public TrackedRover(String roverId) {
        super(roverId, "Tracked");
    }

    /**
     * 执行指令：履带式探测车执行指令时，将结果记录到日志中。
     * 具体指令逻辑由传入的 RoverCommand 实现类决定。
     *
     * @param command 待执行的指令
     */
    @Override
    public void executeCommand(RoverCommand command) {
        String result = command.execute(this);
        logCommand(result);
    }
}
