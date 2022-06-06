package com.cn.data;

import java.util.PriorityQueue;

/**
 * @Description: PriorityQueueTest
 * @Author: 一方通行
 * @Date: 2022-04-21
 * @Version:v1.0
 */
public class PriorityQueueTest {

    public static void main(String[] args) {
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>();
        queue.offer(10);
        queue.offer(123);
        queue.offer(1);
        queue.offer(16);
        queue.offer(14);
        System.out.println(queue);
        queue.poll();
        System.out.println(queue);
        queue.poll();
        System.out.println(queue);
        queue.poll();
        System.out.println(queue);
        queue.poll();
        System.out.println(queue);


    }
}
