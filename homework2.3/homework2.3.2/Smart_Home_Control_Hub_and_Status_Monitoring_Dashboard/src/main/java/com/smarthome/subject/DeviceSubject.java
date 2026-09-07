package com.smarthome.subject;

import com.smarthome.observer.DeviceObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * 被观察者主题接口（Subject）
 * 所有智能设备需实现此接口以支持状态变化通知
 */
public interface DeviceSubject {

    /**
     * 注册观察者
     *
     * @param observer 要注册的观察者
     */
    void registerObserver(DeviceObserver observer);

    /**
     * 移除观察者
     *
     * @param observer 要移除的观察者
     */
    void removeObserver(DeviceObserver observer);

    /**
     * 通知所有已注册的观察者
     *
     * @param message 状态变化描述信息
     */
    void notifyObservers(String message);

    /**
     * 获取设备当前状态的摘要信息，供看板展示使用
     *
     * @return 设备状态描述字符串
     */
    String getStatusInfo();
}
