package org.apache.commons.net;

import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.net.Socket;
import java.net.Proxy;
import javax.net.SocketFactory;

public class DefaultSocketFactory extends SocketFactory
{
    private final Proxy connProxy;
    
    public DefaultSocketFactory() {
        this(null);
    }
    
    public DefaultSocketFactory(final Proxy proxy) {
        this.connProxy = proxy;
    }
    
    @Override
    public Socket createSocket() throws IOException {
        if (this.connProxy != null) {
            return new Socket(this.connProxy);
        }
        return new Socket();
    }
    
    @Override
    public Socket createSocket(final String host, final int port) throws UnknownHostException, IOException {
        if (this.connProxy != null) {
            final Socket s = new Socket(this.connProxy);
            s.connect(new InetSocketAddress(host, port));
            return s;
        }
        return new Socket(host, port);
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port) throws IOException {
        if (this.connProxy != null) {
            final Socket s = new Socket(this.connProxy);
            s.connect(new InetSocketAddress(address, port));
            return s;
        }
        return new Socket(address, port);
    }
    
    @Override
    public Socket createSocket(final String host, final int port, final InetAddress localAddr, final int localPort) throws UnknownHostException, IOException {
        if (this.connProxy != null) {
            final Socket s = new Socket(this.connProxy);
            s.bind(new InetSocketAddress(localAddr, localPort));
            s.connect(new InetSocketAddress(host, port));
            return s;
        }
        return new Socket(host, port, localAddr, localPort);
    }
    
    @Override
    public Socket createSocket(final InetAddress address, final int port, final InetAddress localAddr, final int localPort) throws IOException {
        if (this.connProxy != null) {
            final Socket s = new Socket(this.connProxy);
            s.bind(new InetSocketAddress(localAddr, localPort));
            s.connect(new InetSocketAddress(address, port));
            return s;
        }
        return new Socket(address, port, localAddr, localPort);
    }
    
    public ServerSocket createServerSocket(final int port) throws IOException {
        return new ServerSocket(port);
    }
    
    public ServerSocket createServerSocket(final int port, final int backlog) throws IOException {
        return new ServerSocket(port, backlog);
    }
    
    public ServerSocket createServerSocket(final int port, final int backlog, final InetAddress bindAddr) throws IOException {
        return new ServerSocket(port, backlog, bindAddr);
    }
}
