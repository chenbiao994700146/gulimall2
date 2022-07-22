package com.cb.gulimall.order.listener;

import com.cb.gulimall.order.entity.OrderEntity;
import com.cb.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {

    @Resource
    OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
        System.out.println("收到过去的订单信息：准备关闭订单" + orderEntity.getOrderSn());

        try {
            orderService.closeOrder(orderEntity);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }

    }

}
