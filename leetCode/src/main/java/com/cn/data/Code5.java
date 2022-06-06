package com.cn.data;

/**
 * @Description: Code5
 * @Author: 一方通行
 * @Date: 2022-05-11
 * @Version:v1.0
 */
public class Code5 {

    public static void main(String[] args) {
        int testTime = 100000;
        int maxLen = 5;
        int maxValue = 20;
        for (int i = 0; i <= testTime; i++) {
            int[] arr = randomArray(maxLen, maxValue);
            int ans = oneMinIndex(arr);
            System.out.println(ans);
            if (!check(arr, ans)) {
                printArray(arr);
                System.out.println(ans);
                break;
            }
        }
    }

    // >=num 最左
    public static int mostLeftNoMoreMun(int[] arr, int num) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        int l = 0;
        int r = arr.length - 1;
        // 中间变量
        int ans = -1;
        while (l <= r) {
            // 中点位置
            int mind = (l + r) / 2;
            if (arr[mind] >= num) {
                ans = mind;
                r = mind - 1;
            } else {
                l = mind + 1;
            }
        }
        return ans;
    }


    // 局部最小 arr 整体无序, 相邻的数不相等
    public static int oneMinIndex(int[] arr) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        if (arr.length == 1) {
            return arr[0];
        }
        int n = arr.length;
        if (arr[0] < arr[1]) {
            return 0;
        }
        if (arr[n - 1] < arr[n - 2]) {
            return n - 1;
        }
        // arr 长度大于2
        int l = 0;
        int r = n - 1;
        int ans = -1;
        //l <= r
        while (l < r - 1) {
            // 中点
            int mind = (l + r) / 2;
            // 这里说明中点就是最小值
            if (arr[mind] < arr[mind - 1] && arr[mind] < arr[mind + 1]) {
                return mind;
            }
            //右边放弃 找左边
            if (arr[mind] > arr[mind - 1]) {
                r = mind - 1;
                continue;
            }
            // 左边放弃 找右边
            if (arr[mind] > arr[mind + 1]) {
                l = mind + 1;
                continue;
            }
        }
        return arr[l] < arr[r] ? l : r;
    }

    //生成随机数组 相邻数不相等
    public static int[] randomArray(int maxLen, int value) {
        int len = (int) (Math.random() * maxLen);
        int[] arr = new int[len];
        if (len > 0) {
            arr[0] = (int) (Math.random() * value);
            for (int i = 1; i < len; i++) {
                do {
                    arr[i] = (int) (Math.random() * value);
                } while (arr[i] == arr[i - 1]);
            }
        }
        return arr;
    }

    public static void printArray(int[] array) {
        for (int num : array) {
            System.out.print(num + " ");
        }
        System.out.println();
    }

    // 用于测试
    public static boolean check(int[] arr, int minIndex) {
        if (arr.length == 0) {
            return -1 == minIndex;
        }
        int left = minIndex - 1;
        int right = minIndex + 1;
        boolean leftBigger = left >= 0 ? arr[left] > arr[minIndex] : true;
        boolean rBigger = right < arr.length ? arr[right] > arr[minIndex] : true;
        return leftBigger && rBigger;
    }
}
