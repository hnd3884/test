package sun.net.spi.nameservice;

import java.net.UnknownHostException;
import java.net.InetAddress;

public interface NameService
{
    InetAddress[] lookupAllHostAddr(final String p0) throws UnknownHostException;
    
    String getHostByAddr(final byte[] p0) throws UnknownHostException;
}
