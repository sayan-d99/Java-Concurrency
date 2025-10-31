package synchronization;

public class VisibilityIssueWithSynchronization {

    private int memVar1 = 10;
    private int memVar2 = 20;

    public void readV1(){
        System.out.println("memVar1 = " + memVar1);
        synchronized (this){
            memVar2 = 20;
            System.out.println("memVar2 = " + memVar2);
        }
    }

    public void readV2(){
        System.out.println("memVar2 = " + memVar2);
        synchronized (this){
            memVar2 = 20;
            System.out.println("memVar2 = " + memVar2);
        }
    }

    public void write(){

    }

}
