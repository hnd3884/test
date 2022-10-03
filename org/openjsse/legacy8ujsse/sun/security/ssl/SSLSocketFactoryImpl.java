package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.net.InetAddress;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

public final class SSLSocketFactoryImpl extends SSLSocketFactory
{
    private SSLContextImpl context;
    
    public SSLSocketFactoryImpl() throws Exception {
        this.context = SSLContextImpl.DefaultSSLContext.getDefaultImpl();
    }
    
    SSLSocketFactoryImpl(final SSLContextImpl context) {
        this.context = context;
    }
    
    @Override
    public Socket createSocket() {
        return new SSLSocketImpl(this.context);
    }
    
    @Override
    public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
        return new SSLSocketImpl(this.context, host, port);
    }
    
    @Override
    public Socket createSocket(final Socket s, final String host, final int port, final boolean autoClose) throws IOException {
        return new SSLSocketImpl(this.context, s, host, port, autoClose);
    }
    
    @Override
    public Socket createSocket(final Socket s, final InputStream consumed, final boolean autoClose) throws IOException {
        if (s == null) {
            throw new NullPointerException("the existing socket cannot be null");
        }
        return new SSLSocketImpl(this.context, s, consumed, autoClose);
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port) throws IOException {
        return new SSLSocketImpl(this.context, address, port);
    }
    
    @Override
    public Socket createSocket(final String host, final int port, final InetAddress clientAddress, final int clientPort) throws IOException {
        return new SSLSocketImpl(this.context, host, port, clientAddress, clientPort);
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port, final InetAddress clientAddress, final int clientPort) throws IOException {
        return new SSLSocketImpl(this.context, address, port, clientAddress, clientPort);
    }
    
    @Override
    public String[] getDefaultCipherSuites() {
        return this.context.getDefaultCipherSuiteList(false).toStringArray();
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return this.context.getSupportedCipherSuiteList().toStringArray();
    }
}
