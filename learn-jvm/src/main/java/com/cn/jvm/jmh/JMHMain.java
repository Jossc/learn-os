package com.cn.jvm.jmh;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * {@code @Description: JMHMain}
 * {@code @Author: 一方通行 }
 * {@code @Date: 2024-11-16}
 * {@code @Version:v1.0}
 */
public class JMHMain {
    static List<Integer> nums = new ArrayList<>();

    static {
        Random r = new Random();
        for (int i = 0; i < 10000; i++) {
            nums.add(1000000 + r.nextInt(1000000));
        }
    }

    public static void foreach() {
        nums.forEach(v -> isPrime(v));
    }

    static void parallel() {
        nums.parallelStream().forEach(JMHMain::isPrime);
    }

    static boolean isPrime(int num) {
        for (int i = 2; i <= num / 2; i++) {
            if (num % i == 0) return false;
        }
        return true;
    }
}
