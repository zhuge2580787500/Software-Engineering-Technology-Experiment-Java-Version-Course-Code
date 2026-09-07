package com.smarthome.receiver;

/**
 * 具体接收者：电灯（Light）
 * 支持开关操作与亮度调节，状态变化时自动触发观察者通知
 */
public class Light extends SmartDevice {

    /** 亮度等级，范围 0~100 */
    private int brightness;

    public Light(String deviceName) {
        super(deviceName);
        this.brightness = 0; // 关闭时亮度为 0
    }

    /**
     * 开灯操作
     * 状态变化时会自动通知所有观察者
     */
    public void turnOn() {
        if (!isOn) {
            isOn = true;
            brightness = 80; // 默认亮度 80
            stateChanged(getDeviceName() + "已开启，亮度：" + brightness + "%");
        } else {
            System.out.println(getDeviceName() + "已经是开启状态。");
        }
    }

    /**
     * 关灯操作
     * 状态变化时会自动通知所有观察者
     */
    public void turnOff() {
        if (isOn) {
            isOn = false;
            brightness = 0;
            stateChanged(getDeviceName() + "已关闭");
        } else {
            System.out.println(getDeviceName() + "已经是关闭状态。");
        }
    }

    /**
     * 调节亮度（设备必须在开启状态下）
     *
     * @param level 目标亮度，范围 1~100
     */
    public void setBrightness(int level) {
        if (!isOn) {
            System.out.println(getDeviceName() + "当前处于关闭状态，无法调节亮度。请先开灯。");
            return;
        }
        if (level < 1 || level > 100) {
            System.out.println(getDeviceName() + "亮度调节失败：亮度必须在 1~100 之间。");
            return;
        }
        this.brightness = level;
        stateChanged(getDeviceName() + "亮度已调整为：" + brightness + "%");
    }

    // ==================== Getter ====================

    public int getBrightness() {
        return brightness;
    }

    @Override
    public String getStatusInfo() {
        String powerStatus = isOn ? "开启" : "关闭";
        String info = getDeviceName() + " - 电源：" + powerStatus;
        if (isOn) {
            info += "，亮度：" + brightness + "%";
        }
        return info;
    }
}
