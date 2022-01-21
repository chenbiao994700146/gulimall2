package com.cb.gulimall.order.service.impl;

import com.cb.gulimall.order.entity.OrderEntity;
import com.cb.gulimall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cb.common.utils.PageUtils;
import com.cb.common.utils.Query;

import com.cb.gulimall.order.dao.OrderItemDao;
import com.cb.gulimall.order.entity.OrderItemEntity;
import com.cb.gulimall.order.service.OrderItemService;

@RabbitListener(queues = {"cb-java-Queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 参数可以写一下类型
     * 1、Message message ：原生消息详细信息 头+体
     * 2、T<发送的消息的类型>
     * 3、Channel 当前传输数据的通道
     *
     *
     * Queue:可以很多人来监听。只要收到消息，队列删除消息，而且只能有一个收到消息
     * 场景：
     *  1）、订单服务启动多个;同一个消息，只能有一个客户端收到
     *  2）、只有一个消息完成处理完，方法运行结束，我们就可以接受到下一个消息
     *
     * @param message
     */
   // @RabbitListener(queues = {"cb-java-Queue"})
    @RabbitHandler
    public void recieveMessage(Message message, OrderReturnReasonEntity content, Channel channel) throws InterruptedException {
        System.out.println("接受到消息。。。。"+content);
        byte[] body = message.getBody();
        MessageProperties properties = message.getMessageProperties();
        //Thread.sleep(3000);
        System.out.println("消息处理完成=》"+content.getName());
        //channel 内按顺序自增的
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("deliveryTag==>["+deliveryTag+"]");
        //签收货物，非批量模式
        try {
            if(deliveryTag%2==0){
                //收货
                channel.basicAck(deliveryTag,false);
                System.out.println("签收了。。"+deliveryTag);
            }else{
                //退货 requeue=false 丢弃，requeue=true 发回服务器，服务器重新入队。
                channel.basicNack(deliveryTag,false,false);
                System.out.println("没有签收。。");
            }

        } catch (Exception e) {
            //网络终端

        }
    }

    @RabbitHandler
    public void recieveMessage2(OrderEntity content) throws InterruptedException {

        System.out.println("消息处理完成=》"+content);
    }

}