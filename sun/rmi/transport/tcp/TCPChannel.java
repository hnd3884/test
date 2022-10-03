package sun.rmi.transport.tcp;

import sun.rmi.runtime.RuntimeUtil;
import sun.security.action.GetIntegerAction;
import java.security.PrivilegedAction;
import sun.security.action.GetLongAction;
import java.util.ListIterator;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.net.Socket;
import java.io.IOException;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.ConnectIOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.rmi.RemoteException;
import sun.rmi.runtime.Log;
import sun.rmi.transport.Connection;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import sun.rmi.transport.Endpoint;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.lang.ref.Reference;
import java.util.WeakHashMap;
import java.security.AccessControlContext;
import java.util.concurrent.Future;
import java.util.List;
import sun.rmi.transport.Channel;

public class TCPChannel implements Channel
{
    private final TCPEndpoint ep;
    private final TCPTransport tr;
    private final List<TCPConnection> freeList;
    private Future<?> reaper;
    private boolean usingMultiplexer;
    private ConnectionMultiplexer multiplexer;
    private ConnectionAcceptor acceptor;
    private AccessControlContext okContext;
    private WeakHashMap<AccessControlContext, Reference<AccessControlContext>> authcache;
    private SecurityManager cacheSecurityManager;
    private static final long idleTimeout;
    private static final int handshakeTimeout;
    private static final int responseTimeout;
    private static final ScheduledExecutorService scheduler;
    
    TCPChannel(final TCPTransport tr, final TCPEndpoint ep) {
        this.freeList = new ArrayList<TCPConnection>();
        this.reaper = null;
        this.usingMultiplexer = false;
        this.multiplexer = null;
        this.cacheSecurityManager = null;
        this.tr = tr;
        this.ep = ep;
    }
    
    @Override
    public Endpoint getEndpoint() {
        return this.ep;
    }
    
