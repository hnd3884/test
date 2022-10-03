package com.unboundid.util;

import java.net.InetAddress;
import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SynchronizedSSLSocketFactory extends SSLSocketFactory
{
    private final SSLSocketFactory factory;
    
    public SynchronizedSSLSocketFactory(final SSLSocketFactory factory) {
        this.factory = factory;
    }
    
    public SSLSocketFactory getWrappedSocketFactory() {
        return this.factory;
    }
    
    @Override
    public Socket createSocket(final String host, final int port) throws IOException {
        synchronized (this.factory) {
            return this.factory.createSocket(host, port);
        }
    }
    
    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort) throws IOException {
        synchronized (this.factory) {
            return this.factory.createSocket(host, port, localAddress, localPort);
        }
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port) throws IOException {
        synchronized (this.factory) {
            return this.factory.createSocket(address, port);
        }
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddress, final int localPort) throws IOException {
        synchronized (this.factory) {
            return this.factory.createSocket(address, port, localAddress, localPort);
        }
    }
    
    @Override
    public Socket createSocket(final Socket s, final String host, final int port, final boolean autoClose) throws IOException {
        synchronized (this.factory) {
            return this.factory.createSocket(s, host, port, autoClose);
        }
    }
    
    @Override
    public String[] getDefaultCipherSuites() {
        synchronized (this.factory) {
            return this.factory.getDefaultCipherSuites();
        }
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        synchronized (this.factory) {
            return this.factory.getSupportedCipherSuites();
        }
    }
}
