package locks.medium;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static util.ThreadUtils.log;

class Request{

    private final String requestId;

    public Request(String requestId){
        this.requestId = requestId;
    }

    public void processRequest(){
        try {
            Thread.sleep(new Random().nextInt(1000,3000));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getRequestId() {
        return requestId;
    }
}

/**
 * Provides a thread safe rate limiter with the following characteristics -
 * Permits only N requests per S seconds
 * If a request completes within S seconds, the permit count should be restored by +1
 * If all the permits have been exhausted for the current S second period, the incoming request is blocked until any ongoing request finishes
 */
public class ThreadSafeRateLimiter {

    private final int noOfRequestsAllowed;
    private volatile int requestCount;
    private ReentrantLock lock;
    private volatile boolean shutdown = false;
    private Condition canBeProcessed;
    private ScheduledExecutorService countResetService;
    private Runnable resetCountRunnable;

    public ThreadSafeRateLimiter(int noOfRequests, int seconds){
        this.requestCount = 0;
        this.noOfRequestsAllowed = noOfRequests;
        this.lock = new ReentrantLock(true);
        this.canBeProcessed = this.lock.newCondition();

        this.resetCountRunnable = () -> {
            while (!shutdown) {
                try {
                    log("%s Waiting for %d seconds before resetting count", Thread.currentThread().getName(), seconds);
                    log("%s Lock hold count: %d", Thread.currentThread().getName(), lock.getHoldCount());
                    Thread.sleep(seconds * 1000L);
                    lock.lock();
                    this.requestCount = 0;
                    log("%s Count has been reset to 0", Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    log("%s Waking up waiting threads", Thread.currentThread().getName());
                    canBeProcessed.signalAll();
                    lock.unlock();
                }
            }
        };
        this.resetCount();

//        this.resetCountUsingExecutorService(seconds);
    }

    private void resetCountUsingExecutorService(int seconds){
        this.countResetService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Count-Reset");
            t.setDaemon(true);
            return t;
        });
        this.countResetService.scheduleWithFixedDelay(this.resetCountRunnable, seconds, seconds, TimeUnit.SECONDS);
    }

    /**
     * Need to run this piece of code in a separate thread. If it is not run in another thread,
     * then the thread instantiating the ThreadSafeRateLimiter object will get stuck. In this example, the main thread will get stuck.
     * Try to run the below code without a separate thread
     * Alternatively, one can use a {@link java.util.concurrent.ScheduledExecutorService} to achieve the same as shown below
     */
    private void resetCount(){
        Thread resetThread = new Thread(this.resetCountRunnable, "Count-Reset");
        resetThread.setDaemon(true);
        resetThread.start();
    }

    public void processRequest(Request r){
        try{
            log("Inside processRequest(): Waiting for Lock ");
            lock.lock();
            log("Inside processRequest(): Lock has been acquired for incrementing counter");
            while(requestCount >= noOfRequestsAllowed){
                log("%s Request %s is rate limited. Waiting for ongoing requests to complete", Thread.currentThread().getName(), r.getRequestId());
                canBeProcessed.await();
            }
            this.requestCount++;
            log("%s Request Count incremented. Current count: %d", Thread.currentThread().getName(), this.requestCount);
            lock.unlock();
            log("Inside processRequest(): Lock has been released after incrementing counter");
            log("%s Processing Request %s", Thread.currentThread().getName(), r.getRequestId());
            r.processRequest();
            log("%s Request Processed %s", Thread.currentThread().getName(), r.getRequestId());
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            try{
                log("Inside processRequest(): Lock has been acquired for decrementing counter");
                lock.lock();
                this.requestCount = Math.max(0, this.requestCount-1);
                log("%s Request Count decremented. Permit released. Current count: %d", Thread.currentThread().getName(), this.requestCount);
            }finally{
                if(this.requestCount < noOfRequestsAllowed){
                    canBeProcessed.signalAll();
                }
                lock.unlock();
                log("Inside processRequest(): Lock has been released after decrementing counter");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadSafeRateLimiter rl = new ThreadSafeRateLimiter(3,10);
        Runnable requestRunner = () -> {
            Request r = new Request(UUID.randomUUID().toString());
            rl.processRequest(r);
        };
        Thread[] threads = new Thread[10];
        for(int i=0;i<threads.length;i++){
            threads[i] = new Thread(requestRunner, "Thread-"+i);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

}
