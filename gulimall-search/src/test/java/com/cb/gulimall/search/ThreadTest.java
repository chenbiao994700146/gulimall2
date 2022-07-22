package com.cb.gulimall.search;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ThreadTest {


    public static class ThreadB implements Runnable {
        int count = 1;
        int num;

        public ThreadB(int num) {
            this.num = num;
            System.out.println("创建线程" + num);
        }

        @Override
        public void run() {
            while (true) {
                System.out.println("线程" + num + "计数" + count);


                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
                if (count == 3) {
                    break;
                }
            }

        }


    }

    @Test
    public static void main(String[] args) {
        ThreadB a1 = new ThreadB(1);
        ThreadB a2 = new ThreadB(2);

        // a1.run();
        // a2.run();

        Thread thA = new Thread(a1, "线程A");
        Thread thb = new Thread(a2, "线程B");
        System.out.println(thA.getName());
        System.out.println(thb.getName());
        thA.start();
        thb.start();

    }
}
