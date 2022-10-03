package org.apache.commons.httpclient.protocol;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import java.net.UnknownHostException;
import java.io.IOException;
import javax.net.ssl.SSLSocketFactory;
import java.net.Socket;
import java.net.InetAddress;

public class SSLProtocolSocketFactory implements SecureProtocolSocketFactory
{
    private static final SSLProtocolSocketFactory factory;
    
    static SSLProtocolSocketFactory getSocketFactory() {
        return SSLProtocolSocketFactory.factory;
    }
    
    public Socket createSocket(final String host, final int port, final InetAddress clientHost, final int clientPort) throws IOException, UnknownHostException {
        return SSLSocketFactory.getDefault().createSocket(host, port, clientHost, clientPort);
    }
    
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort, final HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        final int timeout = params.getConnectionTimeout();
        if (timeout == 0) {
            return this.createSocket(host, port, localAddress, localPort);
        }
        Socket socket = ReflectionSocketFactory.createSocket("javax.net.ssl.SSLSocketFactory", host, port, localAddress, localPort, timeout);
        if (socket == null) {
            socket = ControllerThreadSocketFactory.createSocket(this, host, port, localAddress, localPort, timeout);
        }
        return socket;
    }
    
    public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
        return SSLSocketFactory.getDefault().createSocket(host, port);
    }
    
    public Socket createSocket(final Socket socket, final String host, final int port, final boolean autoClose) throws IOException, UnknownHostException {
        return ((SSLSocketFactory)SSLSocketFactory.getDefault()).createSocket(socket, host, port, autoClose);
    }
    
    public boolean equals(final Object obj) {
        return obj != null && obj.getClass().equals(this.getClass());
    }
    
    public int hashCode() {
        return this.getClass().hashCode();
    }
    
    static {
        factory = new SSLProtocolSocketFactory();
    }
}
