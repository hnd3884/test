package javax.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.SocketException;
import java.net.Socket;

public abstract class SocketFactory
{
    private static SocketFactory theFactory;
    
    protected SocketFactory() {
    }
    
    public static SocketFactory getDefault() {
        synchronized (SocketFactory.class) {
            if (SocketFactory.theFactory == null) {
                SocketFactory.theFactory = new DefaultSocketFactory();
            }
        }
        return SocketFactory.theFactory;
    }
    
    public Socket createSocket() throws IOException {
        final UnsupportedOperationException ex = new UnsupportedOperationException();
        final SocketException ex2 = new SocketException("Unconnected sockets not implemented");
        ex2.initCause(ex);
        throw ex2;
    }
    
    public abstract Socket createSocket(final String p0, final int p1) throws IOException, UnknownHostException;
    
    public abstract Socket createSocket(final String p0, final int p1, final InetAddress p2, final int p3) throws IOException, UnknownHostException;
    
    public abstract Socket createSocket(final InetAddress p0, final int p1) throws IOException;
    
    public abstract Socket createSocket(final InetAddress p0, final int p1, final InetAddress p2, final int p3) throws IOException;
}
