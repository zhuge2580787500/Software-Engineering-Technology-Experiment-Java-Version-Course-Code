/**
 * ============================================================
 * 悬浮式探测车 ({@link Rover} 的具体子类)
 * ============================================================
 * <p>
 * 利用反重力技术悬浮于地表之上的探测车。
 * 移动速度快、对地表无损伤，但抗风能力较弱。
 * </p>
 * ============================================================
 */
package com.deepspace.factory;

import com.deepspace.command.RoverCommand;

public class HoverRover extends Rover {

    /**
     * 构造悬浮式探测车。
     *
     * @param roverId 探测车 ID
     */
    public HoverRover(String roverId) {
        super(roverId, "Hover");
    }

    /**
     * 执行指令：悬浮式探测车执行指令时，将结果记录到日志中。
     *
     * @param command 待执行的指令
     */
    @Override
    public void executeCommand(RoverCommand command) {
        String result = command.execute(this);
        logCommand(result);
    }
}
