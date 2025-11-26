package locks.medium;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CountDownLatch requirements
 * Accept a count input
 * A blocked Thread can call countDown() and be released
 * A blocked thread calling await will be blocked until all other threads have called countDown()
 */
public class MyCountDownLatch {

    private int count;
    private boolean hasFinished = false;
    private final ReentrantLock awaitLock;
    private final Condition isCountZero;

    public MyCountDownLatch(int count){
        this.count = count;
        this.awaitLock = new ReentrantLock();
        this.isCountZero = awaitLock.newCondition();
    }

    public void countDown(){
        try {
            awaitLock.lock();
            System.out.printf("%s has called countDown(). Current count: %d\n",
                    Thread.currentThread().getName(),
                    this.count);
            this.count--;
            if(this.count == 0) {
                this.hasFinished = true;
                isCountZero.signalAll();
            }
            System.out.printf("%s has executed countDown(). Current count: %d\n",
                    Thread.currentThread().getName(),
                    this.count);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            awaitLock.unlock();
        }
    }

    public void await(){
        try{
            awaitLock.lock();
            System.out.printf("%s has called await(). Current count: %d. Waiting for count to become 0\n",
                    Thread.currentThread().getName(),
                    this.count);
            while(!hasFinished){
                isCountZero.await();
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally {
            System.out.printf("%s Current count: %d. Wait over. Count has become 0\n",
                    Thread.currentThread().getName(),
                    this.count);
            isCountZero.signalAll();
            awaitLock.unlock();
        }
    }

    static class WorkerThread extends Thread{

        MyCountDownLatch latch;
        String name;

        public WorkerThread(MyCountDownLatch l, String name){
            super(name);
            this.latch = l;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                System.out.printf("%s doing some work\n", name);
                Thread.sleep(new Random().nextInt(1000,3000));
                this.latch.countDown();
                System.out.printf("%s has completed work\n", name);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        MyCountDownLatch l = new MyCountDownLatch(3);
        Thread w1 = new WorkerThread(l, "W1");
        Thread w2 = new WorkerThread(l, "W2");
        Thread w3 = new WorkerThread(l, "W3");

        w1.start();
        w2.start();
        w3.start();

        l.await();

    }

}
