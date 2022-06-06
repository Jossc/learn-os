package com.cn.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description: Code4
 * @Author: 一方通行
 * @Date: 2022-05-11
 * @Version:v1.0
 */
public class Code4 {
    public static void main(String[] args) {

    }

    public static void closeInputStream(InputStream inputStream) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            bufferedInputStream.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
