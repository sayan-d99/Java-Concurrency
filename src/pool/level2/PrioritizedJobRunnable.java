package pool.level2;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class PrioritizedJobRunnable implements Runnable, Comparable<PrioritizedJobRunnable>{

    private final ThreadPriority priority;
    private final Runnable job;
    private final String id;

    public PrioritizedJobRunnable(BlockingQueue<PrioritizedJobRunnable> jobQueue, ThreadPriority priority, Runnable job){
        this.id = UUID.randomUUID().toString();
        this.priority = priority;
        this.job = job;
    }

    @Override
    public int compareTo(PrioritizedJobRunnable o) {
        return this.priority.compareTo(o.priority);
    }

    @Override
    public void run(){
        job.run();
    }

    @Override
    public String toString() {
        return "PrioritizedJobRunnable=[id="+id+",priority="+priority+"]";
    }
}
