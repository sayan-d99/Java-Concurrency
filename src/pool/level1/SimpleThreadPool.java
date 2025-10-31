package pool.level1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleThreadPool {

    private int poolSize;
    private BlockingQueue<Runnable> jobQueue;
    private List<ThreadPoolRunnable> poolWorkers;
    private boolean isStopped = false;

    public SimpleThreadPool(int poolSize, int maxJobs){
        this.poolSize = poolSize;
        this.jobQueue = new LinkedBlockingQueue<>(maxJobs);
        this.poolWorkers = new ArrayList<>();
        ThreadPoolRunnable worker;
        for(int i=1;i<=poolSize;i++){
            worker = new ThreadPoolRunnable(jobQueue);
            this.poolWorkers.add(worker);
            System.out.println("Starting Pool Worker : Pool-Thread-" + i);
            new Thread(worker, "Pool-Thread-"+i).start();
        }
    }

    public synchronized void submit(Runnable r) throws InterruptedException {
        if(isStopped) throw new IllegalStateException("Thread Pool has been stopped");
//        System.out.println("Submitting job - " + r);
        this.jobQueue.put(r);
//        System.out.println("Submitted job - " + r);
    }

    public synchronized void stop(){
        if(isStopped){
            throw new IllegalStateException("Thread Pool has already been stopped");
        }
        this.isStopped = true;
        for(ThreadPoolRunnable r : poolWorkers){
            r.stop();
        }
    }

}
