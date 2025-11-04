package pool.level1;

import util.ThreadUtils;

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
               ThreadUtils.log("Waiting for job from queue");
                Runnable r = this.jobQueue.take();
               ThreadUtils.log("Executing job %s", r);
                r.run();
               ThreadUtils.log("Completed job %s", r);
            } catch (InterruptedException e) {
               ThreadUtils.log("Error fetching job from queue");
            }
        }
       ThreadUtils.log("Thread has been stopped. Exiting run()");
    }

    public void stop(){
        this.isStopped = true;
       ThreadUtils.log("%s is stopping", threadName());
        Thread.currentThread().interrupt();
    }

    public void waitToFinish() throws InterruptedException {
        Thread.currentThread().join();
    }

    private String threadName(){
        return Thread.currentThread().getName();
    }
}
