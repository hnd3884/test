package org.apache.commons.net.ftp;

import javax.net.ssl.SSLServerSocket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLContext;
import javax.net.SocketFactory;

public class FTPSSocketFactory extends SocketFactory
{
    private final SSLContext context;
    
    public FTPSSocketFactory(final SSLContext context) {
        this.context = context;
    }
    
    @Override
    public Socket createSocket() throws IOException {
        return this.context.getSocketFactory().createSocket();
    }
    
    @Override
    public Socket createSocket(final String address, final int port) throws UnknownHostException, IOException {
        return this.context.getSocketFactory().createSocket(address, port);
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port) throws IOException {
        return this.context.getSocketFactory().createSocket(address, port);
    }
    
    @Override
    public Socket createSocket(final String address, final int port, final InetAddress localAddress, final int localPort) throws UnknownHostException, IOException {
        return this.context.getSocketFactory().createSocket(address, port, localAddress, localPort);
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddress, final int localPort) throws IOException {
        return this.context.getSocketFactory().createSocket(address, port, localAddress, localPort);
    }
    
    @Deprecated
    public ServerSocket createServerSocket(final int port) throws IOException {
        return this.init(this.context.getServerSocketFactory().createServerSocket(port));
    }
    
    @Deprecated
    public ServerSocket createServerSocket(final int port, final int backlog) throws IOException {
        return this.init(this.context.getServerSocketFactory().createServerSocket(port, backlog));
    }
    
    @Deprecated
    public ServerSocket createServerSocket(final int port, final int backlog, final InetAddress ifAddress) throws IOException {
        return this.init(this.context.getServerSocketFactory().createServerSocket(port, backlog, ifAddress));
    }
    
    @Deprecated
    public ServerSocket init(final ServerSocket socket) throws IOException {
        ((SSLServerSocket)socket).setUseClientMode(true);
        return socket;
    }
}
