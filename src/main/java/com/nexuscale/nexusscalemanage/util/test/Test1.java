package com.nexuscale.nexusscalemanage.util.test;


import java.math.BigDecimal;
import java.math.RoundingMode;


public class Test1 {
    public static void main(String[] args) {
        BigDecimal numerator = new BigDecimal(1);
        BigDecimal denominator = new BigDecimal(7);
        BigDecimal result = numerator.divide(denominator, 20, RoundingMode.HALF_UP);
        System.out.println(result);
    }
}
