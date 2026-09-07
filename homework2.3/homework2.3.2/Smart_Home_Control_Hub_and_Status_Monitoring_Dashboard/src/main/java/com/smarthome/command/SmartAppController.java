package com.smarthome.command;

import com.smarthome.receiver.AirConditioner;
import com.smarthome.receiver.Light;

/**
 * 调用者（Invoker）：智能家居 App 控制器
 * 相当于手机 App 界面上的虚拟遥控器，拥有多个按钮插槽（slot），
 * 每个插槽可以绑定一个命令对象，用户点击按钮时通过 {@link #buttonWasPressed(int)} 触发对应命令。
 *
 * <p>不关心命令的具体实现细节，只持有 {@link Command} 接口引用，实现调用与执行的解耦。</p>
 */
public class SmartAppController {

    /** 按钮插槽数量，索引 1~7（模拟常见 App 界面 7 个快捷按钮） */
    private static final int SLOT_COUNT = 7;

    /** 命令存储数组，index 0 未使用，有效索引 1~7 */
    private final Command[] slots;

    /** 各插槽的标签描述，便于日志输出 */
    private final String[] slotLabels;

    /** 当前 App 控制的是哪个房间 */
    private final String roomName;

    /**
     * 构造 App 控制器
     *
     * @param roomName 当前控制的房间名称
     */
    public SmartAppController(String roomName) {
        this.roomName = roomName;
        this.slots = new Command[SLOT_COUNT + 1]; // index 0 unused
        this.slotLabels = new String[SLOT_COUNT + 1];
        initializeSlotLabels();
    }

    // ==================== 核心方法 ====================

    /**
     * 将命令绑定到指定插槽
     *
     * @param slot   插槽编号（1~7）
     * @param command 要绑定的命令对象
     */
    public void setCommand(int slot, Command command) {
        if (slot < 1 || slot > SLOT_COUNT) {
            System.out.println("【控制器】插槽编号无效：" + slot + "，有效范围 1~" + SLOT_COUNT);
            return;
        }
        slots[slot] = command;
        System.out.println("【控制器】已将命令绑定到" + roomName + "的插槽 " + slot
                + "（" + slotLabels[slot] + "）");
    }

    /**
     * 将命令绑定到指定插槽，并自定义插槽标签
     *
     * @param slot       插槽编号（1~7）
     * @param slotLabel  插槽标签（如"客厅主灯"）
     * @param command    要绑定的命令对象
     */
    public void setCommand(int slot, String slotLabel, Command command) {
        if (slot < 1 || slot > SLOT_COUNT) {
            System.out.println("【控制器】插槽编号无效：" + slot + "，有效范围 1~" + SLOT_COUNT);
            return;
        }
        slots[slot] = command;
        slotLabels[slot] = slotLabel;
        System.out.println("【控制器】已将命令绑定到" + roomName + "的插槽 " + slot
                + "（" + slotLabel + "）");
    }

    /**
     * 模拟按下指定插槽的按钮，触发对应命令
     *
     * @param slot 被按下的插槽编号（1~7）
     */
    public void buttonWasPressed(int slot) {
        if (slot < 1 || slot > SLOT_COUNT) {
            System.out.println("【控制器】插槽 " + slot + " 不存在（有效范围 1~" + SLOT_COUNT + "）。");
            return;
        }
        String label = slotLabels[slot] != null ? slotLabels[slot] : ("插槽" + slot);
        System.out.println("\n>>> 用户在" + roomName + "按下按钮：[" + label + "]（插槽 " + slot + "）");

        Command command = slots[slot];
        if (command == null) {
            System.out.println("【控制器】该按钮未绑定任何命令！");
        } else {
            command.execute();
        }
        System.out.println("<<< 命令执行完毕\n");
    }

    /**
     * 打印当前控制器各插槽的绑定情况
     */
    public void printSlotStatus() {
        System.out.println("\n========== " + roomName + " - App 控制器按钮绑定情况 ==========");
        for (int i = 1; i <= SLOT_COUNT; i++) {
            String label = slotLabels[i] != null ? slotLabels[i] : "(空)";
            String status = slots[i] != null
                    ? "已绑定 → " + slots[i].getClass().getSimpleName()
                    : "(未绑定)";
            System.out.printf("  插槽 %d [%s]：%s%n", i, label, status);
        }
        System.out.println("==========================================");
    }

    // ==================== 初始化 ====================

    private void initializeSlotLabels() {
        for (int i = 1; i <= SLOT_COUNT; i++) {
            slotLabels[i] = "按钮" + i;
        }
    }

    // ==================== 便捷工厂方法 ====================

    /**
     * 便捷方法：快速创建一个客厅灯的开灯命令并绑定到指定插槽
     */
    public static SmartAppController createLivingRoomController(Light livingRoomLight) {
        SmartAppController controller = new SmartAppController("客厅");
        controller.setCommand(1, "客厅主灯开", new LightOnCommand(livingRoomLight));
        controller.setCommand(2, "客厅主灯关", new LightOffCommand(livingRoomLight));
        return controller;
    }

    /**
     * 便捷方法：快速创建一个卧室空调控制器并绑定相关命令
     */
    public static SmartAppController createBedroomController(AirConditioner bedroomAC) {
        SmartAppController controller = new SmartAppController("主卧");
        controller.setCommand(1, "空调开机", new ACTurnOnCommand(bedroomAC));
        controller.setCommand(2, "设置26℃", new ACSetTemperatureCommand(bedroomAC, 26));
        controller.setCommand(3, "设置24℃", new ACSetTemperatureCommand(bedroomAC, 24));
        return controller;
    }
}
