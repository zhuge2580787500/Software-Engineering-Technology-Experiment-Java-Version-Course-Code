/**
 * ============================================================
 * 探测车指令接口 ({@code RoverCommand})
 * ============================================================
 * <p>
 * 命令模式 (Command Pattern) 的核心接口。
 * 定义了所有危机指令的统一行为：{@code execute()} 方法执行具体指令逻辑。
 * </p>
 * <p>
 * 具体指令实现：
 * </p>
 * <ul>
 *   <li>{@link ShieldCommand} : 开启电磁防护罩</li>
 *   <li>{@link MoveCommand}   : 向基地撤离</li>
 * </ul>
 * <p>
 * 指令的"接收者" (Receiver) 是 {@link com.deepspace.factory.Rover} 对象，
 * 指令对象封装了具体的操作，调用方只需调用 {@code execute()} 即可。
 * ============================================================
 */
package com.deepspace.command;

import com.deepspace.factory.Rover;

public interface RoverCommand {

    /**
     * 执行指令的具体逻辑。
     *
     * @param rover 指令的接收者 (探测车)
     * @return 执行结果的描述字符串 (用于记录日志)
     */
    String execute(Rover rover);
}
