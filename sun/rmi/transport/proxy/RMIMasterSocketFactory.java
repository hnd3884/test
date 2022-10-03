package sun.rmi.transport.proxy;

import java.security.AccessControlContext;
import sun.security.action.GetBooleanAction;
import java.rmi.server.LogStream;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.NoRouteToHostException;
import java.io.InterruptedIOException;
import sun.rmi.runtime.NewThreadAction;
import java.net.Socket;
import sun.security.action.GetLongAction;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Vector;
import java.util.Hashtable;
import sun.rmi.runtime.Log;
import java.rmi.server.RMISocketFactory;

public class RMIMasterSocketFactory extends RMISocketFactory
{
    static int logLevel;
    static final Log proxyLog;
    private static long connectTimeout;
    private static final boolean eagerHttpFallback;
    private Hashtable<String, RMISocketFactory> successTable;
    private static final int MaxRememberedHosts = 64;
    private Vector<String> hostList;
    protected RMISocketFactory initialFactory;
    protected Vector<RMISocketFactory> altFactoryList;
    
    private static String getLogLevel() {
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.rmi.transport.proxy.logLevel"));
    }
    
    private static long getConnectTimeout() {
        return AccessController.doPrivileged((PrivilegedAction<Long>)new GetLongAction("sun.rmi.transport.proxy.connectTimeout", 15000L));
    }
    
