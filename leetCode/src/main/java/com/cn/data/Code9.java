package com.cn.data;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @Description: Code9
 * @Author: 一方通行
 * @Date: 2022-05-16
 * @Version:v1.0
 */
public class Code9 {

    public static class Node<V> {
        public V value;
        public Node<V> next;

        public Node(V value) {
            this.value = value;
            next = null;
        }
    }

    public static void main(String[] args) {
        testQueue();
    }


    public static class Code9Queue<V> {
        private Node<V> head;
        private Node<V> tail;
        private int size;

        public Code9Queue() {
            head = null;
            tail = null;
            size = 0;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public int size() {
            return size;
        }

        public void offer(V value) {
            Node<V> cur = new Node<>(value);
            if (tail == null) {
                head = cur;
            } else {
                tail.next = cur;
            }
            tail = cur;
            size++;
        }

        public V poll() {
            V ans = null;
            if (head != null) {
                ans = head.value;
                head = head.next;
                size--;
            }
            if (head == null) {
                tail = null;
            }
            return ans;
        }

        public V peek() {
            V ans = null;
            if (head != null) {
                ans = head.value;
            }
            return ans;

        }
    }

    public static class Code9Stack<V> {
        private Node<V> head;
        private int size;

        public Code9Stack() {
            head = null;
            size = 0;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public int size() {
            return size;
        }

        public void push(V v) {
            Node<V> cur = new Node<>(v);
            if (head != null) {
                cur.next = head;
            }
            head = cur;
            size++;
        }

        public V pop() {
            V ans = null;
            if (head != null) {
                ans = head.value;
                head = head.next;
                size--;
            }
            return ans;
        }
    }

    public static void testQueue() {
        Code9Queue<Integer> Code9Queue = new Code9Queue<>();
        Queue<Integer> test = new LinkedList<>();
        int testTime = 5000000;
        int maxValue = 200000000;
        System.out.println("测试开始！");
        for (int i = 0; i < testTime; i++) {
            if (Code9Queue.isEmpty() != test.isEmpty()) {
                System.out.println("Oops!");
            }
            if (Code9Queue.size() != test.size()) {
                System.out.println("Oops!");
            }
            double decide = Math.random();
            if (decide < 0.33) {
                int num = (int) (Math.random() * maxValue);
                Code9Queue.offer(num);
                test.offer(num);
            } else if (decide < 0.66) {
                if (!Code9Queue.isEmpty()) {
                    int num1 = Code9Queue.poll();
                    int num2 = test.poll();
                    if (num1 != num2) {
                        System.out.println("Oops!");
                    }
                }
            } else {
                if (!Code9Queue.isEmpty()) {
                    int num1 = Code9Queue.peek();
                    int num2 = test.peek();
                    if (num1 != num2) {
                        System.out.println("Oops!");
                    }
                }
            }
        }
        if (Code9Queue.size() != test.size()) {
            System.out.println("Oops!");
        }
        while (!Code9Queue.isEmpty()) {
            int num1 = Code9Queue.poll();
            int num2 = test.poll();
            if (num1 != num2) {
                System.out.println("Oops!");
            }
        }
        System.out.println("测试结束！");
    }
}
