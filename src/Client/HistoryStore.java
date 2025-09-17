package Client;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class HistoryStore {
    private static final String FILE_NAME = ".ltm_udp_history.txt";
    private static final int MAX_ITEMS = 20;

    private HistoryStore() {}

    private static File getFile() {
        String home = System.getProperty("user.home", ".");
        return new File(home, FILE_NAME);
    }

    public static List<String> load() {
        File f = getFile();
        if (!f.exists()) return new ArrayList<>();
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) list.add(line);
            }
        } catch (IOException ignored) {}
        return list;
    }

    public static void save(List<String> items) {
        // giữ tối đa MAX_ITEMS và loại trùng (ưu tiên mới nhất)
        LinkedHashSet<String> set = new LinkedHashSet<>();
        ListIterator<String> it = items.listIterator(items.size());
        while (it.hasPrevious()) {
            String s = it.previous();
            if (set.add(s) && set.size() >= MAX_ITEMS) break;
        }
        List<String> out = new ArrayList<>(set);
        Collections.reverse(out);
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(getFile()), StandardCharsets.UTF_8))) {
            for (String s : out) {
                bw.write(s);
                bw.newLine();
            }
        } catch (IOException ignored) {}
    }
}
