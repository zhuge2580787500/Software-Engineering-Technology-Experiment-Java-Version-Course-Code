package com.smarthome.receiver;

/**
 * 具体接收者：空调（AirConditioner）
 * 支持开关操作与温度调节，状态变化时自动触发观察者通知
 */
public class AirConditioner extends SmartDevice {

    /** 当前设定温度（摄氏度），范围 16~30 */
    private int temperature;

    /** 当前运行模式 */
    private String mode;

    public AirConditioner(String deviceName) {
        super(deviceName);
        this.temperature = 26; // 默认 26℃
        this.mode = "制冷";    // 默认制冷模式
    }

    /**
     * 打开空调
     * 状态变化时会自动通知所有观察者
     */
    public void turnOn() {
        if (!isOn) {
            isOn = true;
            stateChanged(getDeviceName() + "已开启，模式：" + mode + "，温度：" + temperature + "℃");
        } else {
            System.out.println(getDeviceName() + "已经是开启状态。");
        }
    }

    /**
     * 关闭空调
     * 状态变化时会自动通知所有观察者
     */
    public void turnOff() {
        if (isOn) {
            isOn = false;
            stateChanged(getDeviceName() + "已关闭");
        } else {
            System.out.println(getDeviceName() + "已经是关闭状态。");
        }
    }

    /**
     * 设置空调温度（设备必须在开启状态下）
     *
     * @param temp 目标温度，范围 16~30℃
     */
    public void setTemperature(int temp) {
        if (!isOn) {
            System.out.println(getDeviceName() + "当前处于关闭状态，无法调节温度。请先开机。");
            return;
        }
        if (temp < 16 || temp > 30) {
            System.out.println(getDeviceName() + "温度设置失败：温度必须在 16~30℃ 之间。");
            return;
        }
        int oldTemp = this.temperature;
        this.temperature = temp;
        stateChanged(getDeviceName() + "温度从 " + oldTemp + "℃ 调整为 " + temperature + "℃，模式：" + mode);
    }

    /**
     * 切换运行模式
     *
     * @param newMode 新模式名称，如"制冷"、"制热"、"送风"等
     */
    public void setMode(String newMode) {
        if (!isOn) {
            System.out.println(getDeviceName() + "当前处于关闭状态，无法切换模式。请先开机。");
            return;
        }
        if (newMode == null || newMode.trim().isEmpty()) {
            System.out.println(getDeviceName() + "模式切换失败：模式名称不能为空。");
            return;
        }
        String oldMode = this.mode;
        this.mode = newMode.trim();
        stateChanged(getDeviceName() + "模式从 " + oldMode + " 切换为 " + mode + "，温度：" + temperature + "℃");
    }

    // ==================== Getter ====================

    public int getTemperature() {
        return temperature;
    }

    public String getMode() {
        return mode;
    }

    @Override
    public String getStatusInfo() {
        String powerStatus = isOn ? "开启" : "关闭";
        String info = getDeviceName() + " - 电源：" + powerStatus;
        if (isOn) {
            info += "，温度：" + temperature + "℃，模式：" + mode;
        }
        return info;
    }
}
