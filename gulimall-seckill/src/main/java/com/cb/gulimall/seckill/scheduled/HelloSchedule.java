package com.cb.gulimall.seckill.scheduled;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j

@Component
//@EnableAsync
//@EnableScheduling
public class HelloSchedule {

    /**
     * 1、spring 中6位组成，不允许第7位的年
     * 2、在周几的位置，1-7代表周一到周日；MON-SUN
     * 3、定时任务不应该阻塞
     * 1）、可以让业务运行以异步的方式，自己提交到线程池
     * 2）、支持定时人线程池：设置#spring.task.scheduling.pool.size=5
     * 3）、让定时任务异步执行
     * 异步任务；
     * 1、@EnableAsync 开启异步任务功能
     * 2、@Async   给异步执行的方法上标注
     * 配置：
     * spring.task.execution.pool.core-size=5
     * spring.task.execution.pool.max-size=50
     * 解决：使用异步任务+定时任务来完成定时任务不阻塞的功能；
     */
    @Async
    @Scheduled(cron = "*/5 * * ? * 6")
    public void hello() throws InterruptedException {
        log.info("hello...");
        Thread.sleep(3000);
    }
}
