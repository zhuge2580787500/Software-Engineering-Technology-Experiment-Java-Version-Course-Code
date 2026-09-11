/**
 * ============================================================
 * 基地撤离指令 ({@link RoverCommand} 的具体实现)
 * ============================================================
 * <p>
 * 命令模式的具体实现类。
 * 执行时向指定探测车下达向基地撤离的指令。
 * </p>
 * <p>
 * 执行效果：
 * </p>
 * <blockquote>{@code 探测车 {RoverID} 正在向基地撤离}</blockquote>
 * ============================================================
 */
package com.deepspace.command;

import com.deepspace.factory.Rover;

public class MoveCommand implements RoverCommand {

    @Override
    public String execute(Rover rover) {
        String result = String.format("探测车 %s 正在向基地撤离", rover.getRoverId());
        System.out.println(result);
        return result;
    }
}
