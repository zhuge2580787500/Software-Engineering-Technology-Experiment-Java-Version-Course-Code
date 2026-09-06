import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * 冒泡排序（升序）：从控制台输入 10 个数字，排序后打印。
 * 处理各类输入异常。
 */
public class BubbleSort {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double[] numbers = new double[10];
        int count = 0;

        // 循环读取 10 个数字，遇到非数字则提示重新输入
        while (count < 10) {
            System.out.print("请输入第 " + (count + 1) + " 个数字: ");
            try {
                numbers[count] = scanner.nextDouble();
                count++;
            } catch (InputMismatchException e) {
                System.out.println("输入错误：请输入一个有效的数字（如 3.14 或 5）。");
                scanner.next(); // 丢弃无效输入，防止死循环
            } catch (NoSuchElementException e) {
                // 捕获 EOF（如 Ctrl+Z / Ctrl+D），提前结束
                System.out.println("\n输入已终止，剩余数字未提供。");
                scanner.close();
                return;
            }
        }

        // 冒泡排序（从小到大）
        for (int i = 0; i < numbers.length - 1; i++) {
            for (int j = 0; j < numbers.length - 1 - i; j++) {
                if (numbers[j] > numbers[j + 1]) {
                    double temp = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temp;
                }
            }
        }

        // 打印排序结果
        System.out.print("排序后的数字序列: ");
        for (int i = 0; i < numbers.length; i++) {
            if (i > 0) System.out.print(", ");
            System.out.print(numbers[i]);
        }
        System.out.println();

        scanner.close();
    }
}