package UDP;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class NetUtils {
    private NetUtils() {}

    public static List<InetAddress> getBroadcastAddresses() throws SocketException {
        List<InetAddress> result = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (!ni.isUp() || ni.isLoopback() || ni.isPointToPoint()) continue;

            for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                InetAddress broadcast = ia.getBroadcast();
                if (broadcast != null) {
                    result.add(broadcast);
                }
            }
        }

        // thêm broadcast toàn cục
        try {
            result.add(InetAddress.getByName("255.255.255.255"));
        } catch (Exception ignored) {}

        return result;
    }
}
