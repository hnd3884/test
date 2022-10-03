package sun.net;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceManager
{
    private static final int DEFAULT_MAX_SOCKETS = 25;
    private static final int maxSockets;
    private static final AtomicInteger numSockets;
    
    public static void beforeUdpCreate() throws SocketException {
        if (System.getSecurityManager() != null && ResourceManager.numSockets.incrementAndGet() > ResourceManager.maxSockets) {
            ResourceManager.numSockets.decrementAndGet();
            throw new SocketException("maximum number of DatagramSockets reached");
        }
    }
    
    public static void afterUdpClose() {
        if (System.getSecurityManager() != null) {
            ResourceManager.numSockets.decrementAndGet();
        }
    }
    
    static {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.net.maxDatagramSockets"));
        int int1 = 25;
        try {
            if (s != null) {
                int1 = Integer.parseInt(s);
            }
        }
        catch (final NumberFormatException ex) {}
        maxSockets = int1;
        numSockets = new AtomicInteger(0);
    }
}
