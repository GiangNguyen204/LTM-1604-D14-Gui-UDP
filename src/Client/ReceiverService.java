package Client;

import UDP.Config;
import UDP.MessageEvent;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReceiverService implements Runnable {

    public interface Listener {
        void onMessage(MessageEvent event);
        void onStatus(String text);
        void onError(String text, Exception ex);
    }

    private final Listener listener;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread worker;

    public ReceiverService(Listener listener) {
        this.listener = listener;
    }

    public synchronized void start() {
        if (running.get()) return;
        running.set(true);
        worker = new Thread(this, "UDP-Receiver-Thread");
        worker.setDaemon(true);
        worker.start();
    }

    public synchronized void stop() {
        running.set(false);
        // có thể tạo một “tickle” socket gửi 1 byte để un-block receive nếu cần
    }

    @Override
    public void run() {
        listener.onStatus("Starting receiver on port " + Config.PORT + " ...");
        try (DatagramSocket socket = new DatagramSocket(null)) {
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(Config.PORT));
            listener.onStatus("Listening UDP broadcast on port " + Config.PORT);

            byte[] buf = new byte[Config.MAX_PACKET_SIZE];
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

            while (running.get()) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                byte[] payload = Arrays.copyOfRange(packet.getData(), packet.getOffset(),
                        packet.getOffset() + packet.getLength());
                String msg = new String(payload, Config.CHARSET);

                String timeText = LocalDateTime.now().format(fmt);
                MessageEvent ev = new MessageEvent(
                        System.nanoTime(), timeText,
                        packet.getAddress().getHostAddress(), packet.getPort(), msg
                );
                listener.onMessage(ev);
            }
        } catch (Exception ex) {
            if (running.get()) {
                listener.onError("Receiver stopped with error", ex);
            } else {
                listener.onStatus("Receiver stopped.");
            }
        } finally {
            running.set(false);
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
