/**
 * ============================================================
 * 探测车工厂 ({@code RoverFactory})
 * ============================================================
 * <p>
 * 工厂模式 (Factory Pattern) 的实现类。
 * 根据 CSV 数据中的 {@code RoverType} 字段值，创建并返回对应的
 * 具体探测车子类实例。
 * </p>
 * <p>
 * 映射关系：
 * </p>
 * <ul>
 *   <li>{@code "Tracked"}  → {@link TrackedRover}  (履带式)</li>
 *   <li>{@code "Hover"}    → {@link HoverRover}    (悬浮式)</li>
 *   <li>{@code "Walker"}   → {@link WalkerRover}   (步行式)</li>
 * </ul>
 * <p>
 * 设计说明：
 * </p>
 * <ul>
 *   <li>客户端 ({@link CsvDataLoader}) 无需了解具体子类的存在</li>
 *   <li>所有实例化逻辑集中在此工厂类中，便于维护和扩展</li>
 *   <li>遇到未知类型时抛出 {@link IllegalArgumentException} 并给出明确提示</li>
 * </ul>
 * ============================================================
 */
package com.deepspace.factory;

public class RoverFactory {

    /**
     * 根据探测车类型字符串创建对应的探测车实例。
     *
     * @param roverId   探测车 ID
     * @param roverType 探测车类型字符串 (Tracked / Hover / Walker)
     * @return 对应的 Rover 子类实例
     * @throws IllegalArgumentException 如果 roverType 无法识别
     */
    public static Rover create(String roverId, String roverType) {
        if (roverType == null) {
            throw new IllegalArgumentException(
                    "探测车类型不能为空 (RoverID: " + roverId + ")");
        }

        return switch (roverType.trim()) {
            case "Tracked" -> new TrackedRover(roverId);
            case "Hover"   -> new HoverRover(roverId);
            case "Walker"  -> new WalkerRover(roverId);
            default -> throw new IllegalArgumentException(
                    "未知的探测车类型: '" + roverType + "' (RoverID: " + roverId + ")"
                            + "，支持的类型: Tracked, Hover, Walker");
        };
    }

    /**
     * 检查给定的类型字符串是否为合法的探测车类型。
     *
     * @param roverType 类型字符串
     * @return true 表示合法
     */
    public static boolean isValidType(String roverType) {
        if (roverType == null) return false;
        return switch (roverType.trim()) {
            case "Tracked", "Hover", "Walker" -> true;
            default -> false;
        };
    }
}
