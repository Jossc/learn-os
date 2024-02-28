package com.cn.jvm.cleaner;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * author: 一方通行
 */
@Data
public class DemoObject {
    private String name;

    public DemoObject(String name) {
        this.name = name;
    }
}
