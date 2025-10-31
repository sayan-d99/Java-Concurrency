package pool.level2;

public enum ThreadPriority{

    HIGH(Thread.MAX_PRIORITY), MEDIUM(Thread.NORM_PRIORITY), LOW(Thread.MIN_PRIORITY);

    private final int priority;

    private ThreadPriority(int p){
        this.priority = p;
    }

}
