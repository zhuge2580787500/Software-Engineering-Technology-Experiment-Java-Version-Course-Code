import java.math.BigInteger;
import java.util.Scanner;

/**
 * 打印 Fibonacci 数列 F1 ~ Fn，处理异常输入。
 */
public class Fibonacci {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入一个正整数 n: ");

        int n = 0;
        try {
            n = scanner.nextInt();          // 可能抛出 InputMismatchException
        } catch (Exception e) {
            System.out.println("输入错误：请输入一个整数。");
            scanner.close();
            return;
        }

        if (n <= 0) {
            System.out.println("n 必须为正整数（≥ 1）。");
            scanner.close();
            return;
        }

        printFibonacci(n);
        scanner.close();
    }

    public static void printFibonacci(int n) {
        if (n >= 1) {
            System.out.print("F₁ = 1");
        }
        if (n >= 2) {
            System.out.print(", F₂ = 1");
        }

        BigInteger a = BigInteger.ONE;   // F₁
        BigInteger b = BigInteger.ONE;   // F₂
        for (int i = 3; i <= n; i++) {
            BigInteger c = a.add(b);     // Fᵢ = Fᵢ₋₁ + Fᵢ₋₂
            System.out.print(", F" + i + " = " + c);
            a = b;
            b = c;
        }
        System.out.println();
    }
}