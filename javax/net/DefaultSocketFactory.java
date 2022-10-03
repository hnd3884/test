package javax.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.Socket;

class DefaultSocketFactory extends SocketFactory
{
    @Override
    public Socket createSocket() {
        return new Socket();
    }
    
    @Override
    public Socket createSocket(final String s, final int n) throws IOException, UnknownHostException {
        return new Socket(s, n);
    }
    
    @Override
    public Socket createSocket(final InetAddress inetAddress, final int n) throws IOException {
        return new Socket(inetAddress, n);
    }
    
    @Override
    public Socket createSocket(final String s, final int n, final InetAddress inetAddress, final int n2) throws IOException, UnknownHostException {
        return new Socket(s, n, inetAddress, n2);
    }
    
    @Override
    public Socket createSocket(final InetAddress inetAddress, final int n, final InetAddress inetAddress2, final int n2) throws IOException {
        return new Socket(inetAddress, n, inetAddress2, n2);
    }
}
