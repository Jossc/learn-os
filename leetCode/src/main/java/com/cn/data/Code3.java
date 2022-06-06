package com.cn.data;

/**
 * 冒泡
 *
 * @Description: Code3
 * @Author: 一方通行
 * @Date: 2022-05-10
 * @Version:v1.0
 */
public class Code3 {
    public static int[] help;

    public static void main(String[] args) {
        int[] arr = {7, 8, 34, 4, 5, 67, 123, 454556, 2, 5};
        printSort(arr);
        selectSort(arr);
        printSort(arr);

        prefixNums(arr);
        final int i = rangeSum(0, 2);
        System.out.println(i);


    }

    /**
     * 选择排序
     * 0~n-1 找到最小智
     *
     * @param arr
     */
    public static void selectSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            //最小值
            int minValue = i;
            for (int j = i + 1; j < n; j++) {
                //交换坐标
                minValue = arr[minValue] > arr[j] ? j : minValue;
            }
            swap(arr, i, minValue);
        }

    }


    public static void bubbleSort(int[] arr) {
        int n = arr.length;

        for (int end = n - 1; end >= 0; end--) {
            for (int second = 1; second <= end; second++) {
                if (arr[second - 1] > arr[second]) {
                    swap(arr, second - 1, second);
                }
            }

        }
    }


    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[j];
        arr[j] = arr[i];
        arr[i] = tmp;
    }

    public static void printSort(int[] arr) {
        for (int j : arr) {
            System.out.print(j + " ");
        }
        System.out.println();
    }


    // 前缀和数据
    public static void prefixNums(int[] arr) {
        if (arr == null) {
            return;
        }
        help = new int[arr.length];
        help[0] = arr[0];
        for (int i = 1; i < arr.length; i++) {
            // help 数组里边的i的位置加arr
            help[i] = help[i - 1] + arr[i];
        }
    }

    public static int rangeSum(int l, int r) {
        return l == 0 ? help[r] : help[r] - help[l - 1];
    }


}
