package java.rmi.server;

import java.io.IOException;
import java.net.Socket;

public interface RMIClientSocketFactory
{
    Socket createSocket(final String p0, final int p1) throws IOException;
}
