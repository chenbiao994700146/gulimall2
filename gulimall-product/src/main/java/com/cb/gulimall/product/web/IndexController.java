package com.cb.gulimall.product.web;

import com.cb.gulimall.product.entity.CategoryEntity;
import com.cb.gulimall.product.service.CategoryService;
import com.cb.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Resource
    CategoryService categoryService;

    @Resource
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;
    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        //TODO 1.查出所有的1级分类
       List<CategoryEntity> categoryEntitys= categoryService.getLevel1Categorys();

       model.addAttribute("categorys",categoryEntitys);
        return "index";
    }



    //"index/catalog.json"
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson(){
        Map<String, List<Catelog2Vo>> map= categoryService.getCatalogJson();
        return  map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public  String hello(){
        //1.获取锁
        RLock lock = redissonClient.getLock("my-lock");

        //2.加锁
        lock.lock();//阻塞式等待
        //1)、锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s
        //2)、加锁的业务只要运行完成，就不会个当前锁续期，即使不手动解锁，锁默认在30s以后自动删除
        lock.lock(10, TimeUnit.SECONDS); //10秒钟自动解锁,自动解锁时间一定要大于业务的执行时间
        // lock.lock(10, TimeUnit.SECONDS);在锁时间到了以后，不会自动续期
        //1.如果我们传递了锁的超时时间，就执行脚本，进行占锁，
        //2.如果未制度锁的时间，就使用getLockWatchdogTimeout()  30*1000秒时间
        //  只要占锁成功，就会启动一个定时任务，【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】
        //  internalLockLeaseTime[看门狗时间] / 3 ，10s续期


        //最佳实战
        // lock.lock(10, TimeUnit.SECONDS);
        try{
            System.out.println("加锁成功，执行业务"+Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //3.解锁
            System.out.println("释放锁"+Thread.currentThread().getId());
            lock.unlock();
        }

        return "hello";
    }

    //保证一定能读到最新数据，修改期间，写锁是一个排他锁，读锁是一个共享锁
    //写锁没释放读就必须等待

    //读+读：相当于无锁，并发读，在redis中记录好，所有当前的读锁，他们都会同事加锁成功
    //写+读  ：等待写锁释放
    //写+写：阻塞
    //读+写： 有写，也需要等待
    //只要有写的存在，都必须等待
    @ResponseBody
    @GetMapping("/write")
    public String writeValue(){
        String s =null;

        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = lock.writeLock();
        try {
            //1.改数据加写锁

            rLock.lock();
            System.out.println("写锁加锁成功。。。。"+Thread.currentThread().getId());
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("cb",s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
            System.out.println("写锁释放成功。。。。"+Thread.currentThread().getId());
        }
        return s;
    }


    @ResponseBody
    @GetMapping("/read")
    public String readValue(){

        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = lock.readLock();
        String s ="";
        rLock.lock();;
        try{
            System.out.println("读锁加锁成功。。。。"+Thread.currentThread().getId());
            try {
                 s = redisTemplate.opsForValue().get("cb");
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }finally {
            rLock.unlock();
            System.out.println("读锁释放成功。。。。"+Thread.currentThread().getId());
        }
        return s;

    }

/**
 * 车库停车
 * 车位
 */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        //park.acquire();//获取一个信号，获取一个值,占一个车位
        boolean b = park.tryAcquire();
        return "ok---->"+b;
    }


    @GetMapping("/go")
    @ResponseBody
    public String go() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();//释放一个车位

        return "ok";
    }


    /**
     * 放假锁门
     * 1，2
     * 5个半全走完，才锁大门
     */
    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();
        return "放假了。。。。";
    }

    @ResponseBody
    @GetMapping("/gogo/{id}")
    public String gogo(@PathVariable("id")Long id){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();//计数减一
        return id+"放假了。。。。";
    }


}
