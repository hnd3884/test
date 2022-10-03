package java.rmi.server;

import java.io.IOException;
import java.net.ServerSocket;

public interface RMIServerSocketFactory
{
    ServerSocket createServerSocket(final int p0) throws IOException;
}
