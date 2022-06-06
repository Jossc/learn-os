package com.cn.data;

/**
 * @Description: Code6
 * @Author: 一方通行
 * @Date: 2022-05-12
 * @Version:v1.0
 */
public class Code6 {
    public static void main(String[] args) {

    }


    public static Node reverseLinkedList(Node head) {

        Node pre = null;
        Node next = null;
        while (head != null) {
            next = head.next;
            head.next = pre;
            pre = head;
            head = next;
        }
        return pre;
    }


    public static class Node {
        public int value;
        public Node next;

        public Node(int data) {
            value = data;
        }
    }

}
