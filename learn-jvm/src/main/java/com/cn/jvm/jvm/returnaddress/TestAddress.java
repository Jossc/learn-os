package com.cn.jvm.jvm.returnaddress;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @Description: TestAddress
 * @Author: 一方通行
 * @Date: 2021-12-29
 * @Version:v1.0
 */
public class TestAddress {

    public static void main(String[] args) {
        giveMeThatOldFashionedBoolean(true);
    }

    static int giveMeThatOldFashionedBoolean(boolean bVal) {
        try {
            if (bVal) {
                return 1;
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException();
        }
        /*finally {
            System.out.println("Got old fashioned.");
        }*/
    }

    static void giveMeThatOldFashionedBoolean() {
        int a = 0;
        System.out.println(a);
        int b = 1;
    }

    static void giveMeThatOldFashionedBoolean1() {
        {
            int a = 0;
            System.out.println(a);
        }
        Stream.of(Arrays.asList("1")).forEach(System.out::println);
        int b = 1;
    }
}
