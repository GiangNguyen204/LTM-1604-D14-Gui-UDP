package Client;

import UDP.MessageEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvUtils {
    public static void export(File file, List<MessageEvent> rows) throws IOException {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("time,remote_ip,remote_port,message");
            for (MessageEvent e : rows) {
                String safeMsg = e.message.replace("\"", "\"\"");
                pw.printf("%s,%s,%d,\"%s\"%n", e.timeText, e.remoteIp, e.remotePort, safeMsg);
            }
        }
    }
}
