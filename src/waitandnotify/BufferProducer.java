package waitandnotify;

import java.util.Random;

public class BufferProducer implements Runnable {

    private MyBuffer b;

    public BufferProducer(MyBuffer b){
        this.b = b;
    }

    @Override
    public void run(){
        Random r = new Random();
        int i = 1;
        while(i <= 10){
            int waitTime = r.nextInt(500,1000);
            System.out.printf("[BufferProducer#run] Thread: %s will wait for %d ms\n",threadName(), waitTime);
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.printf("[BufferProducer#run] Thread: %s has completed waiting for %d ms\n",threadName(), waitTime);
            int val = r.nextInt();
            System.out.printf("[BufferProducer#run] Thread: %s is putting value - %d in buffer\n",threadName(), val);
            b.put(val);
            System.out.printf("[BufferProducer#run] Thread: %s has put value - %d in buffer\n",threadName(), val);
            i++;
        }
    }

    private String threadName(){ return Thread.currentThread().getName();}

}
