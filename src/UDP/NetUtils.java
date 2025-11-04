package UDP;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetUtils {
    public static List<InetAddress> listAllBroadcastAddresses() throws SocketException, UnknownHostException {
        List<InetAddress> result = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (!ni.isUp() || ni.isLoopback() || ni.isPointToPoint()) continue;
            for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                InetAddress b = ia.getBroadcast();
                if (b != null) result.add(b);
            }
        }
        // thêm global broadcast
        result.add(InetAddress.getByName("255.255.255.255"));
        return result;
    }
}
