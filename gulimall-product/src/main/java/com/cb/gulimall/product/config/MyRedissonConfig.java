package com.cb.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class MyRedissonConfig {

    @Bean(destroyMethod = "shutdown")
    RedissonClient redisson() throws IOException {
        //1.创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://10.211.55.7:6379");

        //2.根据Config创建出的RedissonClient
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
