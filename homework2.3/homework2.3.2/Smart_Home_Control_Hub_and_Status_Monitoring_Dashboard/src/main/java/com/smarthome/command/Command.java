package com.smarthome.command;

/**
 * 命令接口（Command）
 * 所有具体命令类均实现此接口，提供统一的 execute() 方法供调用者（Invoker）触发
 */
public interface Command {

    /**
     * 执行命令，调用具体接收者（Receiver）完成实际操作
     */
    void execute();
}
