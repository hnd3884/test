package org.apache.commons.httpclient.protocol;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;

public interface ProtocolSocketFactory
{
    Socket createSocket(final String p0, final int p1, final InetAddress p2, final int p3) throws IOException, UnknownHostException;
    
    Socket createSocket(final String p0, final int p1, final InetAddress p2, final int p3, final HttpConnectionParams p4) throws IOException, UnknownHostException, ConnectTimeoutException;
    
    Socket createSocket(final String p0, final int p1) throws IOException, UnknownHostException;
}
