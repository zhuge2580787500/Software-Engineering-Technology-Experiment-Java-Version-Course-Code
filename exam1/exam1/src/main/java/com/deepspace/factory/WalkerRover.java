/**
 * ============================================================
 * 步行式探测车 ({@link Rover} 的具体子类)
 * ============================================================
 * <p>
 * 仿生多足步行探测车，具备极高的地形适应性。
 * 适合在岩石、陡坡等履带和悬浮式探测车难以抵达的区域作业。
 * </p>
 * ============================================================
 */
package com.deepspace.factory;

import com.deepspace.command.RoverCommand;

public class WalkerRover extends Rover {

    /**
     * 构造步行式探测车。
     *
     * @param roverId 探测车 ID
     */
    public WalkerRover(String roverId) {
        super(roverId, "Walker");
    }

    /**
     * 执行指令：步行式探测车执行指令时，将结果记录到日志中。
     *
     * @param command 待执行的指令
     */
    @Override
    public void executeCommand(RoverCommand command) {
        String result = command.execute(this);
        logCommand(result);
    }
}
