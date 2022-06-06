package cn.disruptor.learn;

import cn.disruptor.learn.event.OrderEventFactory;
import cn.disruptor.learn.event.producer.OrderProducerEvent;
import cn.disruptor.learn.handler.OrderHandler;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadFactory;

/**
 * @Description: Main
 * @Author: 一方通行
 * @Date: 2022-02-27
 * @Version:v1.0
 */
public class Main {
    public static void main(String[] args) {
        final OrderEventFactory orderEventFactory = new OrderEventFactory();
/*
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
*/
        /*  RingBuffer ringBuffer = new RingBuffer();*/
        Disruptor<cn.disruptor.learn.event.OrderEvent> disruptor = new Disruptor<>(orderEventFactory, 256,
                (ThreadFactory) Thread::new, ProducerType.SINGLE, new BlockingWaitStrategy());
        final EventHandlerGroup<cn.disruptor.learn.event.OrderEvent> eventHandlerGroup = disruptor.handleEventsWith(new OrderHandler());
        final RingBuffer<cn.disruptor.learn.event.OrderEvent> start = disruptor.start();
        final long cursor = start.getCursor();
        System.out.println(cursor);
        OrderProducerEvent producerEvent = new OrderProducerEvent(start);
        ByteBuffer b = ByteBuffer.allocate(8);
        for (long i = 0; i < 100; ++i) {
            b.putLong(0, i);
            producerEvent.sendData(b);
         }
    }
}
