package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Receiver {
    private DatagramSocket socket;

    public void start() throws Exception {
        socket = new DatagramSocket(null);
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(Config.PORT));
        System.out.println("Receiver listening on port " + Config.PORT);
        byte[] buf = new byte[Config.MAX_PACKET_SIZE];
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        while (true) {
            DatagramPacket p = new DatagramPacket(buf, buf.length);
            socket.receive(p);
            String msg = new String(p.getData(), p.getOffset(), p.getLength(), Config.CHARSET);
            System.out.printf("[%s] from %s:%d -> %s%n",
                    LocalTime.now().format(fmt),
                    p.getAddress().getHostAddress(), p.getPort(), msg);
        }
    }
}