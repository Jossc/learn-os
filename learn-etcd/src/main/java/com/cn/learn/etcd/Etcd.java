package com.cn.learn.etcd;


import com.google.common.collect.Lists;
import lombok.Data;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.requests.EtcdKeyGetRequest;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import mousio.etcd4j.responses.EtcdResponseDecoder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * @Description: Etcd
 * @Author: 一方通行
 * @Date: 2022-07-15
 * @Version:v1.0
 */
public class Etcd {

    public static void main(String[] args) {
   /*     final EtcdClient etcdClient = new EtcdClient(URI.create("http://127.0.0.1:2379"));
        final EtcdKeyGetRequest etcd = etcdClient.getDir("etcd").consistent();
        try {
            final EtcdKeysResponse etcdKeysResponse = etcd.send().get();
            System.out.println("version : + " + etcdClient.getVersion());
            System.out.println(etcdKeysResponse.getNode().getValue());

            etcdClient.put("cc", "hhh");
            final EtcdKeyGetRequest cc = etcdClient.get("cc");
            final EtcdResponseDecoder<EtcdKeysResponse> responseDecoder = cc.getResponseDecoder();
        } catch (Exception e) {
            System.out.println(e);
        }*/
     new Etcd(). hh();
    }

    private static final ThreadPoolExecutor BAIDU_API_EXECUTOR = new ThreadPoolExecutor(20, 20, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100000));




    public void hh()  {
        List<RR> productListToSync = Lists.newArrayList();
        try {
            for (int i =0;i<100;i++ ) {
                BAIDU_API_EXECUTOR.submit(() -> {
                    try {
                        productListToSync.add(create());
                    } catch (Exception e) {
                        System.out.println("e " + e.getMessage());
                    } finally {
                    }
                });

            }
        } catch (Exception e) {
            System.out.println("e " + e.getMessage());
        }
        System.out.println(productListToSync);

    }

    @Data
    public static class  RR {
        private String name;
    }

    public  RR create(){
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        RR r =new RR();
        r.setName("12323");
        return r;
    }
}
