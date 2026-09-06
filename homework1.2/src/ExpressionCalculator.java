import java.util.*;

/**
 * 二元表达式计算器
 * 支持 + - * / 和括号，处理负数和空格，中缀转后缀（逆波兰）并计算。
 */
public class ExpressionCalculator {

    // ---------- 词法单元类型 ----------
    enum TokenType {
        NUMBER,         // 数字
        OP_ADD,         // +
        OP_SUB,         // 二元减法 -
        OP_MUL,         // *
        OP_DIV,         // /
        UNARY_MINUS,    // 一元负号
        LPAREN,         // (
        RPAREN          // )
    }

    // ---------- 词法单元 ----------
    static class Token {
        TokenType type;
        double value;          // 仅当 type == NUMBER 时有效
        char operator;         // 仅当 type 为运算符时有效（用于输出调试）

        Token(TokenType type) {
            this.type = type;
        }

        Token(TokenType type, double value) {
            this.type = type;
            this.value = value;
        }

        Token(TokenType type, char operator) {
            this.type = type;
            this.operator = operator;
        }

        @Override
        public String toString() {
            if (type == TokenType.NUMBER) return String.valueOf(value);
            if (type == TokenType.UNARY_MINUS) return "~";  // 标记一元负号
            return String.valueOf(operator);
        }
    }

    // ---------- 词法分析器 ----------
    private static List<Token> tokenize(String expr) throws Exception {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        int len = expr.length();
        TokenType prevType = null;   // 上一个有效token的类型（用于判断一元/二元减号）

        while (i < len) {
            char ch = expr.charAt(i);

            // 跳过空格
            if (Character.isWhitespace(ch)) {
                i++;
                continue;
            }

            // 数字（包括小数）
            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder num = new StringBuilder();
                boolean hasDot = false;
                while (i < len && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    if (expr.charAt(i) == '.') {
                        if (hasDot) throw new Exception("数字格式错误：多个小数点");
                        hasDot = true;
                    }
                    num.append(expr.charAt(i));
                    i++;
                }
                double val = Double.parseDouble(num.toString());
                tokens.add(new Token(TokenType.NUMBER, val));
                prevType = TokenType.NUMBER;
                continue;
            }

            // 运算符或括号
            switch (ch) {
                case '+':
                    // 二元加号前面必须是数字或右括号
                    if (prevType != null && prevType != TokenType.NUMBER && prevType != TokenType.RPAREN)
                        throw new Exception("语法错误：'+' 前缺少操作数");
                    tokens.add(new Token(TokenType.OP_ADD, '+'));
                    prevType = TokenType.OP_ADD;
                    i++;
                    break;

                case '-':
                    // 判断是一元负号还是二元减号
                    boolean isUnary = (prevType == null || prevType == TokenType.LPAREN ||
                            prevType == TokenType.OP_ADD || prevType == TokenType.OP_SUB ||
                            prevType == TokenType.OP_MUL || prevType == TokenType.OP_DIV ||
                            prevType == TokenType.UNARY_MINUS);
                    if (isUnary) {
                        tokens.add(new Token(TokenType.UNARY_MINUS, '~'));
                        prevType = TokenType.UNARY_MINUS;   // 视为运算符
                    } else {
                        // 二元减号前面必须是数字或右括号
                        if (prevType != TokenType.NUMBER && prevType != TokenType.RPAREN)
                            throw new Exception("语法错误：'-' 前缺少操作数");
                        tokens.add(new Token(TokenType.OP_SUB, '-'));
                        prevType = TokenType.OP_SUB;
                    }
                    i++;
                    break;

                case '*':
                    if (prevType == null || (prevType != TokenType.NUMBER && prevType != TokenType.RPAREN))
                        throw new Exception("语法错误：'*' 前缺少操作数");
                    tokens.add(new Token(TokenType.OP_MUL, '*'));
                    prevType = TokenType.OP_MUL;
                    i++;
                    break;

                case '/':
                    if (prevType == null || (prevType != TokenType.NUMBER && prevType != TokenType.RPAREN))
                        throw new Exception("语法错误：'/' 前缺少操作数");
                    tokens.add(new Token(TokenType.OP_DIV, '/'));
                    prevType = TokenType.OP_DIV;
                    i++;
                    break;

                case '(':
                    // 左括号前可以是运算符或开头，但不能是数字或右括号（否则隐含乘法，暂不支持）
                    if (prevType == TokenType.NUMBER || prevType == TokenType.RPAREN)
                        throw new Exception("语法错误：'(' 前不能直接跟数字或右括号，缺少运算符");
                    tokens.add(new Token(TokenType.LPAREN));
                    prevType = TokenType.LPAREN;
                    i++;
                    break;

                case ')':
                    // 右括号前必须是数字或右括号
                    if (prevType == null || (prevType != TokenType.NUMBER && prevType != TokenType.RPAREN))
                        throw new Exception("语法错误：')' 前缺少操作数");
                    tokens.add(new Token(TokenType.RPAREN));
                    prevType = TokenType.RPAREN;
                    i++;
                    break;

                default:
                    throw new Exception("非法字符: " + ch);
            }
        }

