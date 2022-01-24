package com.cb.gulimall.auth;

//import org.junit.jupiter.api.Test;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallAuthServerApplicationTests {

    @Autowired
    StringRedisTemplate redisTemplate;



    @Test
   public void contextLoads() {
       // redisTemplates.opsForValue().set("k2","v1");
        //redisTemplate.opsForValue().set("k2","v2");
        //redisTemplate.opsForValue().set("k3","v3");
        //redisTemplate.opsForValue().set("k4","v4");

    }


    @Test
    public void  test01(){
        System.out.println(redisTemplate.keys("*"));
        //redisTemplate.opsForValue().set("k2","v2");
       // redisTemplate.opsForValue().set("k3","v3");
       // redisTemplate.opsForValue().set("k4","v4");
    }

    @Test
    public void  test02(){
        redisTemplate.opsForValue().set("k2","v2");
        redisTemplate.opsForValue().set("k2","v2");
        redisTemplate.opsForValue().set("k3","v3");
        redisTemplate.opsForValue().set("k4","v4");
    }

    @Test
    public void  test03(){
//      redisTemplate.setEnableTransactionSupport(true);
//      redisTemplate.multi();
//      redisTemplate.opsForValue().set("k5","v5");
//      redisTemplate.opsForValue().set("k6","v6");
//      redisTemplate.exec();
       // System.out.println(redisTemplate.keys("*"));

        redisTemplate.setEnableTransactionSupport(true);
        try {
            redisTemplate.multi();//开启事务
            redisTemplate.opsForValue().set("k5","v55");
            redisTemplate.opsForValue().set("k6","v66");
            //提交
            redisTemplate.exec();
        }catch (Exception e){
         //   log.error(e.getMessage(), e);
            //开启回滚
            redisTemplate.discard();
        }

    }

    class Brank{
        public boolean method() throws InterruptedException {
            int balance;//可用余额
            int debt;//欠额
            int amtTosubtract=10;//实刷额度

            redisTemplate.watch("balance");
            //jedis.set("balance","5");//此句不该出现，讲课方便。模拟其他程序已经修改了该条目

            Thread.sleep(7000);
            balance=Integer.parseInt(redisTemplate.opsForValue().get("balance"));
            if(balance<amtTosubtract){
                redisTemplate.unwatch();
                System.out.println("modify");
                return false;
            }else{
                System. out .println( "***********transaction" );
                redisTemplate.setEnableTransactionSupport(true);
                redisTemplate.multi();
                redisTemplate.opsForValue().decrement("balance",amtTosubtract);
                redisTemplate.opsForValue().increment("debt",amtTosubtract);
                redisTemplate.exec();
                balance=Integer.parseInt(redisTemplate.opsForValue().get("balance"));
                debt=Integer.parseInt(redisTemplate.opsForValue().get("debt"));

                System. out .println( "*******"+ balance);
                System. out .println( "*******"+ debt);
                return true ;


            }

        }
    }

    /**
        * 通俗点讲，watch命令就是标记一个键，如果标记了一个键， 在提交事务前如果该键被别人修改过，那事务就会失败，这种情况通常可以在程序中
        * 重新再尝试一次。
        * 首先标记了键balance，然后检查余额是否足够，不足就取消标记，并不做扣减； 足够的话，就启动事务进行更新操作，
        * 如果在此期间键balance被其它人修改， 那在提交事务（执行 exec ）时就会报错， 程序中通常可以捕获这类错误再重新执行一次，直到成功。
        */
    @Test
    public void  test04() throws InterruptedException {
     Brank  brank= new Brank();
        boolean method = brank.method();
        System.out.println("main retValue-------: "+ method);
    }


    @Test
    public void  test05() {
//     Jedis jedis_M =  new  Jedis( "127.0.0.1" ,6379);
//     Jedis jedis_S =  new  Jedis( "127.0.0.1" ,6380);
//     
//     jedis_S.slaveof( "127.0.0.1" ,6379);
//     
//     jedis_M.set( "k6" , "v6" );
//     Thread. sleep (500);
//     System. out .println(jedis_S.get( "k6" ));
    }


    /**
     * maxActive ：控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted。
     * maxIdle ：控制一个pool最多有多少个状态为idle(空闲)的jedis实例；
     * whenExhaustedAction：表示当pool中的jedis实例都被allocated完时，pool要采取的操作；默认有三种。
     *  WHEN_EXHAUSTED_FAIL --> 表示无jedis实例时，直接抛出NoSuchElementException；
     *  WHEN_EXHAUSTED_BLOCK --> 则表示阻塞住，或者达到maxWait时抛出JedisConnectionException；
     *  WHEN_EXHAUSTED_GROW --> 则表示新建一个jedis实例，也就说设置的maxActive无用；
     * maxWait ：表示当borrow一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛JedisConnectionException；
     * testOnBorrow ：获得一个jedis实例的时候是否检查连接可用性（ping()）；如果为true，则得到的jedis实例均是可用的；
     *
     * testOnReturn：return 一个jedis实例给pool时，是否检查连接可用性（ping()）；
     *
     * testWhileIdle：如果为true，表示有一个idle object evitor线程对idle object进行扫描，如果validate失败，此object会被从pool中drop掉；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义；
     *
     * timeBetweenEvictionRunsMillis：表示idle object evitor两次扫描之间要sleep的毫秒数；
     *
     * numTestsPerEvictionRun：表示idle object evitor每次扫描的最多的对象数；
     *
     * minEvictableIdleTimeMillis：表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义；
     *
     * softMinEvictableIdleTimeMillis：在minEvictableIdleTimeMillis基础上，加入了至少minIdle个对象已经在pool里面了。如果为-1，evicted不会根据idle time驱逐任何对象。如果minEvictableIdleTimeMillis>0，则此项设置无意义，且只有在timeBetweenEvictionRunsMillis大于0时才有意义；
     *
     * lifo：borrowObject返回对象时，是采用DEFAULT_LIFO（last in first out，即类似cache的最频繁使用队列），如果为False，则表示FIFO队列；
     *
     * ==================================================================================================================
     * 其中JedisPoolConfig对一些参数的默认设置如下：
     * testWhileIdle=true
     * minEvictableIdleTimeMills=60000
     * timeBetweenEvictionRunsMillis=30000
     * numTestsPerEvictionRun=-1
     */
    public static class JedisPoolUtil{
        private static volatile JedisPool jedisPool=null;
        private JedisPoolUtil(){ }

        public static JedisPool getJedisPoolnstance(){
            if(null==jedisPool){
                synchronized (JedisPoolUtil.class){
                    if (null==jedisPool){
                        GenericObjectPoolConfig poolConfig =new GenericObjectPoolConfig();
                        poolConfig.setMaxTotal(1000);
                        poolConfig.setMaxIdle(32);
                        poolConfig.setMaxWaitMillis(100*1000);
                        poolConfig.setTestOnBorrow(true);
                        jedisPool=new JedisPool( poolConfig, "10.211.55.8");
                    }
                }
            }
            return jedisPool;
        }

        public static void release(JedisPool jedisPool, Jedis jedis){
            if(null!=jedis){
                jedis.close();
            }
        }
    }

    @Test
    public void test06(){

        JedisPool jedisPool = JedisPoolUtil.getJedisPoolnstance();

        Jedis jedis=null;

        try{

             jedis = jedisPool.getResource();
             jedis.set("aa","bb");
        }catch (Exception e){
        e.printStackTrace();
        }finally {
            JedisPoolUtil.release(jedisPool,jedis);
        }
    }
}
