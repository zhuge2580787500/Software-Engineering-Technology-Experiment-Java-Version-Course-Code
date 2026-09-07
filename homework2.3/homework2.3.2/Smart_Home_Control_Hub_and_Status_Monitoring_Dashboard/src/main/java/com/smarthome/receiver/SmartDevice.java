package com.smarthome.receiver;

import com.smarthome.observer.DeviceObserver;
import com.smarthome.subject.DeviceSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * 智能设备抽象基类（Receiver 模板）
 * 实现了被观察者（Subject）的核心逻辑：观察者列表的维护与通知分发
 * 子类只需关注自身业务逻辑，通过调用 {@link #stateChanged(String)} 触发通知
 */
public abstract class SmartDevice implements DeviceSubject {

    /** 设备名称，用于识别设备 */
    protected final String deviceName;

    /** 当前是否开机 */
    protected boolean isOn;

    /** 注册的观察者列表 */
    private final List<DeviceObserver> observers;

    protected SmartDevice(String deviceName) {
        this.deviceName = deviceName;
        this.isOn = false; // 默认关闭
        this.observers = new ArrayList<>();
    }

    // ==================== Subject 接口实现 ====================

    @Override
    public void registerObserver(DeviceObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(DeviceObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (DeviceObserver observer : observers) {
            observer.update(message);
        }
    }

    // ==================== 受保护的工具方法 ====================

    /**
     * 设备状态发生改变时调用此方法，自动向所有观察者推送通知
     *
     * @param changeDescription 状态变化的描述
     */
    protected void stateChanged(String changeDescription) {
        notifyObservers(changeDescription);
    }

    /**
     * 子类必须实现此方法，返回设备当前状态的字符串表示
     */
    @Override
    public abstract String getStatusInfo();

    // ==================== Getter ====================

    public String getDeviceName() {
        return deviceName;
    }

    public boolean isOn() {
        return isOn;
    }
}
