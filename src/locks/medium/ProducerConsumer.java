package locks.medium;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumer<T> {

    private Deque<T> q;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition cannotConsume = lock.newCondition();
    private final Condition cannotProduce = lock.newCondition();
    private final int maxCapacity;
    private int nELementsInQ = 0;

    public ProducerConsumer(int maxCapacity){
        this.maxCapacity = maxCapacity;
        this.q = new ArrayDeque<>();
    }

    public T consume(){
        try{
            lock.lock();
            while(nELementsInQ == 0){
                cannotConsume.await();
            }
            cannotProduce.signal();
            nELementsInQ--;
            return q.poll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
            lock.unlock();
        }
    }

    public void produce(T el){
        try{
            lock.lock();
            while(nELementsInQ == maxCapacity){
                cannotProduce.await();
            }
            cannotConsume.signal();
            nELementsInQ++;
            q.add(el);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
            lock.unlock();
        }
    }

}
