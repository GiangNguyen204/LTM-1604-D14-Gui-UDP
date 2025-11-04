package UDP;

public class MessageEvent {
    public final String timeText;
    public final String remoteIp;
    public final int remotePort;
    public final String message;

    public MessageEvent(String timeText, String remoteIp, int remotePort, String message) {
        this.timeText = timeText;
        this.remoteIp = remoteIp;
        this.remotePort = remotePort;
        this.message = message;
    }
}
