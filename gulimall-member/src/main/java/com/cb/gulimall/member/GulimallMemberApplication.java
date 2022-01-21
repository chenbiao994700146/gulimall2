package com.cb.gulimall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/*
    1.引入open-fegin
    2.编写统一的接口，专门用来做远程调用服务
        2.1 声明接口的每一个方法是调用那个远程服务那个请求
    3.开启远程调用功能
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.cb.gulimall.member.fegin")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallMemberApplication {
    public static void main(String[] args) {
            SpringApplication.run(GulimallMemberApplication.class, args);
        }
}
