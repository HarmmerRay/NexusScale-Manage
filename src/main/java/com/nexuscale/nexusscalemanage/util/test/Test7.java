package com.nexuscale.nexusscalemanage.util.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;

public class Test7 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // 从命令行读取输入   如何能从终端读取 9.9*10^99999 对一个的数字展开式？？？
        BigInteger input1 = BigInteger.valueOf(scanner.nextInt());
        BigInteger input2 = new BigInteger("2000000000000");
        if (input1.compareTo(input2) > 0) {
            System.out.println(input1);
        }
        String input = input1.toString();
        try {
            // 将输入字符串转换为 BigDecimal 对象
            BigDecimal number = new BigDecimal(input);
            // 转换为科学计数法字符串
            String scientificNotation = toScientificNotation(number);
            System.out.println(scientificNotation);
        } catch (Exception e) {
            System.out.println("输入无效，请输入一个有效的数字。");
        }
        scanner.close();
    }
    public static String toScientificNotation(BigDecimal number) {
        // 找到指数部分
        int exponent = number.precision() - number.scale() - 1;
        // 计算基数部分
        BigDecimal base = number.movePointLeft(exponent);
        // 构建科学计数法字符串
        return base.stripTrailingZeros().toPlainString() + "E" + exponent;
    }
}

