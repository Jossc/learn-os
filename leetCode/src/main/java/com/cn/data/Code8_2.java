package com.cn.data;

/**
 * @Description: Code8_2
 * @Author: 一方通行
 * @Date: 2022-05-18
 * @Version:v1.0
 */
public class Code8_2 {
    public static class Node {
        public int value;
        public Node next;

        public Node(int data) {
            value = data;
        }
    }

    public static class DoubleNode {
        public int value;
        public DoubleNode last;
        public DoubleNode next;

        public DoubleNode(int data) {
            value = data;
        }
    }

    public Node listNode(Node head) {
        Node next = null;
        Node prev = null;
        while (head != null) {
            next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }

    public DoubleNode listDoubleNode(DoubleNode head) {
        DoubleNode prev = null;
        DoubleNode next = null;
        while (head != null) {
            next = head.next;
            head.next = prev;
            head.last = next;
            prev = head;
            head = next;
        }
        return prev;
    }

    public static int mostLeftNoMoreMun(int[] arr, int num) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        int ans = -1;
        int l = 0, r = arr.length - 1;
        while (l < r) {
            int mid = (r + l) / 2;
            if (arr[mid] > num) {
                ans = mid;
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }

        return ans;
    }

}