    public RMIMasterSocketFactory() {
        this.successTable = new Hashtable<String, RMISocketFactory>();
        this.hostList = new Vector<String>(64);
        this.initialFactory = new RMIDirectSocketFactory();
        this.altFactoryList = new Vector<RMISocketFactory>(2);
        boolean b = false;
        try {
            String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("http.proxyHost"));
            if (s == null) {
                s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("proxyHost"));
            }
            if (!AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.rmi.server.disableHttp", "true")).equalsIgnoreCase("true") && s != null && s.length() > 0) {
                b = true;
            }
        }
        catch (final Exception ex) {}
        if (b) {
            this.altFactoryList.addElement(new RMIHttpToPortSocketFactory());
            this.altFactoryList.addElement(new RMIHttpToCGISocketFactory());
        }
    }
    
    @Override
    public Socket createSocket(final String s, final int n) throws IOException {
        if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF)) {
            RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "host: " + s + ", port: " + n);
        }
        if (this.altFactoryList.size() == 0) {
            return this.initialFactory.createSocket(s, n);
        }
        RMISocketFactory rmiSocketFactory = this.successTable.get(s);
        if (rmiSocketFactory != null) {
            if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF)) {
                RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "previously successful factory found: " + rmiSocketFactory);
            }
            return rmiSocketFactory.createSocket(s, n);
        }
        Socket socket = null;
        Socket socket2 = null;
        final AsyncConnector asyncConnector = new AsyncConnector(this.initialFactory, s, n, AccessController.getContext());
        Throwable t = null;
        Label_1461: {
            try {
                synchronized (asyncConnector) {
                    AccessController.doPrivileged((PrivilegedAction<Thread>)new NewThreadAction(asyncConnector, "AsyncConnector", true)).start();
                    try {
                        long n2 = System.currentTimeMillis();
                        final long n3 = n2 + RMIMasterSocketFactory.connectTimeout;
                        do {
                            asyncConnector.wait(n3 - n2);
                            socket = this.checkConnector(asyncConnector);
                            if (socket != null) {
                                break;
                            }
                            n2 = System.currentTimeMillis();
                        } while (n2 < n3);
                    }
                    catch (final InterruptedException ex) {
                        throw new InterruptedIOException("interrupted while waiting for connector");
                    }
                }
                if (socket == null) {
                    throw new NoRouteToHostException("connect timed out: " + s);
                }
                RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "direct socket connection successful");
                return socket;
            }
            catch (final UnknownHostException | NoRouteToHostException ex2) {
                t = (Throwable)ex2;
            }
            catch (final SocketException ex3) {
                if (RMIMasterSocketFactory.eagerHttpFallback) {
                    t = ex3;
                    break Label_1461;
                }
                throw ex3;
            }
            finally {
                if (t != null) {
                    if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF)) {
                        RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "direct socket connection failed: ", t);
                    }
                    int i = 0;
                    while (i < this.altFactoryList.size()) {
                        rmiSocketFactory = this.altFactoryList.elementAt(i);
                        if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF)) {
                            RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "trying with factory: " + rmiSocketFactory);
                        }
                        Label_1425: {
                            Label_1452: {
                                try (final Socket socket3 = rmiSocketFactory.createSocket(s, n)) {
                                    socket3.getInputStream().read();
                                }
                                catch (final IOException ex4) {
                                    if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF)) {
                                        RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "factory failed: ", ex4);
                                    }
                                    break Label_1452;
                                }
                                break Label_1425;
                            }
                            ++i;
                            continue;
                        }
                        RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "factory succeeded");
                        try {
                            socket2 = rmiSocketFactory.createSocket(s, n);
                        }
                        catch (final IOException ex5) {}
                        break;
                    }
                }
            }
        }
        synchronized (this.successTable) {
            try {
                synchronized (asyncConnector) {
                    socket = this.checkConnector(asyncConnector);
                }
                if (socket != null) {
                    if (socket2 != null) {
                        socket2.close();
                    }
                    return socket;
                }
                asyncConnector.notUsed();
            }
            catch (final UnknownHostException | NoRouteToHostException ex6) {
                t = (Throwable)ex6;
            }
            catch (final SocketException ex7) {
                if (!RMIMasterSocketFactory.eagerHttpFallback) {
                    throw ex7;
                }
                t = ex7;
            }
            if (socket2 != null) {
                this.rememberFactory(s, rmiSocketFactory);
                return socket2;
            }
            throw t;
        }
    }
    
    void rememberFactory(final String s, final RMISocketFactory rmiSocketFactory) {
        synchronized (this.successTable) {
            while (this.hostList.size() >= 64) {
                this.successTable.remove(this.hostList.elementAt(0));
                this.hostList.removeElementAt(0);
            }
            this.hostList.addElement(s);
            this.successTable.put(s, rmiSocketFactory);
        }
    }
    
    Socket checkConnector(final AsyncConnector asyncConnector) throws IOException {
        final Exception access$000 = asyncConnector.getException();
        if (access$000 == null) {
            return asyncConnector.getSocket();
        }
        access$000.fillInStackTrace();
        if (access$000 instanceof IOException) {
            throw (IOException)access$000;
        }
        if (access$000 instanceof RuntimeException) {
            throw (RuntimeException)access$000;
        }
        throw new Error("internal error: unexpected checked exception: " + access$000.toString());
    }
    
    @Override
    public ServerSocket createServerSocket(final int n) throws IOException {
        return this.initialFactory.createServerSocket(n);
    }
    
    static {
        RMIMasterSocketFactory.logLevel = LogStream.parseLevel(getLogLevel());
        proxyLog = Log.getLog("sun.rmi.transport.tcp.proxy", "transport", RMIMasterSocketFactory.logLevel);
        RMIMasterSocketFactory.connectTimeout = getConnectTimeout();
        eagerHttpFallback = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.rmi.transport.proxy.eagerHttpFallback"));
    }
    
    private class AsyncConnector implements Runnable
    {
        private RMISocketFactory factory;
        private String host;
        private int port;
        private AccessControlContext acc;
        private Exception exception;
        private Socket socket;
        private boolean cleanUp;
        
        AsyncConnector(final RMISocketFactory factory, final String host, final int port, final AccessControlContext acc) {
            this.exception = null;
            this.socket = null;
            this.cleanUp = false;
            this.factory = factory;
            this.host = host;
            this.port = port;
            this.acc = acc;
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkConnect(host, port);
            }
        }
        
        @Override
        public void run() {
            try {
                final Socket socket = this.factory.createSocket(this.host, this.port);
                synchronized (this) {
                    this.socket = socket;
                    this.notify();
                }
                RMIMasterSocketFactory.this.rememberFactory(this.host, this.factory);
                synchronized (this) {
                    if (this.cleanUp) {
                        try {
                            this.socket.close();
                        }
                        catch (final IOException ex) {}
                    }
                }
            }
            catch (final Exception exception) {
                synchronized (this) {
                    this.exception = exception;
                    this.notify();
                }
            }
        }
        
        private synchronized Exception getException() {
            return this.exception;
        }
        
        private synchronized Socket getSocket() {
            return this.socket;
        }
        
        synchronized void notUsed() {
            if (this.socket != null) {
                try {
                    this.socket.close();
                }
                catch (final IOException ex) {}
            }
            this.cleanUp = true;
        }
    }
}
