package pool.level2;

import pool.level1.ThreadPoolRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class ThreadPoolLevel2 {

    private final int maxPoolSize;
    private volatile boolean isStopped = false;
    private final AtomicInteger nThreadsInUse = new AtomicInteger(0);
    private BlockingQueue<PrioritizedJobRunnable> jobQueue;
    private List<ThreadPoolRunnable> workers;
    private RejectionPolicy<PrioritizedJobRunnable> rejectionPolicy;

    public ThreadPoolLevel2(int minPoolSize, int maxPoolSize, int maxJobCount, RejectionPolicy<PrioritizedJobRunnable> rejectionPolicy){
        System.out.printf("%s: Initialising worker pool with [Min Pool Count = %d, Max Pool Count = %d, Max Job Count = %d]\n",tname(), minPoolSize, maxPoolSize,maxJobCount);
        this.maxPoolSize = maxPoolSize;
        this.rejectionPolicy = rejectionPolicy;
        if(maxJobCount < this.maxPoolSize){
            throw new IllegalArgumentException("Maximum job count cannot be less than maximum pool size");
        }
        this.jobQueue = new PriorityBlockingQueue<>(maxJobCount);
        IntStream.rangeClosed(1, minPoolSize).forEach((i) -> {this.addWorker();});
        System.out.printf("%s: Worker pool started with [Min Pool Count = %d, Max Pool Count = %d, Max Job Count = %d]\n",tname(), minPoolSize, maxPoolSize,maxJobCount);
    }

    public void submit(PrioritizedJobRunnable r) throws InterruptedException {
        if(this.isStopped) throw new IllegalStateException("Thread pool has been stopped");
        int tempnThreads = nThreadsInUse.get();
        // If job queue is full and maxPoolSize has been reached, reject incoming jobs
        // If job queue is full and maxPoolSize has not been reached, add new worker and offer job
        if(this.jobQueue.size() == tempnThreads) {
            if (tempnThreads == maxPoolSize) {
                rejectionPolicy.reject(r, this);
                return;
            }else{
                addWorker();
            }
        }
        if(!this.jobQueue.offer(r)) throw new RuntimeException("Could not add job due to unknown reasons");
    }

    public void shutdown(){
        System.out.println(tname() + ": Stopping Thread Pool");
        this.isStopped = true;
        this.workers.forEach(ThreadPoolRunnable::stop);
        System.out.println(tname() +": Thread Pool stopped");
    }

    private void addWorker(){
        if(nThreadsInUse.get() == maxPoolSize) throw new RuntimeException("Cannot add worker as max worker size has been reached");
        System.out.printf("%s: Adding a new worker to the pool\n", tname());
        ThreadPoolRunnable nr = new ThreadPoolRunnable(jobQueue);
        this.workers.add(nr);
        System.out.printf("%s: Added worker - %s to the pool\n", tname(), nr);
        int tIx = this.nThreadsInUse.incrementAndGet();
        new Thread(nr, "Thread-Pool-Worker-"+tIx);
        System.out.printf("%s: Number of threads in use: %d\n", tname(), tIx);
    }

    public void waitUntilAllTasksFinished() throws InterruptedException {
        while(!this.jobQueue.isEmpty()){
            try {
                Thread.sleep(10);
            }catch(InterruptedException e){
                throw new RuntimeException(e);
            }
        }
        shutdown();
    }

    private String tname(){
        return Thread.currentThread().getName();
    }

}
