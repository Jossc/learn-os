package cn.disruptor.learn.event.producer;

import cn.disruptor.learn.event.OrderEvent;
import com.lmax.disruptor.RingBuffer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.ByteBuffer;

/**
 * @Description: OrderProducerEvent
 * @Author: 一方通行
 * @Date: 2022-02-27
 * @Version:v1.0
 */
@Data
@AllArgsConstructor
public class OrderProducerEvent {

    private RingBuffer<OrderEvent> ringBuffer;


    public void sendData(ByteBuffer value) {
        System.out.println("OrderProducerEvent  + sendData ");
        final long next = ringBuffer.next();
        final OrderEvent orderEvent = ringBuffer.get(next);
        orderEvent.setValue(value.getLong(0));
        ringBuffer.publish(next);
    }
}
