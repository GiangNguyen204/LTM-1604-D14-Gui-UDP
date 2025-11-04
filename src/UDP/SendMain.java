package UDP;

public class SendMain {
    public static void main(String[] args) throws Exception {
        new Sender().send("Hello Broadcast (console)");
        System.out.println("Sent.");
    }
}
