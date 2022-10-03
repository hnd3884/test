package java.rmi.registry;

import java.rmi.server.RemoteRef;
import sun.rmi.server.Util;
import sun.rmi.server.UnicastRef2;
import sun.rmi.server.UnicastRef;
import sun.rmi.registry.RegistryImpl;
import sun.rmi.transport.Endpoint;
import sun.rmi.transport.LiveRef;
import java.rmi.server.RMIServerSocketFactory;
import sun.rmi.transport.tcp.TCPEndpoint;
import java.rmi.server.ObjID;
import java.net.InetAddress;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.RemoteException;

public final class LocateRegistry
{
    private LocateRegistry() {
    }
    
    public static Registry getRegistry() throws RemoteException {
        return getRegistry(null, 1099);
    }
    
    public static Registry getRegistry(final int n) throws RemoteException {
        return getRegistry(null, n);
    }
    
    public static Registry getRegistry(final String s) throws RemoteException {
        return getRegistry(s, 1099);
    }
    
    public static Registry getRegistry(final String s, final int n) throws RemoteException {
        return getRegistry(s, n, null);
    }
    
    public static Registry getRegistry(String hostAddress, int n, final RMIClientSocketFactory rmiClientSocketFactory) throws RemoteException {
        if (n <= 0) {
            n = 1099;
        }
        Label_0036: {
            if (hostAddress != null) {
                if (hostAddress.length() != 0) {
                    break Label_0036;
                }
            }
            try {
                hostAddress = InetAddress.getLocalHost().getHostAddress();
            }
            catch (final Exception ex) {
                hostAddress = "";
            }
        }
        final LiveRef liveRef = new LiveRef(new ObjID(0), new TCPEndpoint(hostAddress, n, rmiClientSocketFactory, null), false);
        return (Registry)Util.createProxy(RegistryImpl.class, (rmiClientSocketFactory == null) ? new UnicastRef(liveRef) : new UnicastRef2(liveRef), false);
    }
    
    public static Registry createRegistry(final int n) throws RemoteException {
        return new RegistryImpl(n);
    }
    
    public static Registry createRegistry(final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) throws RemoteException {
        return new RegistryImpl(n, rmiClientSocketFactory, rmiServerSocketFactory);
    }
}
