package com.smarthome;

import com.smarthome.command.ACSetTemperatureCommand;
import com.smarthome.command.ACTurnOnCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.command.LightOnCommand;
import com.smarthome.command.SmartAppController;
import com.smarthome.observer.HomeDashboard;
import com.smarthome.receiver.AirConditioner;
import com.smarthome.receiver.Light;

/**
 * 智能家居控制中枢与状态监控看板 - 客户端测试入口
 *
 * <p>测试流程：
 * <ol>
 *   <li>实例化智能设备（电灯、空调）</li>
 *   <li>创建家庭状态看板并注册为各设备的观察者</li>
 *   <li>实例化具体命令并绑定到 App 控制器的按钮插槽</li>
 *   <li>模拟用户按下控制器按钮，验证命令执行与看板自动更新</li>
 * </ol>
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("      智能家居控制中枢与状态监控看板 - 测试开始");
        System.out.println("==========================================================");

        // ==================== 第一步：实例化智能设备 ====================
        System.out.println("\n--- 第一步：实例化智能设备 ---");
        Light livingRoomLight = new Light("客厅主灯");
        Light bedroomLight = new Light("卧室床头灯");
        AirConditioner bedroomAC = new AirConditioner("主卧空调");

        // ==================== 第二步：创建看板并注册观察者 ====================
        System.out.println("\n--- 第二步：创建家庭状态看板并注册设备 ---");
        HomeDashboard dashboard = new HomeDashboard();
        dashboard.registerDevice(livingRoomLight);
        dashboard.registerDevice(bedroomLight);
        dashboard.registerDevice(bedroomAC);

        // 打印初始状态
        dashboard.printStatusSummary();

        // ==================== 第三步：创建 App 控制器并绑定命令 ====================
        System.out.println("\n--- 第三步：创建 App 控制器并绑定命令 ---");

        // 客厅灯控制器
        SmartAppController livingRoomCtrl = SmartAppController.createLivingRoomController(livingRoomLight);

        // 主卧空调控制器
        SmartAppController bedroomCtrl = SmartAppController.createBedroomController(bedroomAC);
        // 额外绑定：卧室灯开关
        bedroomCtrl.setCommand(4, "卧室床头灯开", new LightOnCommand(bedroomLight));
        bedroomCtrl.setCommand(5, "卧室床头灯关", new LightOffCommand(bedroomLight));

        // 打印绑定情况
        livingRoomCtrl.printSlotStatus();
        bedroomCtrl.printSlotStatus();

        // ==================== 第四步：模拟用户按下按钮 ====================
        System.out.println("\n--- 第四步：模拟用户操作（按下 App 按钮）---");

        // 场景一：用户打开客厅主灯
        livingRoomCtrl.buttonWasPressed(1);

        // 场景二：用户打开主卧空调并设置温度为 24℃
        bedroomCtrl.buttonWasPressed(1); // 开空调
        bedroomCtrl.buttonWasPressed(3); // 调温到 24℃

        // 场景三：用户打开卧室床头灯
        bedroomCtrl.buttonWasPressed(4);

        // 场景四：用户关闭客厅主灯
        livingRoomCtrl.buttonWasPressed(2);

        // ==================== 第五步：查看最终看板状态 ====================
        System.out.println("\n--- 第五步：查看最终看板状态 ---");
        dashboard.printStatusSummary();

        // ==================== 测试边界情况 ====================
        System.out.println("\n--- 边界情况测试 ---");

        // 测试：关闭的设备上设置温度（应提示失败）
        bedroomCtrl.buttonWasPressed(2); // 空调已开启，先关闭它
        bedroomAC.turnOff();             // 直接关闭
        bedroomCtrl.buttonWasPressed(3); // 尝试在关闭状态下调温

        // 测试：看板移除设备
        System.out.println();
        dashboard.unregisterDevice(bedroomLight);
        bedroomLight.turnOn(); // 状态变化，但看板不再收到通知

        // ==================== 打印最终看板状态 ====================
        dashboard.printStatusSummary();

        System.out.println("\n==========================================================");
        System.out.println("      所有测试用例执行完毕。");
        System.out.println("==========================================================");
    }
}
