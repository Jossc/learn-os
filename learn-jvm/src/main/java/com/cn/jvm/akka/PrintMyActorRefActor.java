package com.cn.jvm.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.AbstractActor.Receive;

/**
 * {@code @Description: PrintMyActorRefActor}
 * {@code @Author: 一方通行 }
 * {@code @Date: 2025-03-19}
 * {@code @Version:v1.0}
 */
public class PrintMyActorRefActor extends AbstractActor {
    static Props props() {
        return Props.create(PrintMyActorRefActor.class, PrintMyActorRefActor::new);
    }

  /*  @Override
    public void preStart() {
        System.out.println("first started");
        getContext().actorOf(StartStopActor2.props(), "second");

    }*/
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("printit", p -> {
                    ActorRef secondRef = getContext().actorOf(Props.empty(), "second-actor");
                    System.out.println("Second: " + secondRef);
                })
                .build();
    }

  /*  @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("printit", p -> {
                    ActorRef secondRef = getContext().actorOf(Props.empty(), "second-actor");
                    System.out.println("Second: " + secondRef);
                })
                .build();
    }*/
}
