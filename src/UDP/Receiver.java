package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Receiver {
    private volatile boolean running = true;

    public void start() throws IOException {
        try (DatagramSocket socket = new DatagramSocket(Config.PORT)) {
            socket.setReuseAddress(true);
            System.out.println("Receiver đang lắng nghe trên cổng " + Config.PORT);

            byte[] buf = new byte[Config.MAX_PACKET_SIZE];
            while (running) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                byte[] payload = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getOffset() + packet.getLength());
                String msg = new String(payload, Config.CHARSET);

                String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
                System.out.printf("[%s] Nhận từ %s:%d -> %s%n",
                        time,
                        packet.getAddress().getHostAddress(),
                        packet.getPort(),
                        msg);
            }
        }
    }

    public void stop() {
        running = false;
    }
}
