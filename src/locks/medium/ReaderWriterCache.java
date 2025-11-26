package locks.medium;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReaderWriterCache<K, V> {

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock rLock = rwLock.readLock();
    private final ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
    private final Map<K, V> cache = new HashMap<>();

    public void put(K key, V value){
        log("Waiting for write lock");
        wLock.lock();
        log("Write lock acquired");
        try{
            cache.put(key, value);
            log("Key - " + key + " and value - " + value + " put in cache");
        }finally {
            log("Unlocking write lock");
            wLock.unlock();
            log("Unlocked write lock");

        }
    }

    public V get(K key){
        log("Waiting for read lock");
        rLock.lock();
        log("Read lock acquired");
        try{
            log("Reading for a second");
            Thread.sleep(1000);
            log("Read completed after a second");
            return cache.get(key);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally{
            log("Unlocking read lock");
            rLock.unlock();
            log("Unlocked read lock");
        }
    }

    private void log(String s){
        System.out.printf("%s: %s: %s\n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss SSS")),
                Thread.currentThread().getName(),
                s);
    }

    public static void main(String[] args) {
        ReaderWriterCache<Integer, Integer> x = new ReaderWriterCache<>();

        Runnable reader = () -> {
            for(int i = 1; i <= 3; i++){
                x.get(i);
            }
        };

        Runnable writer = () -> {
            for(int i=1; i<=3; i++){
                x.put(1,1);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread[] y = new Thread[]{
                new Thread(reader, "R1"),
                new Thread(reader, "R2"),
                new Thread(writer, "W1")};
        Arrays.stream(y).forEach(Thread::start);
        Arrays.stream(y).forEach((t) -> {
            try {
                t.join();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
