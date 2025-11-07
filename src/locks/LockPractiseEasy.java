package locks;

import locks.easy.BankAccount;
import locks.easy.PrintOddEven;
import locks.easy.SimpleLogger;
import locks.easy.ThreadSafeCounter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class LockPractiseEasy {

    public static void printOddAndEvenAlternatively(){
        PrintOddEven printer = new PrintOddEven(10);
        Thread t1 = new Thread(printer::printOdd, "Thread-One");
        Thread t2 = new Thread(printer::printEven, "Thread-Two");

        t1.start();
        t2.start();
    }

    public static void threadSafeCounter(){
        ThreadSafeCounter counter = new ThreadSafeCounter();
        Thread t1 = new Thread(() -> {
            for(int i = 1; i <= 3; i++){
                counter.increment();
                System.out.println("Incrementer: " + counter.getValue());
            }
        }, "Thread-One");
        Thread t2 = new Thread(() -> {
            for(int i = 1; i <= 3; i++){
                counter.decrement();
                System.out.println("Decrementer: " + counter.getValue());
            }
        }, "Thread-Two");
        t1.start();
        t2.start();
    }

    public static void simpleLogger() throws IOException {
        SimpleLogger log = new SimpleLogger(Path.of("C:\\Users\\sayan\\Desktop\\Dev\\Concurrency\\ConcurrencyCodePractice\\Test-Simple-Logger.txt"));
        Runnable r = () -> {
            for(int i=1; i<=3; i++){
                log.log(String.format("%s : Message : %d", Thread.currentThread().getName(), i));
            }
        };

        for(int i=1;i<=5;i++){
            new Thread(r, "Thread-"+i).start();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        printOddAndEvenAlternatively();
//        threadSafeCounter();
//        simpleLogger();

    }

}
