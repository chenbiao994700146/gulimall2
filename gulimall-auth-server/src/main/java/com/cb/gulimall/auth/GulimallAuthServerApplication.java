package com.cb.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * SpringSession 原理
 * 1）、@EnableRedisHttpSession导入RedisHttpSessionConfiguration
 * 1、给容器中添加了一个组件
 * SessionRepository===> RedisOperationsSessionRepository：redis操作session.
 * 2、SessionRepositoryFilter===》filter：sesion过滤器  ,每个请求过来都必须经过filter
 * 1、创建的时候，就自动从容器中获取到了SessionRepository;
 * 2、原始的request,SessionRepositoryRequestWrapper，SessionRepositoryResponseWrapper
 * 3、以后获取session.request.getSession();
 * 4、wrappedRequest.getSession==>SessionRepository 中获取到的。
 * <p>
 * 装饰者模式：
 * <p>
 * 自动过期：redis里的时间也是有过期时间的
 */

@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }

}
