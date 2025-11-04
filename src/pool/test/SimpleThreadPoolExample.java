package pool.test;

import pool.level1.SimpleThreadPool;

import java.util.Random;

public class SimpleThreadPoolExample {

    public static void main(String[] args) throws InterruptedException {
        Random r = new Random();
        SimpleThreadPool threadPool = new SimpleThreadPool(3, 10);
        for(int i=1;i<=15;i++){
            threadPool.submit(() -> {
                int sleepDuration = r.nextInt(1000,10000);
                System.out.println(Thread.currentThread().getName() + " Executing task. Blocking for " + sleepDuration);
                try {
                    Thread.sleep(sleepDuration);
                    System.out.println(Thread.currentThread().getName() + " has completed task");
                } catch (InterruptedException e) {
                    //
                }
            });
            Thread.sleep(r.nextInt(500,2000));
        }
        threadPool.stop();
    }

}