    private void checkConnectPermission() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            return;
        }
        if (securityManager != this.cacheSecurityManager) {
            this.okContext = null;
            this.authcache = new WeakHashMap<AccessControlContext, Reference<AccessControlContext>>();
            this.cacheSecurityManager = securityManager;
        }
        final AccessControlContext context = AccessController.getContext();
        if (this.okContext == null || (!this.okContext.equals(context) && !this.authcache.containsKey(context))) {
            securityManager.checkConnect(this.ep.getHost(), this.ep.getPort());
            this.authcache.put(context, new SoftReference<AccessControlContext>(context));
        }
        this.okContext = context;
    }
    
    @Override
    public Connection newConnection() throws RemoteException {
        TCPConnection tcpConnection;
        do {
            tcpConnection = null;
            synchronized (this.freeList) {
                final int n = this.freeList.size() - 1;
                if (n >= 0) {
                    this.checkConnectPermission();
                    tcpConnection = this.freeList.get(n);
                    this.freeList.remove(n);
                }
            }
            if (tcpConnection != null) {
                if (!tcpConnection.isDead()) {
                    TCPTransport.tcpLog.log(Log.BRIEF, "reuse connection");
                    return tcpConnection;
                }
                this.free(tcpConnection, false);
            }
        } while (tcpConnection != null);
        return this.createConnection();
    }
    
    private Connection createConnection() throws RemoteException {
        TCPTransport.tcpLog.log(Log.BRIEF, "create connection");
        TCPConnection openConnection;
        if (!this.usingMultiplexer) {
            final Socket socket = this.ep.newSocket();
            openConnection = new TCPConnection(this, socket);
            try {
                final DataOutputStream dataOutputStream = new DataOutputStream(openConnection.getOutputStream());
                this.writeTransportHeader(dataOutputStream);
                if (!openConnection.isReusable()) {
                    dataOutputStream.writeByte(76);
                }
                else {
                    dataOutputStream.writeByte(75);
                    dataOutputStream.flush();
                    int soTimeout = 0;
                    try {
                        soTimeout = socket.getSoTimeout();
                        socket.setSoTimeout(TCPChannel.handshakeTimeout);
                    }
                    catch (final Exception ex) {}
                    final DataInputStream dataInputStream = new DataInputStream(openConnection.getInputStream());
                    final byte byte1 = dataInputStream.readByte();
                    if (byte1 != 78) {
                        throw new ConnectIOException((byte1 == 79) ? "JRMP StreamProtocol not supported by server" : "non-JRMP server at remote endpoint");
                    }
                    final String utf = dataInputStream.readUTF();
                    final int int1 = dataInputStream.readInt();
                    if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                        TCPTransport.tcpLog.log(Log.VERBOSE, "server suggested " + utf + ":" + int1);
                    }
                    TCPEndpoint.setLocalHost(utf);
                    final TCPEndpoint localEndpoint = TCPEndpoint.getLocalEndpoint(0, null, null);
                    dataOutputStream.writeUTF(localEndpoint.getHost());
                    dataOutputStream.writeInt(localEndpoint.getPort());
                    if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                        TCPTransport.tcpLog.log(Log.VERBOSE, "using " + localEndpoint.getHost() + ":" + localEndpoint.getPort());
                    }
                    try {
                        socket.setSoTimeout((soTimeout != 0) ? soTimeout : TCPChannel.responseTimeout);
                    }
                    catch (final Exception ex2) {}
                    dataOutputStream.flush();
                }
            }
            catch (final IOException ex3) {
                try {
                    openConnection.close();
                }
                catch (final Exception ex4) {}
                if (ex3 instanceof RemoteException) {
                    throw (RemoteException)ex3;
                }
                throw new ConnectIOException("error during JRMP connection establishment", ex3);
            }
        }
        else {
            try {
                openConnection = this.multiplexer.openConnection();
            }
            catch (final IOException ex5) {
                synchronized (this) {
                    this.usingMultiplexer = false;
                    this.multiplexer = null;
                }
                throw new ConnectIOException("error opening virtual connection over multiplexed connection", ex5);
            }
        }
        return openConnection;
    }
    
    @Override
    public void free(final Connection connection, final boolean b) {
        if (connection == null) {
            return;
        }
        if (b && connection.isReusable()) {
            final long currentTimeMillis = System.currentTimeMillis();
            final TCPConnection tcpConnection = (TCPConnection)connection;
            TCPTransport.tcpLog.log(Log.BRIEF, "reuse connection");
            synchronized (this.freeList) {
                this.freeList.add(tcpConnection);
                if (this.reaper == null) {
                    TCPTransport.tcpLog.log(Log.BRIEF, "create reaper");
                    this.reaper = TCPChannel.scheduler.scheduleWithFixedDelay(new Runnable() {
                        @Override
                        public void run() {
                            TCPTransport.tcpLog.log(Log.VERBOSE, "wake up");
                            TCPChannel.this.freeCachedConnections();
                        }
                    }, TCPChannel.idleTimeout, TCPChannel.idleTimeout, TimeUnit.MILLISECONDS);
                }
            }
            tcpConnection.setLastUseTime(currentTimeMillis);
            tcpConnection.setExpiration(currentTimeMillis + TCPChannel.idleTimeout);
        }
        else {
            TCPTransport.tcpLog.log(Log.BRIEF, "close connection");
            try {
                connection.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    private void writeTransportHeader(final DataOutputStream dataOutputStream) throws RemoteException {
        try {
            final DataOutputStream dataOutputStream2 = new DataOutputStream(dataOutputStream);
            dataOutputStream2.writeInt(1246907721);
            dataOutputStream2.writeShort(2);
        }
        catch (final IOException ex) {
            throw new ConnectIOException("error writing JRMP transport header", ex);
        }
    }
    
    synchronized void useMultiplexer(final ConnectionMultiplexer multiplexer) {
        this.multiplexer = multiplexer;
        this.usingMultiplexer = true;
    }
    
    void acceptMultiplexConnection(final Connection connection) {
        if (this.acceptor == null) {
            (this.acceptor = new ConnectionAcceptor(this.tr)).startNewAcceptor();
        }
        this.acceptor.accept(connection);
    }
    
    public void shedCache() {
        final Connection[] array;
        synchronized (this.freeList) {
            array = this.freeList.toArray(new Connection[this.freeList.size()]);
            this.freeList.clear();
        }
        int length = array.length;
        while (--length >= 0) {
            final Connection connection = array[length];
            array[length] = null;
            try {
                connection.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    private void freeCachedConnections() {
        synchronized (this.freeList) {
            final int size = this.freeList.size();
            if (size > 0) {
                final long currentTimeMillis = System.currentTimeMillis();
                final ListIterator<TCPConnection> listIterator = this.freeList.listIterator(size);
                while (listIterator.hasPrevious()) {
                    final TCPConnection tcpConnection = listIterator.previous();
                    if (tcpConnection.expired(currentTimeMillis)) {
                        TCPTransport.tcpLog.log(Log.VERBOSE, "connection timeout expired");
                        try {
                            tcpConnection.close();
                        }
                        catch (final IOException ex) {}
                        listIterator.remove();
                    }
                }
            }
            if (this.freeList.isEmpty()) {
                this.reaper.cancel(false);
                this.reaper = null;
            }
        }
    }
    
    static {
        idleTimeout = AccessController.doPrivileged((PrivilegedAction<Long>)new GetLongAction("sun.rmi.transport.connectionTimeout", 15000L));
        handshakeTimeout = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("sun.rmi.transport.tcp.handshakeTimeout", 60000));
        responseTimeout = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("sun.rmi.transport.tcp.responseTimeout", 0));
        scheduler = AccessController.doPrivileged((PrivilegedAction<RuntimeUtil>)new RuntimeUtil.GetInstanceAction()).getScheduler();
    }
}
