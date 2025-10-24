package waitandnotify;

import java.util.Arrays;

public class MyBuffer {

    private int[] buffer;
    private int nElementsInBuffer = 0;

    public MyBuffer(int bufferSize){
        this.buffer = new int[bufferSize];
    }

    private String threadName(){ return Thread.currentThread().getName();}

    public synchronized int read(){
        System.out.printf("MyBuffer#read Thread: %s Entering\n", threadName());
        if(nElementsInBuffer == 0){
            try {
                System.out.printf("[MyBuffer#read] Thread: %s. No value to read as buffer is empty. Buffer - %s. Thread will wait until buffer has value\n", threadName(), this);
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.printf("[MyBuffer#read] Thread - %s will read from position %d of buffer - %s\n", threadName(), nElementsInBuffer-1, this);
        int val = buffer[nElementsInBuffer-1];
        buffer[nElementsInBuffer-1] = 0;
        System.out.printf("[MyBuffer#read] Thread - %s has read from position %d of buffer - %s. Value - %d\n", threadName(), nElementsInBuffer-1, this, val);
        System.out.printf("[MyBuffer#read] Thread - %s will not notify all other threads as read operation is complete\n", threadName());
        nElementsInBuffer--;
        notifyAll();
        System.out.printf("[MyBuffer#read] Thread - %s has notified all other threads\n", threadName());
        System.out.printf("[MyBuffer#read] Thread: %s leaving\n", threadName());
        return val;
    }

    public synchronized void put(int n){
        System.out.printf("[MyBuffer#put] Thread: %s Entering\n", threadName());
        if(nElementsInBuffer == buffer.length){
            try {
                System.out.printf("[MyBuffer#put] Thread: %s. Unable to write to buffer as it is full. Buffer - %s. Thread will wait until buffer has vacancy\n", threadName(), this);
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.printf("[MyBuffer#put] Thread - %s will put value - %d at position %d in buffer - %s\n", threadName(), n, nElementsInBuffer, this);
        buffer[nElementsInBuffer] = n;
        System.out.printf("[MyBuffer#put] Thread - %s has put value - %d at position %d in buffer - %s\n", threadName(), n, nElementsInBuffer, this);
        nElementsInBuffer++;
        System.out.printf("[MyBuffer#put] Thread - %s will not notify all other threads as write operation is complete\n", threadName());
        notifyAll();
        System.out.printf("[MyBuffer#put] Thread - %s has notified all other threads\n", threadName());
        System.out.printf("[MyBuffer#put] Thread: %s leaving\n", threadName());
    }

    @Override
    public String toString(){
        return "Buffer{data=" + Arrays.toString(this.buffer) + "}";
    }

}
