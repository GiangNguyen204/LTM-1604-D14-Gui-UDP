package UDP;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Config {
    private Config() {}

    public static final int PORT = 5005;
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final int MAX_PACKET_SIZE = 64 * 1024;
}
