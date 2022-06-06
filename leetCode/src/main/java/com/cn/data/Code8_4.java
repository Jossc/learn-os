package com.cn.data;

/**
 * @Description: Code8_4
 * @Author: 一方通行
 * @Date: 2022-06-06
 * @Version:v1.0
 */
public class Code8_4 {
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

    public static Node listNode(Node head) {
        Node prev = null;
        Node next = null;
        while (head.next != null) {
            next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }

    public static DoubleNode listNode1(DoubleNode head) {
        DoubleNode prev = null;
        DoubleNode next = null;
        while (head.next != null) {
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
        int l = 0;
        int r = arr.length - 1;
        // 中间变量
        int ans = -1;
        return 0;
    }


}