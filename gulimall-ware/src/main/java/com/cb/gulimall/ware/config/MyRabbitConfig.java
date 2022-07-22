package com.cb.gulimall.ware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitConfig {

//    @Resource
//    RabbitTemplate rabbitTemplate;


//    @RabbitListener(queues = "stock.release.stock.queue")
//    public void listener(Message message){
//
//    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Exchange stockEventExchange() {
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("stock-event-exchange", true, false);
    }

    @Bean
    public Queue stockReleaseStockQueue() {
        return new Queue("stock.release.stock.queue", true, false, false);
    }

    @Bean
    public Queue stockDelayQueue() {

        Map<String, Object> arguments = new HashMap<>();
        /**
         * x-dead-letter-exchange: order-event-exchange
         * x-dead-letter-routing-key: order.release.order
         * x-message-ttl: 60000
         */
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        arguments.put("x-dead-letter-routing-key", "stock.release");
        arguments.put("x-message-ttl", 120000);
        return new Queue("stock.delay.queue", true, false, false, arguments);
    }

    @Bean
    public Binding stockReleaseBinding() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			Map<String, Object> arguments
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release.#", null);
    }

    @Bean
    public Binding stockLockedBinding() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        //			Map<String, Object> arguments
        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.locked", null);
    }

//    /**
//     * 定制RabbitTemplate
//     * 1、服务器收到消息就回调
//     *        1、spring.rabbitmq.publisher-confirms=true
//     *        2、设置确认回调
//     * 2、消息正确抵达队列进行回调
//     *       1、 spring.rabbitmq.publisher-returns=true
//     *           spring.rabbitmq.template.mandatory=true、
//     *       2、设置确认回调ReturnCallback
//     * 3、消费端确认（保证每个消息被正确消费，此时才可以broker删除这个消息）
//     *      1、默认是自动确认的，只要消息接受到，客户端会自动确认，服务器端就会移除这个消息
//     *          问题：
//     *              我们收到很多消息，自动回复给服务器ack,只有一个消息处理成功，宕机了。发送消息丢失；
//     *              消费者手动确认模式，，只要我们没有明确告诉MQ,货物被签收，没有ACK
//     *              手动确认。只要我们没有明确告诉MQ,货物被签收。没有ack,消息就一直是unacked状态。即使Consumer宕机。消息不会丢失，会重新变为Ready,下一次有新的Consumer连接进来就发给它
//     *      2、如何签收货物
//     *              channel.basicAck(deliveryTag,false); 签收 业务成功
//     *               channel.basicNack(deliveryTag,false,false);  拒签 业务失败
//     */
//    @PostConstruct //MyRabbitConfig 对象创建完成以后，执行这个方法
//    public void initRabbitTemplate(){
//        //设置确认回调
//        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//            /**
//             *   1、只要消息抵达Broker就ack=true
//             * @param correlationData  当前消息的唯一关联数据（这个是消息的唯一id）
//             * @param ack 消息是否成功收到
//             * @param cause 失败的原因
//             */
//            @Override
//            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//                System.out.println("confirm...correlationData["+correlationData+"]==>ack["+ack+"]==>cause["+cause+"]");
//            }
//        });
//
//        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
//            /**
//             * 只要消息没有投递给指定的队列，就触发这个失败的回调
//             * @param message  投递失败的消息详细信息
//             * @param replyCode 回复的状态码
//             * @param replyText 回复的文本内容
//             * @param exchange  当时这个消息发给那个交换机
//             * @param routingKey 当时这个消息用那个路由键
//             */
//            @Override
//            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
//                System.out.println("Fail Message["+message+"]==>replyCode["+replyCode+"]====>replyText["+replyText+"]===>exchange["+exchange+"]==>routingKey["+routingKey+"]");
//            }
//        });
//    }


}
