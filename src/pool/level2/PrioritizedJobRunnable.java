package pool.level2;

import java.util.concurrent.BlockingQueue;

public abstract class PrioritizedJobRunnable implements Runnable, Comparable<PrioritizedJobRunnable>{

    ThreadPriority priority;

    public PrioritizedJobRunnable(ThreadPriority priority){
        this.priority = priority;
    }

    @Override
    public int compareTo(PrioritizedJobRunnable o) {
        return o.priority.compareTo(this.priority);
    }

    @Override
    abstract public void run();

}
