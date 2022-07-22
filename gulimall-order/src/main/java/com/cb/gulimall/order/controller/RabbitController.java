package com.cb.gulimall.order.controller;

import com.cb.gulimall.order.entity.OrderEntity;
import com.cb.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
public class RabbitController {

    @Resource
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMq")
    public String sendMq(@RequestParam(value = "num", defaultValue = "10") Integer num) {
        for (int i = 0; i < 10; i++) {

            if (i % 2 == 0) {
                OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
                entity.setCreateTime(new Date());
                entity.setId(1L);
                entity.setName("JVAV==>" + i);
                entity.setSort(1);
                entity.setStatus(1);
                rabbitTemplate.convertAndSend("cb-java-exchange", "cb.java", entity, new CorrelationData(UUID.randomUUID().toString()));
            } else {
                OrderEntity entity = new OrderEntity();
                entity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("cb-java-exchange", "cb22.java", entity, new CorrelationData(UUID.randomUUID().toString()));
            }


        }
        return "ok";
    }
}
