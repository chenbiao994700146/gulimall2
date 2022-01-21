package com.cb.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * 使用RabbitMQ
 *  1、引入amqp场景;RabbitAutoConfiguration就会自动生效
 *  2、给容器中自动配置了
 *      RabbitTemplate、AmqpAdmin、CachingConnectionFactory、RabbitMessageTemplate;
 *      所有的属性都是spring.rabbitmq
 *  3、给配置文件中配置spring.rabbtimq信息
 *  4、@EnableRabbit: @EnableXxxx
 *  5、监听消息：RabbitListener;必须有@EnableRabbit
 *           @RabbitListener：类+方法上（监听哪些队列即可）
 *           @RabbitHandler:标注方法上(重载分区不同的消息)
 *
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableRedisHttpSession
@EnableRabbit
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallOrderApplication {
    public static void main(String[] args) {
            SpringApplication.run(GulimallOrderApplication.class, args);
        }
}
