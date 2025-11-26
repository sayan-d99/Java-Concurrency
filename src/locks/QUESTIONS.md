## 🟢 **Easy Level (Basics & Mutual Exclusion)**

> Focus: learn how to guard shared state, avoid race conditions, and use `ReentrantLock`.

1. **Print Odd and Even Numbers Alternately**

    * Two threads: one prints odd numbers, one prints even numbers up to N.
    * Use a lock or `Condition` to coordinate.

2. **Thread-safe Counter**

    * Implement `increment()`, `decrement()`, `getValue()` safely using `ReentrantLock`.

3. **Simple Logger (Single Writer)**

    * Multiple threads call `log(String message)` — ensure log lines don’t interleave.
    * One thread writes, others wait if file access is busy.

4. **Bank Account**

    * Implement a thread-safe `deposit(amount)` and `withdraw(amount)` using a lock.

5. **Bounded Buffer (Simple Version)**

    * Implement a fixed-size list with thread-safe `add()` and `remove()` methods using `synchronized` or `ReentrantLock`.

---

## 🟡 **Medium Level (Conditions, Read/Write Locks, Coordination)**

> Focus: thread coordination, signaling, and read/write separation.

6. **Producer–Consumer using `ReentrantLock` + `Condition`**

    * Re-implement the classic problem **without** `BlockingQueue`.
    * Use two conditions: `cannotConsume` and `cannotProduce`.

7. **Reader–Writer Cache**

    * Implement a cache where:

        * Multiple readers can access simultaneously.
        * Writers get exclusive access.
    * Use `ReentrantReadWriteLock`.

8. **Custom Countdown Latch**

    * Implement your own version of `CountDownLatch` using `ReentrantLock` + `Condition`.

9. **Alternating Threads (“FooBar” Problem)**

    * Thread A prints “Foo”, Thread B prints “Bar”, alternately N times.

10. **Thread-safe Rate Limiter**

    * Allow only `N` requests per second using `Semaphore` or `ReentrantLock`.

---

## 🔴 **Hard Level (Complex Coordination & Lock Patterns)**

> Focus: multiple locks, fairness, avoidance of deadlocks, lock-free alternatives.

11. **Dining Philosophers**

    * Classic deadlock problem.
    * Use multiple locks (one per chopstick), implement strategies to **avoid or detect deadlock**.

12. **Read-Write-Upgrade Problem**

    * Simulate a situation where a thread needs to upgrade from a read lock to a write lock safely using `ReentrantReadWriteLock`.

13. **Implement a Thread-safe BlockingQueue**

    * From scratch — using only `ReentrantLock` and two `Condition`s.
    * Support `put()` and `take()` with blocking behavior.

14. **Build a Priority Thread Pool**

    * Use `PriorityBlockingQueue` + `ReentrantLock`.
    * Add pause/resume and graceful shutdown.

15. **Lock Ordering Deadlock Demo**

    * Write code that intentionally deadlocks using two locks.
    * Then fix it by enforcing a **consistent lock acquisition order**.

16. **Implement Your Own ReentrantLock**

    * Using `AbstractQueuedSynchronizer (AQS)` — advanced but extremely insightful.
    * Understand internal mechanics of Java’s lock framework.

17. **Concurrent Bounded Set**

    * Implement a set with limited capacity where `add()` blocks when full and `remove()` unblocks producers.

18. **Fair Print Scheduler**

    * Several threads submit “print jobs.”
    * Jobs are served fairly based on submission order (use fair `ReentrantLock`).

---

## 🧠 **Bonus "Think Like a Concurrency Engineer" Tasks**

* Detect and fix **livelock** in a retrying algorithm.
* Use `StampedLock`’s **optimistic read** for a read-heavy workload (e.g., stock price cache).
* Benchmark `ReentrantLock` vs `synchronized` for a CPU-bound shared counter.

---

