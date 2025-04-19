package com.nexuscale.nexusscalemanage.util;

import java.util.regex.Pattern;

public class PhoneRegexCheck {
    public static boolean isValidPhoneNumber(String phone) {
        // 定义手机号码的正则表达式
        String regex = "^1[3-9]\\d{9}$";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 检查字符串是否匹配正则表达式
        return pattern.matcher(phone).matches();
    }

    public static void main(String[] args) {
        String phone = "13800138000";
        if (isValidPhoneNumber(phone)) {
            System.out.println(phone + " 是一个合格的手机号码。");
        } else {
            System.out.println(phone + " 不是一个合格的手机号码。");
        }
    }
}