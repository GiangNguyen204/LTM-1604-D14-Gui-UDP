package UDP;

public class ReceiveMain {
    public static void main(String[] args) {
        Receiver receiver = new Receiver();
        try {
            receiver.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}