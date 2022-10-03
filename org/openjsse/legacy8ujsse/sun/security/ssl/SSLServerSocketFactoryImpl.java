package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.net.InetAddress;
import java.io.IOException;
import java.net.ServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public final class SSLServerSocketFactoryImpl extends SSLServerSocketFactory
{
    private static final int DEFAULT_BACKLOG = 50;
    private SSLContextImpl context;
    
    public SSLServerSocketFactoryImpl() throws Exception {
        this.context = SSLContextImpl.DefaultSSLContext.getDefaultImpl();
    }
    
    SSLServerSocketFactoryImpl(final SSLContextImpl context) {
        this.context = context;
    }
    
    @Override
    public ServerSocket createServerSocket() throws IOException {
        return new SSLServerSocketImpl(this.context);
    }
    
    @Override
    public ServerSocket createServerSocket(final int port) throws IOException {
        return new SSLServerSocketImpl(port, 50, this.context);
    }
    
    @Override
    public ServerSocket createServerSocket(final int port, final int backlog) throws IOException {
        return new SSLServerSocketImpl(port, backlog, this.context);
    }
    
    @Override
    public ServerSocket createServerSocket(final int port, final int backlog, final InetAddress ifAddress) throws IOException {
        return new SSLServerSocketImpl(port, backlog, ifAddress, this.context);
    }
    
    @Override
    public String[] getDefaultCipherSuites() {
        return this.context.getDefaultCipherSuiteList(true).toStringArray();
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return this.context.getSupportedCipherSuiteList().toStringArray();
    }
}
