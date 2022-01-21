package com.cb.gulimall.search.thread;

import java.util.concurrent.*;

public class ThreadTest {
   public static ExecutorService executor = Executors.newFixedThreadPool(10);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         * 1）、继承Thread   Thread01
         *
         * 2）、实现Runnable接口
         *
         * 3）、实现Callable接口+FutureTask(可以拿到返回结果，可以处理异常)
         *
         * 4）、线程池
         */
      //  System.out.println("main---start");
//        Thread01 thread01 = new Thread01();
//        thread01.start();

//        Thread02 thread02 = new Thread02();
//        new Thread(thread02).start();

//        FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
//        new Thread(futureTask).start();
//        //阻塞等待整个线程执行完成，获取返回结果
//        Integer integer = futureTask.get();


          //  service.execute(new Runnable01());


        /**
         * 7大参数
         * int corePoolSize, 核心线程数一直存在除非（allowCoreThreadTimeOut）；线程池，创建好以后就准备就绪的线程数量，就等待来接受异步任务去执行
         * int maximumPoolSize, 最大线程数量；控制资源
         * long keepAliveTime,  存活时间。如果当前的线程数量大于core数量，释放空闲的线程。只要线程空闲大于指定的keepAliveTime
         * TimeUnit unit, 时间单位
         * BlockingQueue<Runnable> workQueue, 阻塞队列。如果任务很多，就会将目前多得任务放在队列里面。只要有线程空闲，就会去队列里面取出新的任务继续执行。
         * ThreadFactory threadFactory, 线程的创建工厂
         * RejectedExecutionHandler handler：如果队列蛮了，按照我们指定的拒绝策略拒绝执行任务
         *
         * 工作顺序：
         *  1）、线程池创建，准备好core数量的核心线程，准备任务
         *  1.1、core满了，就将再进来的任务收入阻塞队列中，空闲的core就会自己去阻塞队列获取任务执行
         *  1.2、阻塞队列满了，就会直接开新线程执行，最大只能开到max指定的数量
         *  1.3、max满了就用RejectedExecutionHandler拒绝任务
         *  1.4、max都执行完成，有很多空闲，在指定的时间keepAliveTime ，释放max-core的线程
         *
         *          new linkedBlockingDeque<>();默认是Integer的最大值。内存不够。根据系统压力测试的到自己系统的最大值
         *
         *   一个线程池 core 7； max 20 ，queue：50，100 并发进来怎么分配的；
         *   7个立即执行，50个进入等待，再开13个进行执行。剩余30个使用拒绝策略
         */
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
//                200,
//                10,
//                TimeUnit.SECONDS,
//                new LinkedBlockingDeque<>(100000),
//                Executors.defaultThreadFactory(),
//                new ThreadPoolExecutor.AbortPolicy());


        //Executors.newCachedThreadPool();
        //Executors.newFixedThreadPool();
        //Executors.newScheduledThreadPool();
        //Executors.newSingleThreadExecutor();




        //System.out.println("main---end");
        System.out.println("main---start");
//        CompletableFuture.runAsync(()->{
//            System.out.println("当前线程："+Thread.currentThread().getId());
//            int i=10/2;
//            System.out.println("运行的结果"+i);
//        },executor);


        /**
         * 方法完成后的感知
         */
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行的结果" + i);
//            return i;
//        }, executor).whenComplete((res,excption)->{
//            //能感知异常信息，但是没法修改返回数据
//            System.out.println("异步任务成功完成了....结果是："+res+"；异常是："+excption);
//        }).exceptionally(throwable -> {
//            //可以感知异常，同时返回默认值
//            return 10;
//        });

        /**
         * 方法完成后的处理
         */
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行的结果" + i);
//            return i;
//        }, executor).handle((res,exc)->{
//            if(res!=null){
//                return res*2;
//            }
//            if(exc!=null){
//                return 0;
//            }
//            return 1;
//        });


        /**
         *线程串行化
         * 1、thenRun:不能获取到上一步的执行结果
         * .thenRunAsync(()->{
         *             System.out.println("任务2启动了");
         *         }, executor);
         * 2、thenAcceptAsync能接受上一步结果，但是无返回值
         * 3、thenApplyAsync能接受上一步结果，有返回值
         */

//        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行的结果" + i);
//            return i;
//        }, executor).thenApplyAsync(res -> {
//            System.out.println("运行的结果" + res);
//            return "hello" + res;
//        }, executor);


        /**
         *两个都完成
         */
//        CompletableFuture<Object> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务1线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("任务1结束：" );
//            return i;
//        }, executor);
//
//        CompletableFuture<Object> future02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务2线程：" + Thread.currentThread().getId());
//
//            try {
//                Thread.sleep(3000);
//                System.out.println("任务2结束：" );
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "hellow";
//        }, executor);


//        future01.runAfterBothAsync(future02,()->{
//            System.out.println("任务3开始。。。。");
//        },executor);

//        future01.thenAcceptBothAsync(future02,(f1,f2)->{
//            System.out.println("任务3开始。。。。之前的结果："+f1+"==>"+f2);
//        },executor);
//
//        CompletableFuture<String> future = future01.thenCombineAsync(future02, (f1, f2) -> {
//            return f1 + ":" + f2 + "-->haha";
//        }, executor);


        /**
         * 两个任务，只要有一个完成，我们就执行任务3
         * runAfterEitherAsync:不感知结果，自己不返回值
         * acceptEitherAsync: 感知结果，自己不返回值
         * applyToEitherAsync :感知结果，有返回值
         */
//        future01.runAfterEitherAsync(future02,()->{
//            System.out.println("任务3开始。。");
//        },executor);
//
//        future01.acceptEitherAsync(future02,(res)->{
//            System.out.println("任务3开始。。"+res);
//        },executor);
//
//        CompletableFuture<String> future = future01.applyToEitherAsync(future02, res -> {
//            System.out.println("任务3开始。。"+res);
//           return res.toString() + "_哈哈";
//        }, executor);
//        System.out.println("main---end"+future.get());

        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片信息");
            return "hello.jpg";
        },executor);
        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性");
            return "黑色+256";
        },executor);
        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的介绍");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "华为";
        },executor);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futureImg, futureDesc, futureAttr);
        allOf.get();
        System.out.println("main---end"+futureImg.get()+"="+futureDesc.get()+"="+futureAttr.get());


        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImg, futureDesc, futureAttr);
        anyOf.get();//等待所有结果完成
        System.out.println("main---end"+anyOf.get());

    }
    public static class Thread01 extends  Thread{
        @Override
        public void run() {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i=10/2;
            System.out.println("运行的结果"+i);
        }
    }

    public  static class Runnable01 implements Runnable{
        @Override
        public void run() {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i=10/2;
            System.out.println("运行的结果"+i);
        }
    }

    public static class Callable01 implements Callable<Integer>{
        @Override
        public Integer call() {
            System.out.println("当前线程："+Thread.currentThread().getId());
            int i=10/2;
            System.out.println("运行的结果"+i);
            return i;
        }
    }
}
