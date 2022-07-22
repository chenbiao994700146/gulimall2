package com.cb.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * 1.整合Mybatis-Plus
 * 1) 导入依赖
 * 2） 配置
 * 1.配置数据源
 * 1>导入数据库驱动
 * 2>在application.yml配置数据源信息
 * 2.配置Mybatis-Plus
 * 1>使用@MapperScan
 * 2>告诉Mybatis-Plus ,sql的映射文件
 * <p>
 * <p>
 * 2.逻辑删除
 * 1）.配置全局的逻辑删除规则（高版本省略）
 * 2）.配置逻辑删除的组件Bean（高版本省略）
 * 3).给Bean加上逻辑删除注解@TableLogic
 * 4).分组校验
 * 1>.@NotBlank(message = "品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})
 * 给校验注解标注什么情况需要进行校验
 * 2>.@Validated({AddGroup.class}
 * 3>.默认没有指定分组的校验组件@Not
 * 5).自定义校验
 * 1>.编写一个自定义的校验注解
 * 2>.编写一个自定义的校验器
 * 3>.关联自定义的校验器和自定义的校验注解
 * <p>
 * 3.JSR303
 * 1).给Bean添加校验注解:javax.validation.constraints 并定义自己的messages提示
 * 2).开启校验功能 @Valid
 * 效果：
 * 3).给校验的bean后紧跟一个BindingResult 就可以获取到校验的结果
 * 4).统一的异常处理
 * <p>
 * 5).模板引擎
 * 5.1）、引入devtools
 *
 * @ControllerAdvice 6、整合redis
 * 1)、引入依赖
 * 2）、配置host地址，prot端口号
 * 3）、使用springboot自动配置好的StringRedisTemplate来操作redis
 * <p>
 * <p>
 * 7、整合redisson作为分布式锁等功能框架
 * 1）引入依赖
 * 2）配置
 * <p>
 * <p>
 * 8、整合SpringCache简化缓存开发
 * 1）、引入依赖spring-boot-starter-cache
 * 2)、写配置
 * 1、自动配置
 * CacheAuroConfiguration 会导入RedisCacheConfiguration
 * 2、spring.cache.type=redis
 * 3、注解
 * @Cacheable: Triggers cache population. 触发将数据报错到缓存的操作
 * @CacheEvict: Triggers cache eviction. 触发将数据从缓存删除的操作
 * @CachePut: Updates the cache without interfering with the method execution.bu'yiing'x 不影响方法执行的更新缓存
 * @Caching: Regroups multiple cache operations to be applied on a method. 组合以上多个操作
 * @CacheConfig: Shares some common cache-related settings at class-level. 在类级别共享缓存的相同配置
 * 1）开启缓存功能 @EnableCachin
 * 2）只需要使用注解就能完成缓存操作
 * 3)、默认行为
 * 1）缓存中有，方法不调用
 * 2）key默认生成：缓存的名字：：自动生成的key     category::SimpleKey []
 * 3)缓存的value的值，默认使用json序列化后的数据，存档redis
 * 4)默认时间：-1 ，永不过期
 * 自定义：
 * 1）指定生成的缓存使用的key   key属性指定，接受一个SpEl
 * 2)指定缓存的数据的存储时间   spring.cache.redis.time-to-live=60000  修改TTL(单位是秒)
 * 3）将数据保存为json数据
 * 1.CacheAutoConfiguration->RedisCacheConfiguration->自动配置了RedisCacheManager->
 * 初始话所有的缓存->没个缓存决定了使用什么配置->如果redisCacheConfiguration有就用已有，没有就用默认配置
 * ->想改缓存的配置，只需要给容器中放一个RedisCacheConfiguration即可
 * ->就会应用到当前RedisCacheManager管理的所有缓存分区中
 * if (this.redisCacheConfiguration != null) {
 * return this.redisCacheConfiguration;
 * }
 * 4）spring-Cache的不足：
 * 1）、读模式：
 * 缓存穿透：查询一个null 数据，解决：缓存空数据 cache-null-values=true
 * 缓存击穿：大量并发进来同时查询一个正好过期的数据，解决：加锁：？默认是不加锁:sync= true (加锁，解决击穿)
 * 缓存雪崩：大量的key同时过期。解决：加随机时间。加上过期时间 redis.time-to-live=600000
 * 2）、写模式：（缓存与数据一致）
 * 1）、读写加锁。
 * 2）、引入Canal,感知Mysql的更新去更新数据库
 * 3）、读多写多，直接去数据库查询就行
 * <p>
 * 总结：常规数据（读多写少，即使性，一致性要求不高的数据）：完成可是使用Spring-Cache：写模式（只要缓存的数据有过期时间就足够了）
 * <p>
 * 特殊数据：特殊设计
 * <p>
 * 原理：
 * CacheManager->Cache->负责缓存的读写
 * <p>
 * 9.使用Sentinel来保护feign远程调用：熔断：
 */

//@MapperScan("com.cb.gulimall.product.dao")
@EnableRedisHttpSession
@EnableCaching
@EnableFeignClients(basePackages = "com.cb.gulimall.product.feign")
@MapperScan("com.cb.gulimall.product.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }
}
