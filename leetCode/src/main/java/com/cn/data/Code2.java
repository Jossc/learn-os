package com.cn.data;

/**
 * 选择排序
 *
 * @Description: Code2
 * @Author: 一方通行
 * @Date: 2022-05-09
 * @Version:v1.0
 */
public class Code2 {
    public static void main(String[] args) {
        int[] arr = {7, 8, 34, 4, 5, 67, 123, 454556, 2, 5};
        printSort(arr);
        insertSort(arr);
        printSort(arr);

    }

    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[j];
        arr[j] = arr[i];
        arr[i] = tmp;

    }

    public static void selectSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        //0~n-1 找到最小值替换下标
        //1~n-1
        for (int i = 0; i < arr.length; i++) {
            //最小值变量
            int minValueIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                minValueIndex = arr[j] < arr[minValueIndex] ? j : minValueIndex;
            }
            swap(arr, i, minValueIndex);
        }
    }

    public static void printSort(int[] arr) {
        for (int j : arr) {
            System.out.print(j + " ");
        }
        System.out.println();
    }

    public static void bubbleSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int n = arr.length;
        // 0~n-1 的位置
        for (int end = n - 1; end >= 0; end--) {
            for (int second = 1; second <= end; second++) {
                if (arr[second - 1] > arr[second]) {
                    swap(arr, second - 1, second);
                }
            }
        }
    }

    public static void insertSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        int n = arr.length;
        // 0~0
        // 0~1
        // 0~n-1 范围
        for (int end = 1; end < n; end++) {
            // 从1 开始
            int currNumIndex = end;
            while (currNumIndex - 1 >= 0 && arr[currNumIndex - 1] > arr[currNumIndex]) {
                swap(arr, currNumIndex - 1, currNumIndex);
                currNumIndex--;
            }
        }
    }
}
