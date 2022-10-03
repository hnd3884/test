package org.apache.commons.net.ftp;

import javax.net.ssl.SSLServerSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.ServerSocket;
import javax.net.ssl.SSLContext;
import javax.net.ServerSocketFactory;

public class FTPSServerSocketFactory extends ServerSocketFactory
{
    private final SSLContext context;
    
    public FTPSServerSocketFactory(final SSLContext context) {
        this.context = context;
    }
    
    @Override
    public ServerSocket createServerSocket() throws IOException {
        return this.init(this.context.getServerSocketFactory().createServerSocket());
    }
    
    @Override
    public ServerSocket createServerSocket(final int port) throws IOException {
        return this.init(this.context.getServerSocketFactory().createServerSocket(port));
    }
    
    @Override
    public ServerSocket createServerSocket(final int port, final int backlog) throws IOException {
        return this.init(this.context.getServerSocketFactory().createServerSocket(port, backlog));
    }
    
    @Override
    public ServerSocket createServerSocket(final int port, final int backlog, final InetAddress ifAddress) throws IOException {
        return this.init(this.context.getServerSocketFactory().createServerSocket(port, backlog, ifAddress));
    }
    
    public ServerSocket init(final ServerSocket socket) {
        ((SSLServerSocket)socket).setUseClientMode(true);
        return socket;
    }
}
