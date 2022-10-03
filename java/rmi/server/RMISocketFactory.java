package java.rmi.server;

import sun.rmi.transport.proxy.RMIMasterSocketFactory;
import java.net.SocketException;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;

public abstract class RMISocketFactory implements RMIClientSocketFactory, RMIServerSocketFactory
{
    private static RMISocketFactory factory;
    private static RMISocketFactory defaultSocketFactory;
    private static RMIFailureHandler handler;
    
    @Override
    public abstract Socket createSocket(final String p0, final int p1) throws IOException;
    
    @Override
    public abstract ServerSocket createServerSocket(final int p0) throws IOException;
    
    public static synchronized void setSocketFactory(final RMISocketFactory factory) throws IOException {
        if (RMISocketFactory.factory != null) {
            throw new SocketException("factory already defined");
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkSetFactory();
        }
        RMISocketFactory.factory = factory;
    }
    
    public static synchronized RMISocketFactory getSocketFactory() {
        return RMISocketFactory.factory;
    }
    
    public static synchronized RMISocketFactory getDefaultSocketFactory() {
        if (RMISocketFactory.defaultSocketFactory == null) {
            RMISocketFactory.defaultSocketFactory = new RMIMasterSocketFactory();
        }
        return RMISocketFactory.defaultSocketFactory;
    }
    
    public static synchronized void setFailureHandler(final RMIFailureHandler handler) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkSetFactory();
        }
        RMISocketFactory.handler = handler;
    }
    
    public static synchronized RMIFailureHandler getFailureHandler() {
        return RMISocketFactory.handler;
    }
    
    static {
        RMISocketFactory.factory = null;
        RMISocketFactory.handler = null;
    }
}
