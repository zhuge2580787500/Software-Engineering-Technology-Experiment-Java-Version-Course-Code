package com.ecommerce;

/**
 * 全局配置管理器（单例模式）
 *
 * <p>采用 Bill Pugh 静态内部类方式实现单例，该方式：
 * <ul>
 *   <li>无需 synchronized，线程安全</li>
 *   <li>懒加载 — 只有第一次调用 getInstance() 时才初始化实例</li>
 *   <li>由 JVM 保证类初始化的线程安全</li>
 * </ul>
 *
 * <p>职责：存储全局唯一的配置信息，供整个系统共享访问。
 */
public class GlobalConfigManager {

    /**
     * 平台名称
     */
    private final String platformName;

    /**
     * 全局基础税率
     */
    private final double taxRate;

    // ---- 私有构造器，禁止外部实例化 ----
    private GlobalConfigManager() {
        // 模拟从配置文件读取，实际项目中可由外部注入
        this.platformName = "天天购物";
        this.taxRate = 0.05;
    }

    // ---- 静态内部类 — 持有单例实例 ----
    private static class Holder {
        private static final GlobalConfigManager INSTANCE = new GlobalConfigManager();
    }

    /**
     * 获取全局唯一实例的访问点
     *
     * @return GlobalConfigManager 单例实例
     */
    public static GlobalConfigManager getInstance() {
        return Holder.INSTANCE;
    }

    // ---- Getter ----
    public String getPlatformName() {
        return platformName;
    }

    public double getTaxRate() {
        return taxRate;
    }

    @Override
    public String toString() {
        return "GlobalConfigManager{" +
                "platformName='" + platformName + '\'' +
                ", taxRate=" + taxRate +
                '}';
    }
}
