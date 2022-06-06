package com.cn.data;

/**
 * @Description: Code01
 * @Author: 一方通行
 * @Date: 2022-05-09
 * @Version:v1.0
 */
public class Code01 {
    public static void main(String[] args) {
        int num = Integer.MIN_VALUE;
        print(num);
        print(~num);
        int a = 1;
        int b = ~a + 1;
        System.out.println(a);
        System.out.println(b);
        System.out.println(~num);
    }

    private static void print(int num) {
        for (int i = 31; i >= 0; i--) {
            System.out.print(((num & (1 << i)) == 0 ? "0" : "1"));
        }
        System.out.println("-----");
    }
}
