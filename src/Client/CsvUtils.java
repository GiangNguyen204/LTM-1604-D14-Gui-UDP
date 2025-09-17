package Client;

import UDP.MessageEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class CsvUtils {
    private CsvUtils() {}

    public static void export(File file, List<MessageEvent> rows) throws Exception {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write("time,remote_ip,remote_port,message");
            bw.newLine();
            for (MessageEvent e : rows) {
                String safeMsg = e.message.replace("\"", "\"\"");
                bw.write(String.format("\"%s\",%s,%d,\"%s\"",
                        e.timeText, e.remoteIp, e.remotePort, safeMsg));
                bw.newLine();
            }
        }
    }
}
