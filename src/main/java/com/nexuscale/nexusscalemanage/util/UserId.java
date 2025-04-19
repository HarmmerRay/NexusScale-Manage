package com.nexuscale.nexusscalemanage.util;

import java.util.Date;
import java.util.UUID;

public class UserId {
    public static String getUserId(String phone_number) {
        StringBuilder result = new StringBuilder();
        result.append(phone_number);
        result.append("-");
        result.append(new Date().getTime());
        System.out.println(result.toString());
        return result.toString();
    }
    public static void main(String[] args) {
        getUserId("13290824341");
    }
}
