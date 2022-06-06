package cn.disruptor.learn.event;

import com.lmax.disruptor.EventFactory;

/**
 * @Description: EventFactory
 * @Author: 一方通行
 * @Date: 2022-02-27
 * @Version:v1.0
 */
public class OrderEventFactory implements EventFactory<OrderEvent> {
    @Override
    public OrderEvent newInstance() {
        return new OrderEvent();
    }
}
