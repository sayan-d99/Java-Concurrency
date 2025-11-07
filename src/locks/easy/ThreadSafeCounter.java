package locks.easy;

import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeCounter {

    private ReentrantLock lock = new ReentrantLock(true);
    private volatile int counter = 0;

    public int getValue(){
        try{
            lock.lock();
            System.out.printf("[getValue] Lock acquired by thread %s. Counter: %d\n", Thread.currentThread().getName(), counter);
            return counter;
        }finally {
            lock.unlock();
        }
    }

    public void increment(){
        try{
            lock.lock();
            System.out.printf("Lock acquired by thread %s. Counter: %d\n", Thread.currentThread().getName(), counter);
            counter = counter + 1;
            System.out.printf("%s: After incrementing value. Counter: %d\n", Thread.currentThread().getName(), counter);
        } finally {
            lock.unlock();
        }
    }

    public void decrement(){
        try{
            lock.lock();
            System.out.printf("Lock acquired by thread %s. Counter: %d\n", Thread.currentThread().getName(), counter);
            counter = counter - 1;
            System.out.printf("%s: After decrementing value. Counter: %d\n", Thread.currentThread().getName(), counter);
        } finally {
            lock.unlock();
        }
    }

}
