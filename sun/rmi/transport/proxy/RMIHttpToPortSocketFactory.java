package sun.rmi.transport.proxy;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.URL;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

public class RMIHttpToPortSocketFactory extends RMISocketFactory
{
    @Override
    public Socket createSocket(final String s, final int n) throws IOException {
        return new HttpSendSocket(s, n, new URL("http", s, n, "/"));
    }
    
    @Override
    public ServerSocket createServerSocket(final int n) throws IOException {
        return new HttpAwareServerSocket(n);
    }
}
