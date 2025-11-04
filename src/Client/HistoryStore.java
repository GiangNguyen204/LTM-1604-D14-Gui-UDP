package Client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HistoryStore (updated)
 * - Persist & load send-history with epoch + message pairs (tab-separated).
 * - Backward compatible with old plain-line format (message only).
 * - Keep only the last MAX_ITEMS items.
 */
public class HistoryStore {

    private static final String FILE_NAME = ".ltm_udp_history.txt";
    private static final int MAX_ITEMS = 20;

    private static File file() {
        String home = System.getProperty("user.home", ".");
        return new File(home, FILE_NAME);
    }

    /** Backward-compatible: load just messages (ignores epoch if present). */
    public static List<String> load() {
        List<String> out = new ArrayList<>();
        File f = file();
        if (!f.exists()) return out;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                int tab = line.indexOf('\t');
                if (tab > 0) {
                    out.add(line.substring(tab + 1)); // message after tab
                } else {
                    out.add(line);
                }
            }
        } catch (IOException ignored) {}
        return out;
    }

    /** Backward-compatible: save just messages, one per line (legacy). */
    public static void save(List<String> items) {
        // keep last MAX_ITEMS
        List<String> data = items == null ? Collections.emptyList() : items;
        int from = Math.max(0, data.size() - MAX_ITEMS);
        List<String> tail = data.subList(from, data.size());

        File f = file();
        if (f.getParentFile() != null) {
            // for safety; though typically file is in user.home, not nested
            f.getParentFile().mkdirs();
        }
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            for (String msg : tail) {
                if (msg == null) msg = "";
                // strip newlines to keep one record per line
                msg = msg.replace("\r", " ").replace("\n", " ");
                bw.write(msg);
                bw.newLine();
            }
        } catch (IOException ignored) {}
    }

    /** New format: load (epochMillis, message) pairs. */
    public static List<String[]> loadPairs() {
        List<String[]> out = new ArrayList<>();
        File f = file();
        if (!f.exists()) return out;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                int tab = line.indexOf('\t');
                if (tab > 0) {
                    String epoch = line.substring(0, tab);
                    String msg = line.substring(tab + 1);
                    out.add(new String[]{ epoch, msg });
                } else {
                    // legacy line: no epoch; store empty epoch
                    out.add(new String[]{ "", line });
                }
            }
        } catch (IOException ignored) {}
        return out;
    }

    /** New format: save (epochMillis, message) pairs (tab-separated). */
    public static void savePairs(List<String[]> items) {
        List<String[]> data = items == null ? Collections.emptyList() : items;
        int from = Math.max(0, data.size() - MAX_ITEMS);
        List<String[]> tail = data.subList(from, data.size());

        File f = file();
        if (f.getParentFile() != null) {
            f.getParentFile().mkdirs();
        }
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            for (String[] p : tail) {
                String epoch = p != null && p.length > 0 && p[0] != null ? p[0] : "";
                String msg   = p != null && p.length > 1 && p[1] != null ? p[1] : "";
                // strip newlines in message
                msg = msg.replace("\r", " ").replace("\n", " ");
                bw.write(epoch + "\t" + msg);
                bw.newLine();
            }
        } catch (IOException ignored) {}
    }
}
