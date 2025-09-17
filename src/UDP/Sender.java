package UDP;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class Sender {
    public void send(String message) throws IOException {
        byte[] data = message.getBytes(Config.CHARSET);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);

            List<InetAddress> broadcasts = NetUtils.getBroadcastAddresses();
            for (InetAddress bcast : broadcasts) {
                DatagramPacket packet = new DatagramPacket(data, data.length, bcast, Config.PORT);
                socket.send(packet);
                System.out.println("Đã gửi tới " + bcast.getHostAddress() + ":" + Config.PORT);
            }
        }
    }
}
