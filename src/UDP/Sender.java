package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

public class Sender {
    public void send(String message) throws Exception {
        byte[] data = message.getBytes(Config.CHARSET);
        List<InetAddress> broadcasts = NetUtils.listAllBroadcastAddresses();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            for (InetAddress bcast : broadcasts) {
                DatagramPacket p = new DatagramPacket(data, data.length, bcast, Config.PORT);
                socket.send(p);
            }
        }
    }
}
