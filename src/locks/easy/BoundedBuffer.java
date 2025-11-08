package locks.easy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer<T> {

    private final int bufferSize;
    private final Queue<T> buffer;
    private final ReentrantLock lock;
    private int nElementsInBuffer;
    private final Condition isBufferFull;

    public BoundedBuffer(int bufferSize){
        this.bufferSize = bufferSize;
        buffer = new ArrayDeque<>(bufferSize);
        nElementsInBuffer = 0;
        lock = new ReentrantLock();
        isBufferFull = lock.newCondition();
    }

    public void add(T el){
        try{
            System.out.printf("%s: [add] Before Locking. %s\n", Thread.currentThread().getName(), this);
            lock.lock();
            System.out.printf("%s: [add] After Locking. %s\n", Thread.currentThread().getName(), this);
            while(nElementsInBuffer == bufferSize){
                System.out.printf("%s: [add] Condition is waiting.%s\n", Thread.currentThread().getName(), this);
                isBufferFull.await();
            }
            buffer.add(el);
            nElementsInBuffer++;
            System.out.printf("%s: [add] %s is added. %s\n", Thread.currentThread().getName(), el, this);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.printf("%s: [add] Before Releasing Lock. %s\n", Thread.currentThread().getName(), this);
            lock.unlock();
            System.out.printf("%s: [add] After Releasing Lock. %s\n", Thread.currentThread().getName(), this);
        }
    }

    public T remove(){
        try{
            System.out.printf("%s: [remove] Before Locking. %s\n", Thread.currentThread().getName(), this);
            lock.lock();
            System.out.printf("%s: [remove] After Locking. %s\n", Thread.currentThread().getName(), this);
            boolean signalAwake = nElementsInBuffer == bufferSize;
            T el = buffer.poll();
            if(null != el) nElementsInBuffer--;
            System.out.printf("%s: [remove] %s is removed. %s\n", Thread.currentThread().getName(), el, this);
            if(signalAwake) {
                System.out.printf("%s: [remove] Condition is signalling. %s\n", Thread.currentThread().getName(), this);
                isBufferFull.signal();
            }
            return el;
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            System.out.printf("%s: [remove] Before Releasing Lock.%s\n", Thread.currentThread().getName(),this);
            lock.unlock();
            System.out.printf("%s: [remove] After Releasing Lock. %s\n", Thread.currentThread().getName(), this);
        }
    }

    @Override
    public String toString(){
        return String.format("BoundedBuffer=[bufferSize=%d, buffer=%s, lock={%s, Hold Count = %d}, nElementsInBuffer = %d, Condition = %s]",bufferSize, buffer, lock, lock.getHoldCount(), nElementsInBuffer, isBufferFull);
    }

    public static void main(String[] args) throws InterruptedException{
        BoundedBuffer<Integer> boundedBuffer = new BoundedBuffer<>(3);
        Runnable adder = () -> {
            for(int i = 1; i <= 5; i++){
                boundedBuffer.add(i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Runnable remover = () -> {
            for(int i = 1; i <= 5; i++){
                boundedBuffer.remove();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread adder1 = new Thread(adder, "Adder-1");
        Thread adder2 = new Thread(adder, "Adder-2");
        Thread adder3 = new Thread(adder, "Adder-3");

        Thread remover1 = new Thread(remover, "Remover-1");
        Thread remover2 = new Thread(remover, "Remover-2");
        Thread remover3 = new Thread(remover, "Remover-3");

        adder1.start(); adder2.start(); adder3.start();
        remover1.start();remover2.start();remover3.start();
        adder1.join(); adder2.join(); adder3.join(); remover1.join(); remover2.join(); remover3.join();
    }

}
