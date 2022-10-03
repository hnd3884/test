package com.sun.corba.se.spi.transport;

import java.net.SocketException;
import com.sun.corba.se.pept.transport.Acceptor;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import com.sun.corba.se.spi.orb.ORB;

public interface ORBSocketFactory
{
    void setORB(final ORB p0);
    
    ServerSocket createServerSocket(final String p0, final InetSocketAddress p1) throws IOException;
    
    Socket createSocket(final String p0, final InetSocketAddress p1) throws IOException;
    
    void setAcceptedSocketOptions(final Acceptor p0, final ServerSocket p1, final Socket p2) throws SocketException;
}
