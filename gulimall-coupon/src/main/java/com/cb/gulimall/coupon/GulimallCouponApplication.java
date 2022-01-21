package com.cb.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/*
    1.nacos作为配置中心统一管理
        1.1 引入依赖
        1.2.创建一个bootstrap.properties
            配置spring.application.name、spring.cloud.nacos.config.server-addr
        1.3.需要给配置中心默认添加一个数据集 gulimall-coupon.properties 《当前应用名+properties》
        1.4 给.properties添加配置
        1.5 动态获取配置 添加注解@RefreshScope  动态刷新获取配置
                                @Value

 */
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallCouponApplication {
    public static void main(String[] args) {
            SpringApplication.run(GulimallCouponApplication.class, args);
        }
}
