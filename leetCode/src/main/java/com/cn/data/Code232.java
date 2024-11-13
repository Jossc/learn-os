package com.cn.data;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @Description: Code232
 * @Author: 一方通行
 * @Date: 2024-03-04
 * @Version:v1.0
 */
public class Code232 {

    class MyQueue {

        Deque<Integer> inStack;
        Deque<Integer> outStack;

        public MyQueue() {
            inStack = new ArrayDeque<>();
            outStack = new ArrayDeque<>();

        }

        public void push(int x) {
            inStack.push(x);
        }

        public int pop() {
            if (outStack.isEmpty()) {
                in2out();
            }
            return outStack.pop();
        }

        public int peek() {
            if (outStack.isEmpty()) {
                in2out();
            }
            return outStack.peek();
        }

        private void in2out() {
            while (!inStack.isEmpty()) {
                outStack.push(inStack.pop());
            }
        }

        public boolean empty() {
            return outStack.isEmpty() && inStack.isEmpty();
        }
    }

}
