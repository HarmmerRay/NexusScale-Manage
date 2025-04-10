package com.nexuscale.nexusscalemanage.util;


import java.util.Arrays;
import java.util.Scanner;


public class Test1 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNextInt()) { // 注意 while 处理多个 case
            int a = in.nextInt();//组数
            String [][] results = new String[a][1];
            for(int i =0; i<a;i++){
                int b = in.nextInt();  //数组长度
                String arr = in.next();
                char[] arr2 = arr.toCharArray();
                int count = 0;
                int maxCount = 0;
                for(int g=1;g<=b;g++){
                    if (arr2[g-1] == '1'){
                        count++;
                        if (count > maxCount){
                            maxCount = count;
                        }
                    }else{
                        count=0;
                    }
                }
                if(maxCount == 9){
                    results[i][0] = "lucky";
                }else {
                    results[i][0] = "unlucky";
                }
            }
            for(int i =0; i<a;i++){
                System.out.println(results[i][0]);
            }
        }

    }
}
