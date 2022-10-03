package com.me.devicemanagement.onpremise.server.sdp;

import javax.net.ssl.SSLSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;

public class ProtocolOverridingSSLSocketFactory extends SSLSocketFactory
{
    private final SSLSocketFactory underlyingSSLSocketFactory;
    private final String[] enabledProtocols;
    
    public ProtocolOverridingSSLSocketFactory(final SSLSocketFactory delegate, final String[] enabledProtocols) {
        this.underlyingSSLSocketFactory = delegate;
        this.enabledProtocols = enabledProtocols;
    }
    
    @Override
    public String[] getDefaultCipherSuites() {
        return this.underlyingSSLSocketFactory.getDefaultCipherSuites();
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return this.underlyingSSLSocketFactory.getSupportedCipherSuites();
    }
    
    @Override
    public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException {
        final Socket underlyingSocket = this.underlyingSSLSocketFactory.createSocket(socket, host, port, autoClose);
        return this.overrideProtocol(underlyingSocket);
    }
    
    @Override
    public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
        final Socket underlyingSocket = this.underlyingSSLSocketFactory.createSocket(host, port);
        return this.overrideProtocol(underlyingSocket);
    }
    
    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort) throws IOException, UnknownHostException {
        final Socket underlyingSocket = this.underlyingSSLSocketFactory.createSocket(host, port, localAddress, localPort);
        return this.overrideProtocol(underlyingSocket);
    }
    
    @Override
    public Socket createSocket(final InetAddress host, final int port) throws IOException {
        final Socket underlyingSocket = this.underlyingSSLSocketFactory.createSocket(host, port);
        return this.overrideProtocol(underlyingSocket);
    }
    
    @Override
    public Socket createSocket(final InetAddress host, final int port, final InetAddress localAddress, final int localPort) throws IOException {
        final Socket underlyingSocket = this.underlyingSSLSocketFactory.createSocket(host, port, localAddress, localPort);
        return this.overrideProtocol(underlyingSocket);
    }
    
    private Socket overrideProtocol(final Socket socket) {
        if (socket instanceof SSLSocket && this.enabledProtocols != null && this.enabledProtocols.length > 0) {
            ((SSLSocket)socket).setEnabledProtocols(this.enabledProtocols);
        }
        return socket;
    }
}
