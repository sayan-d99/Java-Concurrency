package synchronization;

public class VisibilityAndInstructionReordering {

    public static void main(String[] args) throws InterruptedException {
        Resource1 r = new Resource1();
        Thread threadOne = new Thread(() -> {
            r.write();
        }, "Thread-Write");

        Thread threadTwo = new Thread(() -> {
            r.read();
        }, "Thread-Read");

        threadOne.start();
        threadTwo.start();

    }

}

class Resource1{
    private int data = 0;
    private boolean hasData = false;

    public void write(){
        data = 50;
        hasData = true;
    }

    public void read() {
        if(hasData){
            System.out.println("data = " + data);
            // do more operations on 'data'
        }
    }

}