        // 最后不能以运算符或左括号结尾
        if (prevType == TokenType.OP_ADD || prevType == TokenType.OP_SUB ||
                prevType == TokenType.OP_MUL || prevType == TokenType.OP_DIV ||
                prevType == TokenType.UNARY_MINUS || prevType == TokenType.LPAREN)
            throw new Exception("表达式末尾缺少操作数或括号不匹配");

        return tokens;
    }

    // ---------- 运算符优先级 ----------
    private static int precedence(TokenType type) {
        switch (type) {
            case UNARY_MINUS: return 4;
            case OP_MUL:
            case OP_DIV:      return 3;
            case OP_ADD:
            case OP_SUB:      return 2;
            default:          return 0;
        }
    }

    // ---------- 中缀转后缀（逆波兰） ----------
    private static List<Token> infixToPostfix(List<Token> tokens) throws Exception {
        List<Token> output = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();

        for (Token token : tokens) {
            switch (token.type) {
                case NUMBER:
                    output.add(token);
                    break;

                case UNARY_MINUS:
                    // 一元运算符直接压栈，优先级最高
                    stack.push(token);
                    break;

                case OP_ADD:
                case OP_SUB:
                case OP_MUL:
                case OP_DIV:
                    while (!stack.isEmpty() && stack.peek().type != TokenType.LPAREN &&
                            precedence(stack.peek().type) >= precedence(token.type)) {
                        output.add(stack.pop());
                    }
                    stack.push(token);
                    break;

                case LPAREN:
                    stack.push(token);
                    break;

                case RPAREN:
                    while (!stack.isEmpty() && stack.peek().type != TokenType.LPAREN) {
                        output.add(stack.pop());
                    }
                    if (stack.isEmpty()) throw new Exception("括号不匹配：多余的右括号");
                    stack.pop(); // 弹出左括号
                    // 如果栈顶是一元负号，需要弹出（因为它属于括号内的整体）
                    if (!stack.isEmpty() && stack.peek().type == TokenType.UNARY_MINUS) {
                        output.add(stack.pop());
                    }
                    break;
            }
        }

        // 弹出栈中剩余运算符
        while (!stack.isEmpty()) {
            Token t = stack.pop();
            if (t.type == TokenType.LPAREN) throw new Exception("括号不匹配：缺少右括号");
            output.add(t);
        }

        return output;
    }

    // ---------- 计算后缀表达式 ----------
    private static double evaluatePostfix(List<Token> postfix) throws Exception {
        Deque<Double> stack = new ArrayDeque<>();

        for (Token token : postfix) {
            switch (token.type) {
                case NUMBER:
                    stack.push(token.value);
                    break;

                case UNARY_MINUS:
                    if (stack.isEmpty()) throw new Exception("计算错误：一元负号缺少操作数");
                    double val = -stack.pop();
                    stack.push(val);
                    break;

                case OP_ADD:
                    if (stack.size() < 2) throw new Exception("计算错误：'+' 缺少操作数");
                    double b = stack.pop(), a = stack.pop();
                    stack.push(a + b);
                    break;

                case OP_SUB:
                    if (stack.size() < 2) throw new Exception("计算错误：'-' 缺少操作数");
                    b = stack.pop(); a = stack.pop();
                    stack.push(a - b);
                    break;

                case OP_MUL:
                    if (stack.size() < 2) throw new Exception("计算错误：'*' 缺少操作数");
                    b = stack.pop(); a = stack.pop();
                    stack.push(a * b);
                    break;

                case OP_DIV:
                    if (stack.size() < 2) throw new Exception("计算错误：'/' 缺少操作数");
                    b = stack.pop(); a = stack.pop();
                    if (b == 0) throw new Exception("除零错误");
                    stack.push(a / b);
                    break;

                default:
                    throw new Exception("未知的token类型");
            }
        }

        if (stack.size() != 1) throw new Exception("计算错误：表达式不完整");
        return stack.pop();
    }

    // ---------- 对外接口 ----------
    public static double calculate(String expr) throws Exception {
        List<Token> tokens = tokenize(expr);
        List<Token> postfix = infixToPostfix(tokens);
        return evaluatePostfix(postfix);
    }

    // ---------- 主程序 ----------
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入一个数学表达式 (支持 + - * / 和括号，可含负数): ");
        String expr = scanner.nextLine().trim();

        try {
            double result = calculate(expr);
            System.out.println("计算结果: " + result);
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}