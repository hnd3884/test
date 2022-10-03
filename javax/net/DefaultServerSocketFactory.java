package javax.net;

import java.net.InetAddress;
import java.io.IOException;
import java.net.ServerSocket;

class DefaultServerSocketFactory extends ServerSocketFactory
{
    @Override
    public ServerSocket createServerSocket() throws IOException {
        return new ServerSocket();
    }
    
    @Override
    public ServerSocket createServerSocket(final int n) throws IOException {
        return new ServerSocket(n);
    }
    
    @Override
    public ServerSocket createServerSocket(final int n, final int n2) throws IOException {
        return new ServerSocket(n, n2);
    }
    
    @Override
    public ServerSocket createServerSocket(final int n, final int n2, final InetAddress inetAddress) throws IOException {
        return new ServerSocket(n, n2, inetAddress);
    }
}
