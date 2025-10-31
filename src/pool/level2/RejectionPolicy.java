package pool.level2;

import java.util.Collection;

/*
    If the job queue is full (i.e. max jobs have been added to the queue), then a RejectionPolicy can be used to
    take a decision on how to handle the rejection of incoming jobs
 */
public interface RejectionPolicy<T extends Runnable> {

    void reject(T job, ThreadPoolLevel2 threadPool);

}
