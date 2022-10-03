package java.net;

import java.io.IOException;

interface InetAddressImpl
{
    String getLocalHostName() throws UnknownHostException;
    
    InetAddress[] lookupAllHostAddr(final String p0) throws UnknownHostException;
    
    String getHostByAddr(final byte[] p0) throws UnknownHostException;
    
    InetAddress anyLocalAddress();
    
    InetAddress loopbackAddress();
    
    boolean isReachable(final InetAddress p0, final int p1, final NetworkInterface p2, final int p3) throws IOException;
}
