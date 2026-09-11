/**
 * ============================================================
 * 指挥中心 ({@code CommandCenter})
 * ============================================================
 * <p>
 * 负责管理舰队中所有探测车的指令队列和指令执行日志。
 * </p>
 * <p>
 * 职责：
 * </p>
 * <ol>
 *   <li>管理探测车实例的创建与缓存 (根据 {@code RoverID})</li>
 *   <li>为 {@code CommandCenterObserver} 提供 execute + log 能力</li>
 *   <li>为 Main 菜单选项4 提供指令日志查询接口</li>
 * </ol>
 * <p>
 * 设计说明：
 * </p>
 * <ul>
 *   <li>使用 {@code Map<String, Rover>} 按 {@code RoverID} 缓存探测车实例</li>
 *   <li>如果某个 {@code RoverID} 还没有对应的 {@code Rover} 对象，自动创建默认 {@code TrackedRover}</li>
 *   <li>指令日志存储在 {@code Rover} 对象内部 (通过 {@code logCommand} 方法)</li>
 * </ul>
 * ============================================================
 */
package com.deepspace.command;

import com.deepspace.factory.Rover;

import java.util.*;
import java.util.stream.Collectors;

public class CommandCenter {

    /** 探测车实例缓存：RoverID → Rover 对象 */
    private final Map<String, Rover> roverMap;

    /**
     * 构造指挥中心，初始化空的探测车缓存。
     */
    public CommandCenter() {
        this.roverMap = new HashMap<>();
    }

    /**
     * 获取或创建指定 RoverID 对应的探测车实例。
     * 如果该 RoverID 已在缓存中，直接返回；否则创建一个默认的 TrackedRover。
     *
     * 注意：实际探测车类型信息应从 CSV 数据中获取并提前缓存，
     * 这里作为兜底方案提供默认实例。
     *
     * @param roverId 探测车 ID
     * @return 对应的 Rover 实例
     */
    public Rover getOrCreateRover(String roverId) {
        return roverMap.computeIfAbsent(roverId, id -> {
            // 默认创建 TrackedRover 作为兜底
            // 实际类型应在数据加载阶段由 RoverFactory 创建并注入
            return new com.deepspace.factory.TrackedRover(id);
        });
    }

    /**
     * 注册一个已知类型的探测车实例到指挥中心。
     * 在数据加载阶段，根据 CSV 中的 RoverType 创建 Rover 后调用此方法。
     *
     * @param rover 探测车实例
     */
    public void registerRover(Rover rover) {
        if (rover != null && rover.getRoverId() != null) {
            roverMap.put(rover.getRoverId(), rover);
        }
    }

    /**
     * 获取所有在危机中接收到过指令的探测车日志。
     * 只返回那些至少有一条指令日志的探测车。
     *
     * @return 按键排序的日志条目列表 (格式: "[R-02] 接收指令: ...")
     */
    public List<String> getAllCommandLogs() {
        List<String> allLogs = new ArrayList<>();

        // 按 RoverID 排序输出，保证日志顺序一致性
        List<Rover> rovers = roverMap.values().stream()
                .filter(r -> !r.getCommandLog().isEmpty())
                .sorted(Comparator.comparing(Rover::getRoverId))
                .collect(Collectors.toList());

        for (Rover rover : rovers) {
            for (String log : rover.getCommandLog()) {
                // 包装为菜单选项4要求的格式
                allLogs.add(String.format("[%s] 接收指令: %s... 执行成功: %s",
                        rover.getRoverId(),
                        extractCommandName(log),
                        log));
            }
        }

        return allLogs;
    }

    /**
     * 从指令执行结果字符串中提取指令类型名称。
     * <p>例如: {@code "探测车 R-02 已开启电磁防护罩"} → {@code "ShieldCommand"}</p>
     *
     * @param logEntry 指令日志条目
     * @return 指令类型名称
     */
    private String extractCommandName(String logEntry) {
        if (logEntry.contains("电磁防护罩")) {
            return "ShieldCommand";
        } else if (logEntry.contains("向基地撤离")) {
            return "MoveCommand";
        }
        return "UnknownCommand";
    }

    /**
     * 清空所有探测车的指令日志。
     * 在重新运行分析前调用，避免日志累积。
     */
    public void clearLogs() {
        for (Rover rover : roverMap.values()) {
            rover.getCommandLog().clear();
        }
    }
}
