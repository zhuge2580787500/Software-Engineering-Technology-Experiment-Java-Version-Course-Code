import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * 矩阵计算程序
 * 支持：加法、减法、乘法、转置、标量乘法
 * 提供菜单界面，全面的输入验证与错误处理
 */
public class MatrixCalculator {

    private static final Scanner scanner = new Scanner(System.in);

    // ---------- 矩阵类 ----------
    static class Matrix {
        private final int rows;
        private final int cols;
        private final double[][] data;

        // 构造空矩阵
        public Matrix(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
            this.data = new double[rows][cols];
        }

        // 从二维数组构造（深拷贝）
        public Matrix(double[][] data) {
            this.rows = data.length;
            this.cols = data[0].length;
            this.data = new double[rows][cols];
            for (int i = 0; i < rows; i++) {
                System.arraycopy(data[i], 0, this.data[i], 0, cols);
            }
        }

        // 获取行数
        public int getRows() { return rows; }

        // 获取列数
        public int getCols() { return cols; }

        // 获取指定元素
        public double get(int i, int j) { return data[i][j]; }

        // 设置指定元素
        public void set(int i, int j, double value) { data[i][j] = value; }

        // 矩阵加法
        public Matrix add(Matrix other) throws IllegalArgumentException {
            if (this.rows != other.rows || this.cols != other.cols) {
                throw new IllegalArgumentException("矩阵维度不同，无法相加");
            }
            Matrix result = new Matrix(rows, cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result.data[i][j] = this.data[i][j] + other.data[i][j];
                }
            }
            return result;
        }

        // 矩阵减法
        public Matrix subtract(Matrix other) throws IllegalArgumentException {
            if (this.rows != other.rows || this.cols != other.cols) {
                throw new IllegalArgumentException("矩阵维度不同，无法相减");
            }
            Matrix result = new Matrix(rows, cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result.data[i][j] = this.data[i][j] - other.data[i][j];
                }
            }
            return result;
        }

        // 矩阵乘法
        public Matrix multiply(Matrix other) throws IllegalArgumentException {
            if (this.cols != other.rows) {
                throw new IllegalArgumentException("第一个矩阵列数不等于第二个矩阵行数，无法相乘");
            }
            Matrix result = new Matrix(this.rows, other.cols);
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < other.cols; j++) {
                    double sum = 0;
                    for (int k = 0; k < this.cols; k++) {
                        sum += this.data[i][k] * other.data[k][j];
                    }
                    result.data[i][j] = sum;
                }
            }
            return result;
        }

        // 矩阵转置
        public Matrix transpose() {
            Matrix result = new Matrix(this.cols, this.rows);
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < this.cols; j++) {
                    result.data[j][i] = this.data[i][j];
                }
            }
            return result;
        }

        // 标量乘法
        public Matrix scalarMultiply(double scalar) {
            Matrix result = new Matrix(this.rows, this.cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    result.data[i][j] = this.data[i][j] * scalar;
                }
            }
            return result;
        }

        // 显示矩阵
        public void display() {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    System.out.printf("%8.2f", data[i][j]);
                }
                System.out.println();
            }
        }

        // 从控制台输入矩阵（静态工厂方法）
        public static Matrix inputMatrix(String name) {
            System.out.println("请输入矩阵 " + name + " 的行数和列数（用空格分隔）：");
            int rows = readPositiveInt("行数");
            int cols = readPositiveInt("列数");
            Matrix matrix = new Matrix(rows, cols);
            System.out.println("请逐行输入矩阵元素（每行 " + cols + " 个数，用空格分隔）：");
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    double val = readDouble("第 " + (i + 1) + " 行第 " + (j + 1) + " 个元素");
                    matrix.data[i][j] = val;
                }
            }
            return matrix;
        }

        // 辅助：读取正整数
        private static int readPositiveInt(String prompt) {
            while (true) {
                try {
                    System.out.print(prompt + "：");
                    int n = scanner.nextInt();
                    if (n <= 0) {
                        System.out.println("请输入正整数！");
                        continue;
                    }
                    return n;
                } catch (InputMismatchException e) {
                    System.out.println("请输入整数！");
                    scanner.next(); // 丢弃无效输入
                }
            }
        }

        // 辅助：读取双精度浮点数
        private static double readDouble(String prompt) {
            while (true) {
                try {
                    System.out.print(prompt + "：");
                    return scanner.nextDouble();
                } catch (InputMismatchException e) {
                    System.out.println("请输入有效数字！");
                    scanner.next();
                }
            }
        }
    }

    // ---------- 主程序 ----------
    public static void main(String[] args) {
        System.out.println("========== 矩阵计算程序 ==========");
        while (true) {
            showMenu();
            int choice = readIntInRange("请选择操作", 0, 6);
            if (choice == 0) {
                System.out.println("感谢使用，再见！");
                break;
            }

            try {
                switch (choice) {
                    case 1 -> doAddition();
                    case 2 -> doSubtraction();
                    case 3 -> doMultiplication();
                    case 4 -> doTranspose();
                    case 5 -> doScalarMultiply();
                    default -> System.out.println("未知选项，请重新选择。");
                }
            } catch (Exception e) {
                System.err.println("操作失败：" + e.getMessage());
            }
            System.out.println();
        }
        scanner.close();
    }

    // 显示菜单
    private static void showMenu() {
        System.out.println("\n请选择操作：");
        System.out.println("1. 矩阵加法");
        System.out.println("2. 矩阵减法");
        System.out.println("3. 矩阵乘法");
        System.out.println("4. 矩阵转置");
        System.out.println("5. 标量乘法（矩阵 × 常数）");
        System.out.println("0. 退出");
        System.out.print("输入选项：");
    }

    // 读取范围内的整数
    private static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt + " (" + min + "-" + max + ")：");
                int n = scanner.nextInt();
                if (n < min || n > max) {
                    System.out.println("请输入 " + min + " 到 " + max + " 之间的整数。");
                    continue;
                }
                return n;
            } catch (InputMismatchException e) {
                System.out.println("请输入整数！");
                scanner.next();
            }
        }
    }

    // 操作1：加法
    private static void doAddition() {
        Matrix A = Matrix.inputMatrix("A");
        Matrix B = Matrix.inputMatrix("B");
        Matrix C = A.add(B);
        System.out.println("\nA + B =");
        C.display();
    }

    // 操作2：减法
    private static void doSubtraction() {
        Matrix A = Matrix.inputMatrix("A");
        Matrix B = Matrix.inputMatrix("B");
        Matrix C = A.subtract(B);
        System.out.println("\nA - B =");
        C.display();
    }

    // 操作3：乘法
    private static void doMultiplication() {
        Matrix A = Matrix.inputMatrix("A");
        Matrix B = Matrix.inputMatrix("B");
        Matrix C = A.multiply(B);
        System.out.println("\nA × B =");
        C.display();
    }

    // 操作4：转置
    private static void doTranspose() {
        Matrix A = Matrix.inputMatrix("A");
        Matrix T = A.transpose();
        System.out.println("\nA 的转置 =");
        T.display();
    }

    // 操作5：标量乘法
    private static void doScalarMultiply() {
        Matrix A = Matrix.inputMatrix("A");
        System.out.print("请输入常数：");
        double scalar = 0;
        while (true) {
            try {
                scalar = scanner.nextDouble();
                break;
            } catch (InputMismatchException e) {
                System.out.println("请输入有效数字！");
                scanner.next();
            }
        }
        Matrix result = A.scalarMultiply(scalar);
        System.out.println("\n" + scalar + " × A =");
        result.display();
    }
}