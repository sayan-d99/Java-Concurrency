package locks.easy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {

    volatile int money;
    List<String> operations = new ArrayList<>();
    ReentrantLock lock = new ReentrantLock();

    public BankAccount(int money){
        this.money = money;
    }

    public void debit(int amount){
        boolean success = false;
        try{
            lock.lock();
            if(amount > money) throw new IllegalStateException("Account does not have enough money. Current Balance: " + money);
            if(amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
            this.money -= amount;
            success = true;
            System.out.printf("%s debited account by %d. Remaining balance: %d\n",
                    Thread.currentThread().getName(), amount, money);
        }finally {
            if(success) operations.add("DEBIT " + amount);
            lock.unlock();
        }
    }

    public void credit(int amount){
        boolean success = false;
        try{
            lock.lock();
            if(amount < 0) {
                throw new IllegalArgumentException("Amount cannot be negative");
            }
            System.out.printf("%s Current Balance: %d. Crediting amount: %s to bank account\n",
                    Thread.currentThread().getName(), money, amount);
            this.money += amount;
            success = true;
            System.out.printf("%s account credited by %d. Remaining balance: %d\n",
                    Thread.currentThread().getName(), amount, money);
        }finally{
            if(success) operations.add("CREDIT " + amount);
            lock.unlock();
        }
    }

    public int getMoney(){
        try{
            lock.lock();
            return this.money;
        }finally{
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BankAccount acc = new BankAccount(100);
        Random r = new Random();
        Runnable debitor = () -> {
            int debitAmount = 0;
            for(int i=1; i<=5;i++){
                try{
                    debitAmount = r.nextInt(100,500);
                    acc.debit(debitAmount);
                    Thread.sleep(1000);
                }catch(IllegalStateException | IllegalArgumentException e){
                    System.out.printf("%s failed to debit amount %d due to reason: %s\n",
                            Thread.currentThread().getName(), debitAmount, e.getMessage());
                }catch(InterruptedException ignored) {
                }
            }
        };

        Runnable creditor = () -> {
            for(int i=1; i<=5;i++){
                try{
                    int creditAmount = r.nextInt(200,600);
                    acc.credit(creditAmount);
                    Thread.sleep(1000);
                }catch(IllegalStateException | IllegalArgumentException e){
                    System.out.printf("%s failed to credit amount due to reason: %s\n",
                            Thread.currentThread().getName(), e.getMessage());
                }catch(InterruptedException ignored) {
                }
            }
        };

        Thread t1 = new Thread(creditor, "Creditor-1");
        Thread t2 = new Thread(debitor, "Debitor-1");
        Thread t3 = new Thread(debitor, "Debitor-2");

        t1.start(); t2.start(); t3.start();
        t1.join(); t2.join(); t3.join();
    }

}
