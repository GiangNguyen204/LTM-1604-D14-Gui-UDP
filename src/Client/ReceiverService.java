package Client;

import UDP.Config;
import UDP.MessageEvent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ReceiverService (updated)
 * - Đóng dấu thời gian đầy đủ: yyyy-MM-dd HH:mm:ss.SSS
 * - Dùng hằng cục bộ BUF_SIZE thay vì Config.BUFFER_SIZE để tránh lỗi compile.
 */
public class ReceiverService implements Runnable {

    public interface Listener {
        void onMessage(MessageEvent event);
        void onStatus(String text);
        void onError(String text, Exception ex);
    }

    private static final int BUF_SIZE = 8192; // <--- kích thước buffer cục bộ

    private final Listener listener;
    private Thread thread;
    private final AtomicBoolean running = new AtomicBoolean(false);

    // Định dạng NGÀY + GIỜ (milli)
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public ReceiverService(Listener listener) {
        this.listener = listener;
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            thread = new Thread(this, "udp-receiver");
            thread.setDaemon(true);
            thread.start();
            if (listener != null) listener.onStatus("Starting...");
        }
    }

    public void stop() {
        running.set(false);
        if (thread != null) {
            try { thread.interrupt(); } catch (Exception ignore) {}
            thread = null;
        }
        if (listener != null) listener.onStatus("Stopped.");
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(new InetSocketAddress(Config.PORT))) {
            socket.setReuseAddress(true);
            if (listener != null) listener.onStatus("Listening UDP broadcast on port " + Config.PORT);

            byte[] buf = new byte[BUF_SIZE];                       // <--- dùng hằng cục bộ
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            while (running.get()) {
                socket.receive(packet);

                String msg = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                String ip  = packet.getAddress().getHostAddress();
                int port   = packet.getPort();

                String timeText = TIME_FMT.format(Instant.now().atZone(ZoneId.systemDefault()));

                if (listener != null) {
                    listener.onMessage(new MessageEvent(timeText, ip, port, msg));
                }
            }
        } catch (IOException ex) {
            if (running.get()) {
                if (listener != null) listener.onError("Receive error", ex);
            } else {
                if (listener != null) listener.onStatus("Stopped.");
            }
        } catch (Exception ex) {
            if (listener != null) listener.onError("Unexpected error", ex);
        } finally {
            running.set(false);
        }
    }
}
