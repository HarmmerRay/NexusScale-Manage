package com.nexuscale.nexusscalemanage.util.test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Test4 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        // 注意 hasNext 和 hasNextLine 的区别
        while (in.hasNextInt()) { // 注意 while 处理多个 case
            int goodsNum = in.nextInt();
            int couponsNum = in.nextInt();
            int []price = new int[goodsNum];
            for (int i = 0; i < goodsNum; i++) {
                price[i] = in.nextInt();
            }
            int [][] coupons = new int[couponsNum][];
            for (int j = 0; j < couponsNum; j++) {
                int tmp1 = in.nextInt();
                int tmp2 = in.nextInt();
                int []tmp = {tmp1, tmp2};
                coupons[j] = tmp;
            }
            // 门槛最高  价格最高
            Arrays.sort(price);
            Arrays.sort(coupons,new Comparator<int[]>() {

                @Override
                public int compare(int[] o1, int[] o2) {
                    return   o2[0] - o1[0];
                }
            });
            int sum = 0;
            int index = 0;
            for (int i = goodsNum - 1; i >= 0; i--) {
                for(int j = index; j < couponsNum; j++ ) {
                    if (price[i] >= coupons[j][0]){
                        index = j + 1;
                        sum += coupons[j][1];
                        break;
                    }
                }
            }
            System.out.println(sum);
        }
    }
}
