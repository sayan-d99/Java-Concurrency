package pool.level1;

import java.util.concurrent.BlockingQueue;

public class ThreadPoolRunnable implements Runnable{

    private final BlockingQueue<? extends Runnable> jobQueue;
    private boolean isStopped = false;

    public ThreadPoolRunnable(BlockingQueue<? extends Runnable> jobQueue){
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        while(!isStopped){
            try {
                Runnable r = this.jobQueue.take();
                System.out.printf("%s : Executing job %s\n", threadName(), r);
                r.run();
                System.out.printf("%s : Completed job %s\n", threadName(), r);
            } catch (InterruptedException e) {
                System.out.printf("%s : Error fetching job from queue\n", threadName());
            }
        }
        System.out.printf("%s : Thread has been stopped. Exiting run()\n", threadName());
    }

    public void stop(){
        this.isStopped = true;
        System.out.printf("%s is stopping\n", threadName());
        Thread.currentThread().interrupt();
    }

    private String threadName(){
        return Thread.currentThread().getName();
    }
}
