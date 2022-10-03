package org.apache.commons.net;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.DatagramSocket;

public interface DatagramSocketFactory
{
    DatagramSocket createDatagramSocket() throws SocketException;
    
    DatagramSocket createDatagramSocket(final int p0) throws SocketException;
    
    DatagramSocket createDatagramSocket(final int p0, final InetAddress p1) throws SocketException;
}
