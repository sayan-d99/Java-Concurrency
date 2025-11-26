package locks.medium;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe token-bucket rate limiter.
 *
 * - capacity: max tokens stored
 * - refillTokensPerSecond: tokens added per second
 *
 * Uses lazy refill on each acquire/tryAcquire to avoid a periodic refill thread.
 */
public class TokenBucketRateLimiter {

    private final long capacity;
    private final double refillTokensPerSecond;

    // protected by lock
    private double storedTokens;           // fractional tokens allowed for precision
    private long lastRefillNanos;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition tokensAvailable = lock.newCondition();

    public TokenBucketRateLimiter(long capacity, double refillTokensPerSecond) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity > 0");
        if (refillTokensPerSecond <= 0) throw new IllegalArgumentException("refill > 0");
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
        this.storedTokens = capacity; // start full (change if you prefer empty start)
        this.lastRefillNanos = System.nanoTime();
    }

    /** Refill tokens based on elapsed time. Caller must hold lock. */
    private void refill() {
        long now = System.nanoTime();
        long elapsedNanos = now - lastRefillNanos;
        if (elapsedNanos <= 0) return;
        double tokensToAdd = (elapsedNanos / 1_000_000_000.0) * refillTokensPerSecond;
        if (tokensToAdd > 0.0) {
            storedTokens = Math.min(capacity, storedTokens + tokensToAdd);
            lastRefillNanos = now;
        }
    }

    /** Try acquire 1 token immediately. */
    public boolean tryAcquire() {
        lock.lock();
        try {
            refill();
            if (storedTokens >= 1.0) {
                storedTokens -= 1.0;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /** Try acquire with timeout (interruptible). Returns true if token acquired. */
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        long nanosTimeout = unit.toNanos(timeout);
        final long deadline = System.nanoTime() + nanosTimeout;

        lock.lockInterruptibly();
        try {
            for (;;) {
                refill();
                if (storedTokens >= 1.0) {
                    storedTokens -= 1.0;
                    return true;
                }
                long now = System.nanoTime();
                long remain = deadline - now;
                if (remain <= 0) return false;

                // Compute approximate wait time until at least 1 token becomes available
                double tokensNeeded = 1.0 - storedTokens;
                long nanosToNext = (long) Math.ceil((tokensNeeded / refillTokensPerSecond) * 1_000_000_000L);

                // wait for the smaller of remain and nanosToNext
                long waitNanos = Math.min(remain, nanosToNext);
                if (waitNanos <= 0) return false;

                long waited = tokensAvailable.awaitNanos(waitNanos);
                // spurious wakeups handled by loop
            }
        } finally {
            lock.unlock();
        }
    }

    /** Blocking acquire (interruptible) */
    public void acquire() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            for (;;) {
                refill();
                if (storedTokens >= 1.0) {
                    storedTokens -= 1.0;
                    return;
                }
                double tokensNeeded = 1.0 - storedTokens;
                long nanosToNext = (long) Math.ceil((tokensNeeded / refillTokensPerSecond) * 1_000_000_000L);
                tokensAvailable.awaitNanos(nanosToNext);
            }
        } finally {
            lock.unlock();
        }
    }

    /** Release (return) a token back to bucket. Useful if you reserved a token but want to rollback. */
    public void release() {
        lock.lock();
        try {
            refill();
            storedTokens = Math.min(capacity, storedTokens + 1.0);
            tokensAvailable.signal(); // wake one waiter
        } finally {
            lock.unlock();
        }
    }

    /** Snapshot of available tokens (may be fractional). */
    public double getAvailableTokens() {
        lock.lock();
        try {
            refill();
            return storedTokens;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucketRateLimiter rl = new TokenBucketRateLimiter(5, 2.0); // capacity 5, 2 tokens/sec

        Runnable worker = () -> {
            String name = Thread.currentThread().getName();
            try {
                rl.acquire();
                System.out.println(name + " got token at " + System.currentTimeMillis()/1000.0);
                // simulate work
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                rl.release(); // optional: return token if you want (depends on semantics)
            }
        };

        // start many threads trying to acquire
        for (int i = 0; i < 10; i++) {
            new Thread(worker, "T-" + i).start();
            Thread.sleep(200); // stagger starts
        }
    }

}
