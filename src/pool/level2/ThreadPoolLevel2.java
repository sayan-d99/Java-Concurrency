package pool.level2;

import pool.level1.ThreadPoolRunnable;
import util.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ThreadPoolLevel2 {

    private final int maxPoolSize;
    private volatile boolean isStopped = false;
    private final AtomicInteger nThreadsInPool = new AtomicInteger(0);
    public BlockingQueue<PrioritizedJobRunnable> jobQueue;
    private List<ThreadPoolRunnable> workers;
    private RejectionPolicy<PrioritizedJobRunnable> rejectionPolicy;

    public ThreadPoolLevel2(int minPoolSize, int maxPoolSize, int maxJobCount, RejectionPolicy<PrioritizedJobRunnable> rejectionPolicy){
        ThreadUtils.log("Initialising worker pool with [Min Pool Count = %d, Max Pool Count = %d, Max Job Count = %d]", minPoolSize, maxPoolSize,maxJobCount);
        this.maxPoolSize = maxPoolSize;
        this.rejectionPolicy = rejectionPolicy;
        if(maxJobCount < this.maxPoolSize){
            throw new IllegalArgumentException("Maximum job count cannot be less than maximum pool size");
        }
        this.jobQueue = new PriorityBlockingQueue<>(maxJobCount);
        this.workers = new ArrayList<>();
        IntStream.rangeClosed(1, minPoolSize).forEach((i) -> {this.addWorker();});
        ThreadUtils.log("Worker pool started with [Min Pool Count = %d, Max Pool Count = %d, Max Job Count = %d]", minPoolSize, maxPoolSize,maxJobCount);
    }

    public void submit(Runnable job, ThreadPriority priority) throws InterruptedException {
        ThreadUtils.log("[ThreadPoolLevel2#submit] Entering. Job Queue - %s. No of Threads In Pool: %d", jobQueue, nThreadsInPool.get());
        if(this.isStopped) throw new IllegalStateException("Thread pool has been stopped");
        // If job queue is full and maxPoolSize has been reached, reject incoming jobs
        // If job queue is full and maxPoolSize has not been reached, add new worker and offer job
        PrioritizedJobRunnable r = new PrioritizedJobRunnable(jobQueue, priority, job);
        if(this.jobQueue.size() >= nThreadsInPool.get()) {
            ThreadUtils.log("[ThreadPoolLevel2#submit] Number of Jobs == Number of Threads");
            if (nThreadsInPool.get() == maxPoolSize) {
                ThreadUtils.log("[ThreadPoolLevel2#submit] Max Threads reached. Invoking rejection policy");
                rejectionPolicy.reject(r, this);
                return;
            }else{
                ThreadUtils.log("[ThreadPoolLevel2#submit] Max Pool not reached. Adding worker");
                addWorker();
            }
        }
        ThreadUtils.log("[ThreadPoolLevel2#submit] Adding Job - %s with priority %s", r, priority);
        if(!this.jobQueue.offer(r)) throw new RuntimeException("Could not add job due to unknown reasons");
        ThreadUtils.log("[ThreadPoolLevel2#submit] Added Job - %s with priority %s. Job Queue -%s", r, priority, this.jobQueue);
        ThreadUtils.log("[ThreadPoolLevel2#submit] Exiting submit");
    }

    private void addWorker(){
        ThreadUtils.log("[ThreadPoolLevel2#addWorker] Entering");
        if(nThreadsInPool.get() == maxPoolSize){
            ThreadUtils.log("Cannot add worker as pool has reached maximum count %d", tname(), maxPoolSize);
        }
        ThreadUtils.log("[ThreadPoolLevel2#addWorker]  Adding a new worker to the pool");
        ThreadPoolRunnable nr = new ThreadPoolRunnable(jobQueue);
        this.workers.add(nr);
        ThreadUtils.log("[ThreadPoolLevel2#addWorker] Added worker - %s to the pool", nr);
        int tIx = this.nThreadsInPool.incrementAndGet();
        new Thread(nr, "Thread-Pool-Worker-"+tIx).start();
        ThreadUtils.log("[ThreadPoolLevel2#addWorker] Number of threads in use: %d", tIx);
        ThreadUtils.log("[ThreadPoolLevel2#addWorker] Exiting");
    }

    public void waitUntilAllTasksFinished() throws InterruptedException {
        try {
        shutdown();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void shutdown(){
        ThreadUtils.log("Stopping Thread Pool");
        this.isStopped = true;
        this.workers.forEach(ThreadPoolRunnable::stop);
        ThreadUtils.log("Thread Pool stopped");
    }

    private String tname(){
        return Thread.currentThread().getName();
    }

}
