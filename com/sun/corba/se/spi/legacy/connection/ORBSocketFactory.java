package com.sun.corba.se.spi.legacy.connection;

import java.net.Socket;
import com.sun.corba.se.spi.transport.SocketInfo;
import com.sun.corba.se.spi.ior.IOR;
import org.omg.CORBA.ORB;
import java.io.IOException;
import java.net.ServerSocket;

public interface ORBSocketFactory
{
    public static final String IIOP_CLEAR_TEXT = "IIOP_CLEAR_TEXT";
    
    ServerSocket createServerSocket(final String p0, final int p1) throws IOException;
    
    SocketInfo getEndPointInfo(final ORB p0, final IOR p1, final SocketInfo p2);
    
    Socket createSocket(final SocketInfo p0) throws IOException, GetEndPointInfoAgainException;
}
