package com.cn.jvm.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.IOException;

/**
 * {@code @Description: AkkATest}
 * {@code @Author: 一方通行 }
 * {@code @Date: 2025-03-19}
 * {@code @Version:v1.0}
 */
public class AkkATest {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("testAkkaTest");
        ActorRef firstRef = system.actorOf(PrintMyActorRefActor.props(), "first-test");
        System.out.println("First: " + firstRef);
        firstRef.tell("printit", ActorRef.noSender());
        System.out.println(">>> Press ENTER to exit <<<");
        try {
            System.in.read();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            system.terminate();
        }
    }
}
