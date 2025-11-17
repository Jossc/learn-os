package com.cn.jvm.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

/**
 * 商品分组
 * {@code @Description: GroupProductRefActor}
 * {@code @Author: 一方通行 }
 * {@code @Date: 2025-03-20}
 * {@code @Version:v1.0}
 */
public class GroupProductRefActor extends AbstractActor {
    private final ActorRef actProductQuery;

    public GroupProductRefActor(ActorRef actProductQuery) {
        this.actProductQuery = actProductQuery;
    }

    @Override
    public Receive createReceive() {
        return null;
    }
}
