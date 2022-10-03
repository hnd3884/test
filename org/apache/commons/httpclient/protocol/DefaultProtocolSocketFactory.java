package org.apache.commons.httpclient.protocol;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;

public class DefaultProtocolSocketFactory implements ProtocolSocketFactory
{
    private static final DefaultProtocolSocketFactory factory;
    
    static DefaultProtocolSocketFactory getSocketFactory() {
        return DefaultProtocolSocketFactory.factory;
    }
    
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort) throws IOException, UnknownHostException {
        return new Socket(host, port, localAddress, localPort);
    }
    
    public Socket createSocket(final String host, final int port, final InetAddress localAddress, final int localPort, final HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        final int timeout = params.getConnectionTimeout();
        if (timeout == 0) {
            return this.createSocket(host, port, localAddress, localPort);
        }
        Socket socket = ReflectionSocketFactory.createSocket("javax.net.SocketFactory", host, port, localAddress, localPort, timeout);
        if (socket == null) {
            socket = ControllerThreadSocketFactory.createSocket(this, host, port, localAddress, localPort, timeout);
        }
        return socket;
    }
    
    public Socket createSocket(final String host, final int port) throws IOException, UnknownHostException {
        return new Socket(host, port);
    }
    
    public boolean equals(final Object obj) {
        return obj != null && obj.getClass().equals(this.getClass());
    }
    
    public int hashCode() {
        return this.getClass().hashCode();
    }
    
    static {
        factory = new DefaultProtocolSocketFactory();
    }
}
