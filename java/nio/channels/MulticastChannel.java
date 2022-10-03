package java.nio.channels;

import java.net.NetworkInterface;
import java.net.InetAddress;
import java.io.IOException;

public interface MulticastChannel extends NetworkChannel
{
    void close() throws IOException;
    
    MembershipKey join(final InetAddress p0, final NetworkInterface p1) throws IOException;
    
    MembershipKey join(final InetAddress p0, final NetworkInterface p1, final InetAddress p2) throws IOException;
}
