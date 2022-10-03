package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import java.nio.channels.SocketChannel;
import java.net.Socket;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.ior.IOR;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.net.ServerSocket;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.legacy.connection.ORBSocketFactory;

public class DefaultSocketFactory implements ORBSocketFactory
{
    private ORB orb;
    private static ORBUtilSystemException wrapper;
    
    public void setORB(final ORB orb) {
        this.orb = orb;
    }
    
    @Override
    public ServerSocket createServerSocket(final String s, final int n) throws IOException {
        if (!s.equals("IIOP_CLEAR_TEXT")) {
            throw DefaultSocketFactory.wrapper.defaultCreateServerSocketGivenNonIiopClearText(s);
        }
        ServerSocket socket;
        if (this.orb.getORBData().acceptorSocketType().equals("SocketChannel")) {
            socket = ServerSocketChannel.open().socket();
        }
        else {
            socket = new ServerSocket();
        }
        socket.bind(new InetSocketAddress(n));
        return socket;
    }
    
    @Override
    public SocketInfo getEndPointInfo(final org.omg.CORBA.ORB orb, final IOR ior, final SocketInfo socketInfo) {
        final IIOPAddress primaryAddress = ((IIOPProfileTemplate)ior.getProfile().getTaggedProfileTemplate()).getPrimaryAddress();
        return new EndPointInfoImpl("IIOP_CLEAR_TEXT", primaryAddress.getPort(), primaryAddress.getHost().toLowerCase());
    }
    
    @Override
    public Socket createSocket(final SocketInfo socketInfo) throws IOException, GetEndPointInfoAgainException {
        Socket socket;
        if (this.orb.getORBData().acceptorSocketType().equals("SocketChannel")) {
            socket = SocketChannel.open(new InetSocketAddress(socketInfo.getHost(), socketInfo.getPort())).socket();
        }
        else {
            socket = new Socket(socketInfo.getHost(), socketInfo.getPort());
        }
        try {
            socket.setTcpNoDelay(true);
        }
        catch (final Exception ex) {}
        return socket;
    }
    
    static {
        DefaultSocketFactory.wrapper = ORBUtilSystemException.get("rpc.transport");
    }
}
