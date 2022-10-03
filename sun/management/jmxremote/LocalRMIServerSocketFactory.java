package sun.management.jmxremote;

import java.util.Enumeration;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.NetworkInterface;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;

public final class LocalRMIServerSocketFactory implements RMIServerSocketFactory
{
    @Override
    public ServerSocket createServerSocket(final int n) throws IOException {
        return new ServerSocket(n) {
            @Override
            public Socket accept() throws IOException {
                final Socket accept = super.accept();
                final InetAddress inetAddress = accept.getInetAddress();
                if (inetAddress == null) {
                    String s = "";
                    if (accept.isClosed()) {
                        s = " Socket is closed.";
                    }
                    else if (!accept.isConnected()) {
                        s = " Socket is not connected";
                    }
                    try {
                        accept.close();
                    }
                    catch (final Exception ex) {}
                    throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported. Couldn't determine client address." + s);
                }
                if (inetAddress.isLoopbackAddress()) {
                    return accept;
                }
                Enumeration<NetworkInterface> networkInterfaces;
                try {
                    networkInterfaces = NetworkInterface.getNetworkInterfaces();
                }
                catch (final SocketException ex2) {
                    try {
                        accept.close();
                    }
                    catch (final IOException ex3) {}
                    throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported.", ex2);
                }
                while (networkInterfaces.hasMoreElements()) {
                    final Enumeration<InetAddress> inetAddresses = networkInterfaces.nextElement().getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        if (inetAddresses.nextElement().equals(inetAddress)) {
                            return accept;
                        }
                    }
                }
                try {
                    accept.close();
                }
                catch (final IOException ex4) {}
                throw new IOException("The server sockets created using the LocalRMIServerSocketFactory only accept connections from clients running on the host where the RMI remote objects have been exported.");
            }
        };
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof LocalRMIServerSocketFactory;
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
