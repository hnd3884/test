package sun.net;

import java.io.IOException;
import java.net.InetAddress;
import java.io.FileDescriptor;

public final class NetHooks
{
    public static void beforeTcpBind(final FileDescriptor fileDescriptor, final InetAddress inetAddress, final int n) throws IOException {
    }
    
    public static void beforeTcpConnect(final FileDescriptor fileDescriptor, final InetAddress inetAddress, final int n) throws IOException {
    }
}
