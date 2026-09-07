package com.smarthome.command;

import com.smarthome.receiver.Light;

/**
 * 具体命令：开灯（LightOnCommand）
 * 内部持有 Light 引用，execute() 时调用 Light.turnOn()
 */
public class LightOnCommand implements Command {

    /** 命令对应的接收者：电灯 */
    private final Light light;

    /**
     * 构造开灯命令
     *
     * @param light 要控制的电灯实例
     */
    public LightOnCommand(Light light) {
        this.light = light;
    }

    /**
     * 执行开灯操作
     */
    @Override
    public void execute() {
        System.out.println("[命令执行] 正在执行：打开" + light.getDeviceName() + "……");
        light.turnOn();
    }
}
