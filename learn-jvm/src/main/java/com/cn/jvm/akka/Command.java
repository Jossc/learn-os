package com.cn.jvm.akka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * {@code @Description: Command}
 * {@code @Author: 一方通行 }
 * {@code @Date: 2025-03-19}
 * {@code @Version:v1.0}
 */
@Data
@NoArgsConstructor
public class Command implements Serializable {

    private static final long serialVersionUID = -4975904524892821359L;

    private String data;
    public Command(int i) {

    }

    public Command(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
