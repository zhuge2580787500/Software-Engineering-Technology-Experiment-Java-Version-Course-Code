package com.smarthome.observer;

/**
 * 观察者接口（Observer）
 * 任何需要响应设备状态变化的组件（如状态看板）需实现此接口
 */
public interface DeviceObserver {

    /**
     * 当被观察的智能设备状态发生变化时，此方法被调用
     *
     * @param message 状态变化描述信息
     */
    void update(String message);
}
