package cn.disruptor.learn.handler;

import cn.disruptor.learn.event.OrderEvent;
import com.lmax.disruptor.EventHandler;

/**
 * @Description: OrderHandler
 * @Author: 一方通行
 * @Date: 2022-02-27
 * @Version:v1.0
 */
public class OrderHandler implements EventHandler<OrderEvent> {
    @Override
    public void onEvent(OrderEvent orderEvent, long l, boolean b) throws Exception {
        System.out.println(orderEvent.getValue());
    }
}
