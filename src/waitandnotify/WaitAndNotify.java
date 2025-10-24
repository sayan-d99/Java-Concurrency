package waitandnotify;

public class WaitAndNotify {
    public static void main(String[] args) {
        MyBuffer mb = new MyBuffer(5);
        for(int i = 1; i <= 3; i++){
            new Thread(new BufferProducer(mb), "Thread-Buffer-Producer-"+i).start();
        }
        for(int i = 1; i <= 3; i++){
            new Thread(new BufferConsumer(mb), "Thread-Buffer-Consumer-"+i).start();
        }
    }
}
