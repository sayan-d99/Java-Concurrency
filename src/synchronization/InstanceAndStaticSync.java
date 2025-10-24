package synchronization;

import java.time.LocalDate;
import java.time.LocalDateTime;

class Resource {

    public static synchronized void staticSyncMethod() throws InterruptedException{
        System.out.println(LocalDateTime.now() + " " + Thread.currentThread().getName() + " inside staticSyncMethod");
        Thread.sleep(2000);
        System.out.println(LocalDateTime.now() + " " + Thread.currentThread().getName() + "Exiting staticSyncMethod");
    }

    public synchronized void instanceSyncMethod() throws InterruptedException{
        System.out.println(LocalDateTime.now() + " " + Thread.currentThread().getName() + " inside instanceSyncMethod");
        Thread.sleep(2000);
        System.out.println(LocalDateTime.now() + " " + Thread.currentThread().getName() + "Exiting instanceSyncMethod");
    }

}

public class InstanceAndStaticSync {

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> {
            try {
                Resource.staticSyncMethod();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                new Resource().instanceSyncMethod();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread1.start();
        thread2.start();
    }

}
