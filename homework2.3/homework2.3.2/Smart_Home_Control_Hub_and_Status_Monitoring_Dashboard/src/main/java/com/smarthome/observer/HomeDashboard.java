package com.smarthome.observer;

import com.smarthome.subject.DeviceSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * 具体观察者：家庭状态看板（HomeDashboard）
 * 实时展示所有已注册智能设备的最新状态
 */
public class HomeDashboard implements DeviceObserver {

    /** 看板所关注的设备列表 */
    private final List<DeviceSubject> registeredDevices;

    public HomeDashboard() {
        this.registeredDevices = new ArrayList<>();
    }

    /**
     * 向看板注册一个新设备
     *
     * @param device 要注册的智能设备
     */
    public void registerDevice(DeviceSubject device) {
        if (device == null) {
            System.out.println("【看板】注册设备失败：设备不能为空。");
            return;
        }
        if (!registeredDevices.contains(device)) {
            registeredDevices.add(device);
            device.registerObserver(this);
            System.out.println("【看板】已注册新设备：" + device.getClass().getSimpleName());
        }
    }

    /**
     * 从看板移除一个设备
     *
     * @param device 要移除的智能设备
     */
    public void unregisterDevice(DeviceSubject device) {
        if (device == null) {
            System.out.println("【看板】移除设备失败：设备不能为空。");
            return;
        }
        if (registeredDevices.contains(device)) {
            registeredDevices.remove(device);
            device.removeObserver(this);
            System.out.println("【看板】已移除设备：" + device.getClass().getSimpleName());
        }
    }

    /**
     * 当被观察的设备状态发生变化时，看板自动调用此方法进行更新
     *
     * @param message 状态变化描述信息
     */
    @Override
    public void update(String message) {
        System.out.println("【看板更新】" + message);
    }

    /**
     * 打印当前看板显示的所有设备状态摘要
     */
    public void printStatusSummary() {
        System.out.println("\n========== 家庭状态看板 ==========");
        for (DeviceSubject device : registeredDevices) {
            String statusInfo = device.getStatusInfo();
            System.out.println("  " + statusInfo);
        }
        System.out.println("====================================");
    }
}
