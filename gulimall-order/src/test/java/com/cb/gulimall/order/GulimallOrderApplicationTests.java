package com.cb.gulimall.order;

//import org.junit.jupiter.api.Test;
import com.alibaba.fastjson.JSON;
import com.cb.gulimall.order.entity.OrderEntity;
import com.cb.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {


    @Resource
    AmqpAdmin amqpAdmin;

    @Resource
    RabbitTemplate rabbitTemplate;
    /**
     * 1、如果创建Exchange、Queue、Binding
     *      1)、使用AmqpAdmiin进行创建
     * 2、如何收发消息
     *
     */

    @Test
   public void creatExchange() {
        DirectExchange exchange = new DirectExchange("cb-java-exchange",true,false);
        amqpAdmin.declareExchange(exchange);
       log.info("exchang[{}]创建成功","java");
    }

    @Test
    public void creatQueue(){
        Queue queue = new Queue("cb-java-Queue",true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功","Queue");
    }
    @Test
    public void creatBinding(){
        Binding binding = new Binding("cb-java-Queue", Binding.DestinationType.QUEUE,"cb-java-exchange","cb.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("binding[{}]创建成功","binding");
    }

    @Test
    public void pushMessage(){


        String msg="JAVA";
       // String s = JSON.toJSONString(entity);
        for(int i=0;i<10;i++){

            if(i%2==0){
                OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
                entity.setCreateTime(new Date());
                entity.setId(1L);
                entity.setName("JVAV==>"+i);
                entity.setSort(1);
                entity.setStatus(1);
                rabbitTemplate.convertAndSend("cb-java-exchange","cb.java",entity);
            }else{
                OrderEntity entity = new OrderEntity();
                entity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("cb-java-exchange","cb.java",entity);
            }

            log.info("消息完成完成{}");
        }

    }
}
