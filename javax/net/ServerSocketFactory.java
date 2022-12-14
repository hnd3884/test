package javax.net;

import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketException;
import java.net.ServerSocket;

public abstract class ServerSocketFactory
{
    private static ServerSocketFactory theFactory;
    
    protected ServerSocketFactory() {
    }
    
    public static ServerSocketFactory getDefault() {
        synchronized (ServerSocketFactory.class) {
            if (ServerSocketFactory.theFactory == null) {
                ServerSocketFactory.theFactory = new DefaultServerSocketFactory();
            }
        }
        return ServerSocketFactory.theFactory;
    }
    
    public ServerSocket createServerSocket() throws IOException {
        throw new SocketException("Unbound server sockets not implemented");
    }
    
    public abstract ServerSocket createServerSocket(final int p0) throws IOException;
    
    public abstract ServerSocket createServerSocket(final int p0, final int p1) throws IOException;
    
    public abstract ServerSocket createServerSocket(final int p0, final int p1, final InetAddress p2) throws IOException;
}
