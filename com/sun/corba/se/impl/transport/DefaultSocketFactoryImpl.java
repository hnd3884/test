package com.sun.corba.se.impl.transport;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.SocketException;
import com.sun.corba.se.pept.transport.Acceptor;
import java.nio.channels.SocketChannel;
import java.net.Socket;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.ORBSocketFactory;

public class DefaultSocketFactoryImpl implements ORBSocketFactory
{
    private ORB orb;
    private static final boolean keepAlive;
    
    @Override
    public void setORB(final ORB orb) {
        this.orb = orb;
    }
    
    @Override
    public ServerSocket createServerSocket(final String s, final InetSocketAddress inetSocketAddress) throws IOException {
        ServerSocket socket;
        if (this.orb.getORBData().acceptorSocketType().equals("SocketChannel")) {
            socket = ServerSocketChannel.open().socket();
        }
        else {
            socket = new ServerSocket();
        }
        socket.bind(inetSocketAddress);
        return socket;
    }
    
    @Override
    public Socket createSocket(final String s, final InetSocketAddress inetSocketAddress) throws IOException {
        Socket socket;
        if (this.orb.getORBData().connectionSocketType().equals("SocketChannel")) {
            socket = SocketChannel.open(inetSocketAddress).socket();
        }
        else {
            socket = new Socket(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        }
        socket.setTcpNoDelay(true);
        if (DefaultSocketFactoryImpl.keepAlive) {
            socket.setKeepAlive(true);
        }
        return socket;
    }
    
    @Override
    public void setAcceptedSocketOptions(final Acceptor acceptor, final ServerSocket serverSocket, final Socket socket) throws SocketException {
        socket.setTcpNoDelay(true);
        if (DefaultSocketFactoryImpl.keepAlive) {
            socket.setKeepAlive(true);
        }
    }
    
    static {
        keepAlive = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String property = System.getProperty("com.sun.CORBA.transport.enableTcpKeepAlive");
                if (property != null) {
                    return new Boolean(!"false".equalsIgnoreCase(property));
                }
                return Boolean.FALSE;
            }
        });
    }
}
