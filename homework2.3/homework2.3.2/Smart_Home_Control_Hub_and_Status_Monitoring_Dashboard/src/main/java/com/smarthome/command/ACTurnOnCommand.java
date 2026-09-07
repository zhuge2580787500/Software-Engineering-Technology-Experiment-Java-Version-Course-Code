package com.smarthome.command;

import com.smarthome.receiver.AirConditioner;

/**
 * 具体命令：打开空调（ACTurnOnCommand）
 * 内部持有 AirConditioner 引用，execute() 时调用 AC.turnOn()
 */
public class ACTurnOnCommand implements Command {

    /** 命令对应的接收者：空调 */
    private final AirConditioner airConditioner;

    /**
     * 构造开空调命令
     *
     * @param airConditioner 要控制的空调实例
     */
    public ACTurnOnCommand(AirConditioner airConditioner) {
        this.airConditioner = airConditioner;
    }

    /**
     * 执行开空调操作
     */
    @Override
    public void execute() {
        System.out.println("[命令执行] 正在执行：打开" + airConditioner.getDeviceName() + "……");
        airConditioner.turnOn();
    }
}
