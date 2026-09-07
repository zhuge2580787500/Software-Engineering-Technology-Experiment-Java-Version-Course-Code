package com.smarthome.command;

import com.smarthome.receiver.AirConditioner;

/**
 * 具体命令：设置空调温度（ACSetTemperatureCommand）
 * 内部持有 AirConditioner 引用和目标温度，execute() 时调用 AC.setTemperature()
 */
public class ACSetTemperatureCommand implements Command {

    /** 命令对应的接收者：空调 */
    private final AirConditioner airConditioner;

    /** 目标温度 */
    private final int targetTemperature;

    /**
     * 构造设置空调温度命令
     *
     * @param airConditioner   要控制的空调实例
     * @param targetTemperature 目标温度（16~30℃）
     */
    public ACSetTemperatureCommand(AirConditioner airConditioner, int targetTemperature) {
        this.airConditioner = airConditioner;
        this.targetTemperature = targetTemperature;
    }

    /**
     * 执行设置温度操作
     */
    @Override
    public void execute() {
        System.out.println("[命令执行] 正在执行：将" + airConditioner.getDeviceName()
                + "温度设置为 " + targetTemperature + "℃……");
        airConditioner.setTemperature(targetTemperature);
    }

    /**
     * 获取此命令的目标温度
     */
    public int getTargetTemperature() {
        return targetTemperature;
    }
}
