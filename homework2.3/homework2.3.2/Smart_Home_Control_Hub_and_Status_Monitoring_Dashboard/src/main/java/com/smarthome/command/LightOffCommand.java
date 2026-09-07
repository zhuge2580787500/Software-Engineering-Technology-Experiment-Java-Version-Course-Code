package com.smarthome.command;

import com.smarthome.receiver.Light;

/**
 * 具体命令：关灯（LightOffCommand）
 * 内部持有 Light 引用，execute() 时调用 Light.turnOff()
 */
public class LightOffCommand implements Command {

    /** 命令对应的接收者：电灯 */
    private final Light light;

    /**
     * 构造关灯命令
     *
     * @param light 要控制的电灯实例
     */
    public LightOffCommand(Light light) {
        this.light = light;
    }

    /**
     * 执行关灯操作
     */
    @Override
    public void execute() {
        System.out.println("[命令执行] 正在执行：关闭" + light.getDeviceName() + "……");
        light.turnOff();
    }
}
