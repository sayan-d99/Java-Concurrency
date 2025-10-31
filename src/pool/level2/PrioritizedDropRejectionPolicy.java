package pool.level2;

public class PrioritizedDropRejectionPolicy implements RejectionPolicy<PrioritizedJobRunnable>{

    @Override
    public synchronized void reject(PrioritizedJobRunnable job, ThreadPoolLevel2 threadPool) {

    }

}
