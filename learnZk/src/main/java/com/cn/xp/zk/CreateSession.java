package com.cn.xp.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * @Description: CreateSession
 * @Author: 一方通行
 * @Date: 2021-07-25
 * @Version:v1.0
 */
public class CreateSession {


    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        //链接地址,会话超时时间, wather 监听器
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 40000, event -> {
            // 这里监听客户端事件建立成功
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                System.out.println("path :" + event.getPath());
                System.out.println("type:" + event.getType());
                latch.countDown();
            }
            if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                System.out.println("子节点有发生变化");
            }

        });
        latch.await();
       /* createNodeSync(zooKeeper, "/hahahah0000000009/hhahaha", "这是个持久顺序节点");
        System.out.println(zooKeeper.getState().isConnected());

        byte[] data = zooKeeper.getData("/hahahah0000000009", false, null);
        System.out.println(new String(data));
        getChildren(zooKeeper);*/
        // -1 是最新版本修改
        byte[] data = zooKeeper.getData("/hahahah0000000009", event -> {
            if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
                System.out.println("数据节点发生变化 :" + event.getPath());
            }

        }, null);
        System.out.println(new String(data));
        Stat stat = zooKeeper.setData("/hahahah0000000009", "陈卓测试哈哈哈哈".getBytes(), -1);
        System.out.println(stat.getAversion());

        Stat exists = zooKeeper.exists("/hahahah0000000009", false);
        if (Objects.nonNull(exists)) {
            zooKeeper.delete("/hahahah0000000009", -1);
        }

    }

    private static void getChildren(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren("/hahahah0000000009", false, null);
        children.forEach(System.out::println);
    }

    /**
     * z同步方式创建节点
     *
     * @param path
     * @param type
     */
    public static void createNodeSync(ZooKeeper zooKeeper, String path, String type) throws InterruptedException, KeeperException {
        String node = zooKeeper.create(path, type.getBytes(), OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(node);
    }

    /**
     * 异步创建节点
     *
     * @param path
     * @param type
     */
    public void createNode(String path, String type) {

    }

}
