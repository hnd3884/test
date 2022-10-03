package com.me.mdm.onpremise.server.settings.proxy;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import java.net.InetAddress;
import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

public class ExtendedProtocolSocketFactory implements SecureProtocolSocketFactory
{
    private final SSLSocketFactory sslSocketFactory;
    
    public ExtendedProtocolSocketFactory(final SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }
    
    public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException {
        return this.sslSocketFactory.createSocket(socket, host, port, autoClose);
    }
    
    public Socket createSocket(final String host, final int port, final InetAddress clientHost, final int clientPort) throws IOException {
        return this.sslSocketFactory.createSocket(host, port, clientHost, clientPort);
    }
    
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort, final HttpConnectionParams params) throws IOException {
        return this.createSocket(host, port, localAddress, localPort);
    }
    
    public Socket createSocket(final String host, final int port) throws IOException {
        return this.sslSocketFactory.createSocket(host, port);
    }
}
