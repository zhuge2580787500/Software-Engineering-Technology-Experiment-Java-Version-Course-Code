/**
 * ============================================================
 * 电磁防护罩指令 ({@link RoverCommand} 的具体实现)
 * ============================================================
 * <p>
 * 命令模式的具体实现类。
 * 执行时向指定探测车下达开启电磁防护罩的指令。
 * </p>
 * <p>
 * 执行效果：
 * </p>
 * <blockquote>{@code 探测车 {RoverID} 已开启电磁防护罩}</blockquote>
 * ============================================================
 */
package com.deepspace.command;

import com.deepspace.factory.Rover;

public class ShieldCommand implements RoverCommand {

    @Override
    public String execute(Rover rover) {
        String result = String.format("探测车 %s 已开启电磁防护罩", rover.getRoverId());
        System.out.println(result);
        return result;
    }
}
