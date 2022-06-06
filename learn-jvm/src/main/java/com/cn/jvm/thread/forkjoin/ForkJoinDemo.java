package com.cn.jvm.thread.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * @Description: ForkJoinDemo
 * @Author: 一方通行
 * @Date: 2022-04-16
 * @Version:v1.0
 */
public class ForkJoinDemo {
    private static Thread worker1 = null;
    private static Thread worker2 = null;
    private static AtomicBoolean worker1Park = new AtomicBoolean();
    private static AtomicBoolean worker2Park = new AtomicBoolean();

    static {
        worker1Park.set(false);
        worker2Park.set(false);
    }

    public abstract static class ForkJoinTaskDemo extends ForkJoinTask {
        @Override
        public Object getRawResult() {
            return null;
        }

        @Override
        protected void setRawResult(Object value) {

        }
    }

    public static void setParkFlag() {
        if (Thread.currentThread().getName().equals("worker-1")) {
            //自旋设置, 保证与main线程同步
            while (!worker1Park.compareAndSet(false, true)) {

            }
        } else if (Thread.currentThread().getName().equals("worker-2")) {
            //自旋设置, 保证与main线程同步
            while (!worker2Park.compareAndSet(false, true)) {

            }
        }
    }

    private static Runnable task1 = new Runnable() {
        @Override
        public void run() {
            worker1 = Thread.currentThread();
            worker1.setName("worker-1");
            System.out.println(Thread.currentThread().getName() + " execute task1");
            //暂停并且设置暂停flag以便main结束自旋
            setParkFlag();
            LockSupport.park();
            ForkJoinTask task11 = new ForkJoinTaskDemo() {
                @Override
                protected boolean exec() {
                    System.out.println(Thread.currentThread().getName() + " execute task1-1");
                    //暂停并且设置暂停flag以便main结束自旋
                    setParkFlag();
                    LockSupport.park();
                    return true;
                }
            };
            task11.fork();
            ForkJoinTask task12 = new ForkJoinTaskDemo() {
                @Override
                protected boolean exec() {
                    System.out.println(Thread.currentThread().getName() + " execute task1-2");
                    //暂停并且设置暂停flag以便main结束自旋
                    setParkFlag();
                    LockSupport.park();
                    return true;
                }
            };
            task12.fork();
            ForkJoinTask task13 = new ForkJoinTaskDemo() {
                @Override
                protected boolean exec() {
                    System.out.println(Thread.currentThread().getName() + " execute task1-3");
                    //暂停并且设置暂停flag以便main结束自旋
                    setParkFlag();
                    LockSupport.park();
                    return true;
                }
            };
            task13.fork();
            ForkJoinTask task14 = new ForkJoinTaskDemo() {
                @Override
                protected boolean exec() {
                    System.out.println(Thread.currentThread().getName() + " execute task1-4");
                    //暂停并且设置暂停flag以便main结束自旋
                    setParkFlag();
                    LockSupport.park();
                    return true;
                }
            };
            task14.fork();
            //提交4个子任务后暂停
            setParkFlag();
            LockSupport.park();
        }
    };

    private static Runnable task2 = new Runnable() {
        @Override
        public void run() {
            worker2 = Thread.currentThread();
            worker2.setName("worker-2");
            System.out.println(Thread.currentThread().getName() + " execute task2");
            //暂停并且设置暂停flag以便main结束自旋
            setParkFlag();
            LockSupport.park();
        }
    };

    public static void main(String[] args) {
        //只用两个线程，方便测试
        ForkJoinPool pool = new ForkJoinPool(2);
        //step-1、step-2 提交2各任务，期望worker-1 worker-2各执行一个任务
        System.out.println("step-1 step-2: 提交2个任务，触发2个worker线程");
        pool.submit(task1);
        pool.submit(task2);

        while (!worker1Park.get()) {
        }
        //step-3  唤醒worker-1 产生4个任务 然后worker-1继续暂停
        System.out.println("\n*******************************************");
        System.out.println("step-3 唤醒worker-1 产生4个子任务");
        LockSupport.unpark(worker1);
        //自旋设置, 保证与worker-1线程同步
        while (!worker1Park.compareAndSet(true, false)) {
        }


        while (!worker1Park.get() || !worker2Park.get()) {
        }
        //step-4 唤醒worker-1 worker-2
        System.out.println("\n*******************************************");
        System.out.println("step-4 唤醒worker-1、worker-2：worker-1弹出task1-4执行，worker-2窃取task1-1执行");
        LockSupport.unpark(worker1);
        LockSupport.unpark(worker2);
        //自旋设置, 保证与worker-1线程同步
        while (!worker1Park.compareAndSet(true, false)) {
        }
        //自旋设置, 保证与worker-2线程同步
        while (!worker2Park.compareAndSet(true, false)) {
        }

        while (!worker1Park.get() || !worker2Park.get()) {
        }
        //step-5 唤醒worker-1 worker-2
        System.out.println("\n*******************************************");
        System.out.println("step-5 唤醒worker-1、worker-2：worker-1弹出task1-3执行，worker-2窃取task1-2执行");
        LockSupport.unpark(worker1);
        LockSupport.unpark(worker2);
        //自旋设置, 保证与worker-1线程同步
        while (!worker1Park.compareAndSet(true, false)) {
        }
        //自旋设置, 保证与worker-2线程同步
        while (!worker2Park.compareAndSet(true, false)) {
        }


        while (!worker1Park.get() || !worker2Park.get()) {
        }
        //唤醒worker-1 worker-2 结束
        LockSupport.unpark(worker1);
        LockSupport.unpark(worker2);
        //自旋设置, 保证与worker-1线程同步
        while (!worker1Park.compareAndSet(true, false)) {
        }
        //自旋设置, 保证与worker-2线程同步
        while (!worker2Park.compareAndSet(true, false)) {
        }

    }
}
