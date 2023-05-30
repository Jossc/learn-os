package com.cn.jvm.cleaner;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.ref.Cleaner;
import java.lang.ref.PhantomReference;

/**
 * author: 一方通行
 */
public class CleanerTest {
    public static void main(String[] args) throws Exception {
        int index = 0;
        while (true) {
            Thread.sleep(1000);
            System.gc();
            DemoObject obj = new DemoObject("demo01");
            Cleaner cleaner = Cleaner.create();
            cleaner.register(obj, new DoSomethingThread("thread_" + index++));
        }
    }
}
