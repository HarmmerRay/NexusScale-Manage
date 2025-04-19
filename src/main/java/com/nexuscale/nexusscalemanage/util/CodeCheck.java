package com.nexuscale.nexusscalemanage.util;

import java.util.Random;

public class CodeCheck {
    public static boolean checkCode(String phone_number,String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }else{
            String value = Redis.getValue(phone_number);
            System.out.println("code" + code + "redis_code" + value);
            return value.equals(code);
        }
    }
    public static String generateCode() {
        return String.valueOf(new Random().nextInt(1000,9999));
    }

    public static void main(String[] args) {
        System.out.println(generateCode());
    }
}
