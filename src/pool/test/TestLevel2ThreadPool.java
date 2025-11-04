package pool.test;

import pool.level2.*;
import util.ThreadUtils;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

public class TestLevel2ThreadPool {

    public static void main(String[] args) throws InterruptedException {
        RejectionPolicy<PrioritizedJobRunnable> rp = new PrioritizedDropRejectionPolicy();
        ThreadPoolLevel2 threadPoolLevel2 = new ThreadPoolLevel2(1, 3,5, rp);
        Random random = new Random();

        // Each producer produces 1 <= n <= 5 jobs
        // Each job waits for an arbitrary time for 1 to 2 seconds

        Runnable jobRunnable = () -> {
            int waitingTime = random.nextInt(2000, 5001);
           ThreadUtils.log("Inside Job Runnable: Starting job. Will sleep for " + waitingTime + " ms");
            try{
                Thread.sleep(waitingTime);
            }
            catch(InterruptedException e){
            }
           ThreadUtils.log("Inside Job Runnable: Starting job. Will sleep for " + waitingTime + " ms");
        };

        Thread producerOne = new Thread(() -> {
            IntStream.rangeClosed(1, random.nextInt(1, 3))
                    .forEach((i) -> {
                        try {
                            threadPoolLevel2.submit(jobRunnable, ThreadPriority.HIGH);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }, "L2TP-Producer-One");

        Thread producerTwo = new Thread(() -> {
            IntStream.rangeClosed(1, random.nextInt(1, 3))
                    .forEach((i) -> {
                        try {
                            threadPoolLevel2.submit(jobRunnable, ThreadPriority.MEDIUM);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }, "L2TP-Producer-Two");

        Thread producerThree = new Thread(() -> {
            IntStream.rangeClosed(1, random.nextInt(1, 3))
                    .forEach((i) -> {
                        try {
                            threadPoolLevel2.submit(jobRunnable, ThreadPriority.LOW);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }, "L2TP-Producer-Three");

        producerOne.start();
        producerTwo.start();
        producerThree.start();

        producerOne.join();
        producerTwo.join();
        producerThree.join();

//        threadPoolLevel2.waitUntilAllTasksFinished();
    }

}
