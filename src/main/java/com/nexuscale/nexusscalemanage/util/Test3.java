package com.nexuscale.nexusscalemanage.util;

import java.util.Arrays;
import java.util.Scanner;

public class Test3 {
    public static String solve(int len1,int len2,char[] arr1,char[] arr2){
        Arrays.sort(arr1);
        char[] res = new char[len1];
        if (arr2[0] > arr1[0]){
            return "-1";
        }
        return "-1";
    }
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNextInt()) { // 注意 while 处理多个 case
            int groupNum = in.nextInt();
            String []results = new String[groupNum];
            for (int i = 0; i < groupNum; i++) {
                int s1_len = in.nextInt();
                int s2_len = in.nextInt();
                String s1_str = in.nextLine();
                String s2_str = in.nextLine();
                char[] arr1 = s1_str.toCharArray();
                char[] arr2 = s2_str.toCharArray();
                results[i] = solve(s1_len,s2_len,arr1,arr2);
            }
        }
    }
}
