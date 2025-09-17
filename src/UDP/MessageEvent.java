package UDP;

public class MessageEvent {
    public final long timeNanos;
    public final String timeText;
    public final String remoteIp;
    public final int remotePort;
    public final String message;

    public MessageEvent(long timeNanos, String timeText, String remoteIp, int remotePort, String message) {
        this.timeNanos = timeNanos;
        this.timeText = timeText;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.message = message;
    }
}
