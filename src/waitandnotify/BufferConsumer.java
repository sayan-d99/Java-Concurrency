package waitandnotify;

import java.util.Random;

public class BufferConsumer implements Runnable {

    private MyBuffer b;

    public BufferConsumer(MyBuffer b){
        this.b = b;
    }

    @Override
    public void run(){
        Random r = new Random();
        int i = 1;
        while(i <= 10){
            int waitTime = r.nextInt(3000,7000);
            System.out.printf("[BufferConsumer#run] Thread: %s will wait for %d ms\n",threadName(), waitTime);
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.printf("[BufferConsumer#run] Thread: %s has completed waiting for %d ms\n",threadName(), waitTime);
            System.out.printf("[BufferConsumer#run] Thread: %s is waiting for value from buffer\n",threadName());
            int val = b.read();
            System.out.printf("[BufferConsumer#run] Thread: %s has read value - %d from buffer\n",threadName(), val);
            i++;
        }
    }

    private String threadName(){ return Thread.currentThread().getName();}

}
