package com.cb.gulimall.seckill;

import com.sun.xml.internal.bind.v2.util.DataSourceSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * 1、使用sentinel来保护feign远程调用:熔断
 * 1）、调用方的熔断保护：feign.sentinel.enabled=true
 * 2）、调用方手动指定远程服务的降级策略。远程服务被降级处理。触发我们的熔断回调方法
 * 3）、超大流量的时候。必须牺牲一些远程服务。在服务的提供方（远程服务）指定降级策略。
 * 提供是在运行。但是不允许自己的业务逻辑，返回的是默认的东段数据（限流的数据）。
 * 2、自定义受保护的资源
 * 1）、代码  try(Entry entry = SphU.entry("seckillSkus")){
 * 2）、注解  @SentinelResource(value = "getCurrentSeckillSkus",blockHandler = "blockHandler")
 * <p>
 * 无论是1、2方式一定要配置被限流以后的默认返回
 * url可以设置同一返回
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GulimallSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallSeckillApplication.class, args);
    }

}
