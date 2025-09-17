package UDP;

public class SendMain {
    public static void main(String[] args) {
        String message = (args.length > 0)
                ? String.join(" ", args)
                : "Hello Broadcast | epoch=" + System.currentTimeMillis();

        Sender sender = new Sender();
        try {
            sender.send(message);
            System.out.println("Đã gửi broadcast xong.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
