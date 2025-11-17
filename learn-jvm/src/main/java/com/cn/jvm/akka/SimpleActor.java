package com.cn.jvm.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

/**
 * {@code @Description: SimpleActor}
 * {@code @Author: 一方通行 }
 * {@code @Date: 2025-03-19}
 * {@code @Version:v1.0}
 */
public class SimpleActor extends UntypedActor {
    @Override
    public void onReceive(Object msg) throws Exception {
        System.out.println("onReceive " + msg.toString());
        if (msg instanceof Command) {
            final String data = ((Command) msg).getData();
            System.out.println("data " + msg.toString());
        } else if (msg.equals("echo")) {
            System.out.println("onReceive11 " + msg.toString());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        final ActorSystem actorSystem = ActorSystem.create("actor-system");

        Thread.sleep(5000);

        final ActorRef actorRef = actorSystem.actorOf(Props.create(SimpleActor.class), "simple-actor");

        actorRef.tell(new Command("CMD 1"), null);
        actorRef.tell(new Command("CMD 2"), null);
        actorRef.tell(new Command("CMD 3"), null);
        actorRef.tell(new Command("echo"), null);
        actorRef.tell(new Command("CMD 5"), null);

        Thread.sleep(5000);

    }
}
