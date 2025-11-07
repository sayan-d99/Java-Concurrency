package locks.easy;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PrintOddEven {

    ReentrantLock lock = new ReentrantLock();
    Condition oddNumbers = lock.newCondition();
    Condition evenNumbers = lock.newCondition();
    int limit;
    int numberToPrint;
    boolean isOddTurn = true;

    public PrintOddEven(int limit){
        this.limit = limit;
    }

    public void printOdd(){
        while(true){
            lock.lock();
            try{
                while(!isOddTurn){
                    oddNumbers.await();
                }

                if(numberToPrint > limit){
                    evenNumbers.signal();
                    break;
                }

                System.out.println(numberToPrint++);
                isOddTurn = false;
                evenNumbers.signal();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    public void printEven(){
        while(true){
            lock.lock();
            try{
                while(isOddTurn){
                    evenNumbers.await();
                }

                if(numberToPrint > limit){
                    oddNumbers.signal();
                    break;
                }

                System.out.println(numberToPrint++);
                isOddTurn = true;
                oddNumbers.signal();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

}