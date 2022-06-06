package com.cn.data;

/**
 * @Description: Code7
 * @Author: 一方通行
 * @Date: 2022-05-13
 * @Version:v1.0
 */
public class Code7 {

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


    public static void main(String[] args) {

    }

    public static Node listNode(Node head) {
        Node pre = null;
        Node next;
        while (head != null) {
            next = head.next;
            head.next = pre;
            pre = head;
            head = next;
        }
        return pre;
    }

    public static DoubleNode listDoubleNode(DoubleNode head) {
        DoubleNode pre = null;
        DoubleNode next;
        while (null != head) {
            next = head.next;
            head.next = pre;
            head.last = next;
            pre = head;
            head = next;
        }
        return pre;
    }
}
