package sun.rmi.transport.tcp;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import sun.rmi.transport.proxy.HttpReceiveSocket;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.lang.ref.SoftReference;
import java.rmi.server.RMIFailureHandler;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.logging.Level;
import java.util.concurrent.RejectedExecutionException;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import sun.security.action.GetLongAction;
import sun.security.action.GetIntegerAction;
import java.rmi.server.LogStream;
import sun.security.action.GetPropertyAction;
import sun.rmi.transport.Channel;
import java.rmi.server.ServerNotActiveException;
import sun.rmi.transport.DGCAckHandler;
import java.io.DataInput;
import java.rmi.server.UID;
import java.io.DataOutputStream;
import java.rmi.server.RemoteCall;
import sun.rmi.transport.StreamRemoteCall;
import java.io.DataInputStream;
import sun.rmi.transport.Connection;
import java.net.Socket;
import java.net.BindException;
import java.rmi.server.ExportException;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.rmi.runtime.NewThreadAction;
import java.io.IOException;
import java.rmi.RemoteException;
import sun.rmi.transport.Target;
import java.lang.ref.WeakReference;
import sun.rmi.transport.Endpoint;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.WeakHashMap;
import java.rmi.server.RMISocketFactory;
import java.lang.ref.Reference;
import java.util.Map;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.security.AccessControlContext;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;
import sun.rmi.runtime.Log;
import sun.rmi.transport.Transport;

public class TCPTransport extends Transport
{
    static final Log tcpLog;
    private static final int maxConnectionThreads;
    private static final long threadKeepAliveTime;
    private static final ExecutorService connectionThreadPool;
    private static final boolean disableIncomingHttp;
    private static final AtomicInteger connectionCount;
    private static final ThreadLocal<ConnectionHandler> threadConnectionHandler;
    private static final AccessControlContext NOPERMS_ACC;
    private final LinkedList<TCPEndpoint> epList;
    private int exportCount;
    private ServerSocket server;
    private final Map<TCPEndpoint, Reference<TCPChannel>> channelTable;
    static final RMISocketFactory defaultSocketFactory;
    private static final int connectionReadTimeout;
    
    TCPTransport(final LinkedList<TCPEndpoint> epList) {
        this.exportCount = 0;
        this.server = null;
        this.channelTable = new WeakHashMap<TCPEndpoint, Reference<TCPChannel>>();
        this.epList = epList;
        if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
            TCPTransport.tcpLog.log(Log.BRIEF, "Version = 2, ep = " + this.getEndpoint());
        }
    }
    
    public void shedConnectionCaches() {
        final ArrayList list;
        synchronized (this.channelTable) {
            list = new ArrayList(this.channelTable.values().size());
            final Iterator<Reference<TCPChannel>> iterator = this.channelTable.values().iterator();
            while (iterator.hasNext()) {
                final TCPChannel tcpChannel = iterator.next().get();
                if (tcpChannel != null) {
                    list.add(tcpChannel);
                }
            }
        }
        final Iterator iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            ((TCPChannel)iterator2.next()).shedCache();
        }
    }
    
    @Override
    public TCPChannel getChannel(final Endpoint endpoint) {
        TCPChannel tcpChannel = null;
        if (endpoint instanceof TCPEndpoint) {
            synchronized (this.channelTable) {
                final Reference reference = this.channelTable.get(endpoint);
                if (reference != null) {
                    tcpChannel = (TCPChannel)reference.get();
                }
                if (tcpChannel == null) {
                    final TCPEndpoint tcpEndpoint = (TCPEndpoint)endpoint;
                    tcpChannel = new TCPChannel(this, tcpEndpoint);
                    this.channelTable.put(tcpEndpoint, new WeakReference<TCPChannel>(tcpChannel));
                }
            }
        }
        return tcpChannel;
    }
    
    @Override
    public void free(final Endpoint endpoint) {
        if (endpoint instanceof TCPEndpoint) {
            synchronized (this.channelTable) {
                final Reference reference = this.channelTable.remove(endpoint);
                if (reference != null) {
                    final TCPChannel tcpChannel = (TCPChannel)reference.get();
                    if (tcpChannel != null) {
                        tcpChannel.shedCache();
                    }
                }
            }
        }
    }
    
    @Override
    public void exportObject(final Target target) throws RemoteException {
        synchronized (this) {
            this.listen();
            ++this.exportCount;
        }
        boolean b = false;
        try {
            super.exportObject(target);
            b = true;
        }
        finally {
            if (!b) {
                synchronized (this) {
                    this.decrementExportCount();
                }
            }
        }
    }
    
    @Override
    protected synchronized void targetUnexported() {
        this.decrementExportCount();
    }
    
    private void decrementExportCount() {
        assert Thread.holdsLock(this);
        --this.exportCount;
        if (this.exportCount == 0 && this.getEndpoint().getListenPort() != 0) {
            final ServerSocket server = this.server;
            this.server = null;
            try {
                server.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    @Override
    protected void checkAcceptPermission(final AccessControlContext accessControlContext) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            return;
        }
        final ConnectionHandler connectionHandler = TCPTransport.threadConnectionHandler.get();
        if (connectionHandler == null) {
            throw new Error("checkAcceptPermission not in ConnectionHandler thread");
        }
        connectionHandler.checkAcceptPermission(securityManager, accessControlContext);
    }
    
    private TCPEndpoint getEndpoint() {
        synchronized (this.epList) {
            return this.epList.getLast();
        }
    }
    
    private void listen() throws RemoteException {
        assert Thread.holdsLock(this);
        final TCPEndpoint endpoint = this.getEndpoint();
        final int port = endpoint.getPort();
        if (this.server == null) {
            if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                TCPTransport.tcpLog.log(Log.BRIEF, "(port " + port + ") create server socket");
            }
            try {
                this.server = endpoint.newServerSocket();
                AccessController.doPrivileged((PrivilegedAction<Thread>)new NewThreadAction(new AcceptLoop(this.server), "TCP Accept-" + port, true)).start();
                return;
            }
            catch (final BindException ex) {
                throw new ExportException("Port already in use: " + port, ex);
            }
            catch (final IOException ex2) {
                throw new ExportException("Listen failed on port: " + port, ex2);
            }
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkListen(port);
        }
    }
    
    private static void closeSocket(final Socket socket) {
        try {
            socket.close();
        }
        catch (final IOException ex) {}
    }
    
    void handleMessages(final Connection connection, final boolean b) {
        final int port = this.getEndpoint().getPort();
        try {
            final DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
            do {
                final int read = dataInputStream.read();
                if (read == -1) {
                    if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                        TCPTransport.tcpLog.log(Log.BRIEF, "(port " + port + ") connection closed");
                        break;
                    }
                    break;
                }
                else {
                    if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                        TCPTransport.tcpLog.log(Log.BRIEF, "(port " + port + ") op = " + read);
                    }
                    switch (read) {
                        case 80: {
                            if (!this.serviceCall(new StreamRemoteCall(connection))) {
                                return;
                            }
                            continue;
                        }
                        case 82: {
                            new DataOutputStream(connection.getOutputStream()).writeByte(83);
                            connection.releaseOutputStream();
                            continue;
                        }
                        case 84: {
                            DGCAckHandler.received(UID.read(dataInputStream));
                            continue;
                        }
                        default: {
                            throw new IOException("unknown transport op " + read);
                        }
                    }
                }
            } while (b);
        }
        catch (final IOException ex) {
            if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                TCPTransport.tcpLog.log(Log.BRIEF, "(port " + port + ") exception: ", ex);
            }
        }
        finally {
            try {
                connection.close();
            }
            catch (final IOException ex2) {}
        }
    }
    
    public static String getClientHost() throws ServerNotActiveException {
        final ConnectionHandler connectionHandler = TCPTransport.threadConnectionHandler.get();
        if (connectionHandler != null) {
            return connectionHandler.getClientHost();
        }
        throw new ServerNotActiveException("not in a remote call");
    }
    
    static {
        tcpLog = Log.getLog("sun.rmi.transport.tcp", "tcp", LogStream.parseLevel(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.rmi.transport.tcp.logLevel"))));
        maxConnectionThreads = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("sun.rmi.transport.tcp.maxConnectionThreads", Integer.MAX_VALUE));
        threadKeepAliveTime = AccessController.doPrivileged((PrivilegedAction<Long>)new GetLongAction("sun.rmi.transport.tcp.threadKeepAliveTime", 60000L));
        connectionThreadPool = new ThreadPoolExecutor(0, TCPTransport.maxConnectionThreads, TCPTransport.threadKeepAliveTime, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                return AccessController.doPrivileged((PrivilegedAction<Thread>)new NewThreadAction(runnable, "TCP Connection(idle)", true, true));
            }
        });
        disableIncomingHttp = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.rmi.server.disableIncomingHttp", "true")).equalsIgnoreCase("true");
        connectionCount = new AtomicInteger(0);
        threadConnectionHandler = new ThreadLocal<ConnectionHandler>();
        NOPERMS_ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, new Permissions()) });
        defaultSocketFactory = RMISocketFactory.getDefaultSocketFactory();
        connectionReadTimeout = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("sun.rmi.transport.tcp.readTimeout", 7200000));
    }
    
    private class AcceptLoop implements Runnable
    {
        private final ServerSocket serverSocket;
        private long lastExceptionTime;
        private int recentExceptionCount;
        
        AcceptLoop(final ServerSocket serverSocket) {
            this.lastExceptionTime = 0L;
            this.serverSocket = serverSocket;
        }
        
        @Override
        public void run() {
            try {
                this.executeAcceptLoop();
            }
            finally {
                try {
                    this.serverSocket.close();
                }
                catch (final IOException ex) {}
            }
        }
        
        private void executeAcceptLoop() {
            if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                TCPTransport.tcpLog.log(Log.BRIEF, "listening on port " + TCPTransport.this.getEndpoint().getPort());
            }
            while (true) {
                Socket accept = null;
                try {
                    accept = this.serverSocket.accept();
                    final InetAddress inetAddress = accept.getInetAddress();
                    final String s = (inetAddress != null) ? inetAddress.getHostAddress() : "0.0.0.0";
                    try {
                        TCPTransport.connectionThreadPool.execute(new ConnectionHandler(accept, s));
                    }
                    catch (final RejectedExecutionException ex) {
                        closeSocket(accept);
                        TCPTransport.tcpLog.log(Log.BRIEF, "rejected connection from " + s);
                    }
                }
                catch (final Throwable t) {
                    try {
                        if (this.serverSocket.isClosed()) {
                            break;
                        }
                        try {
                            if (TCPTransport.tcpLog.isLoggable(Level.WARNING)) {
                                TCPTransport.tcpLog.log(Level.WARNING, "accept loop for " + this.serverSocket + " throws", t);
                            }
                        }
                        catch (final Throwable t2) {}
                    }
                    finally {
                        if (accept != null) {
                            closeSocket(accept);
                        }
                    }
                    if (!(t instanceof SecurityException)) {
                        try {
                            TCPEndpoint.shedConnectionCaches();
                        }
                        catch (final Throwable t3) {}
                    }
                    if (t instanceof Exception || t instanceof OutOfMemoryError || t instanceof NoClassDefFoundError) {
                        if (!this.continueAfterAcceptFailure(t)) {
                            return;
                        }
                        continue;
                    }
                    else {
                        if (t instanceof Error) {
                            throw (Error)t;
                        }
                        throw new UndeclaredThrowableException(t);
                    }
                }
            }
        }
        
        private boolean continueAfterAcceptFailure(final Throwable t) {
            final RMIFailureHandler failureHandler = RMISocketFactory.getFailureHandler();
            if (failureHandler != null) {
                return failureHandler.failure((t instanceof Exception) ? ((Exception)t) : new InvocationTargetException(t));
            }
            this.throttleLoopOnException();
            return true;
        }
        
        private void throttleLoopOnException() {
            final long currentTimeMillis = System.currentTimeMillis();
            if (this.lastExceptionTime == 0L || currentTimeMillis - this.lastExceptionTime > 5000L) {
                this.lastExceptionTime = currentTimeMillis;
                this.recentExceptionCount = 0;
            }
            else if (++this.recentExceptionCount >= 10) {
                try {
                    Thread.sleep(10000L);
                }
                catch (final InterruptedException ex) {}
            }
        }
    }
    
    private class ConnectionHandler implements Runnable
    {
        private static final int POST = 1347375956;
        private AccessControlContext okContext;
        private Map<AccessControlContext, Reference<AccessControlContext>> authCache;
        private SecurityManager cacheSecurityManager;
        private Socket socket;
        private String remoteHost;
        
        ConnectionHandler(final Socket socket, final String remoteHost) {
            this.cacheSecurityManager = null;
            this.socket = socket;
            this.remoteHost = remoteHost;
        }
        
        String getClientHost() {
            return this.remoteHost;
        }
        
        void checkAcceptPermission(final SecurityManager cacheSecurityManager, final AccessControlContext okContext) {
            if (cacheSecurityManager != this.cacheSecurityManager) {
                this.okContext = null;
                this.authCache = new WeakHashMap<AccessControlContext, Reference<AccessControlContext>>();
                this.cacheSecurityManager = cacheSecurityManager;
            }
            if (okContext.equals(this.okContext) || this.authCache.containsKey(okContext)) {
                return;
            }
            final InetAddress inetAddress = this.socket.getInetAddress();
            cacheSecurityManager.checkAccept((inetAddress != null) ? inetAddress.getHostAddress() : "*", this.socket.getPort());
            this.authCache.put(okContext, new SoftReference<AccessControlContext>(okContext));
            this.okContext = okContext;
        }
        
        @Override
        public void run() {
            final Thread currentThread = Thread.currentThread();
            final String name = currentThread.getName();
            try {
                currentThread.setName("RMI TCP Connection(" + TCPTransport.connectionCount.incrementAndGet() + ")-" + this.remoteHost);
                AccessController.doPrivileged(() -> {
                    this.run0();
                    return null;
                }, TCPTransport.NOPERMS_ACC);
            }
            finally {
                currentThread.setName(name);
            }
        }
        
        private void run0() {
            final TCPEndpoint access$000 = TCPTransport.this.getEndpoint();
            final int port = access$000.getPort();
            TCPTransport.threadConnectionHandler.set(this);
            try {
                this.socket.setTcpNoDelay(true);
            }
            catch (final Exception ex) {}
            try {
                if (TCPTransport.connectionReadTimeout > 0) {
                    this.socket.setSoTimeout(TCPTransport.connectionReadTimeout);
                }
            }
            catch (final Exception ex2) {}
            try {
                final InputStream inputStream = this.socket.getInputStream();
                BufferedInputStream bufferedInputStream = (BufferedInputStream)(inputStream.markSupported() ? inputStream : new BufferedInputStream(inputStream));
                bufferedInputStream.mark(4);
                DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
                int n = dataInputStream.readInt();
                if (n == 1347375956) {
                    if (TCPTransport.disableIncomingHttp) {
                        throw new RemoteException("RMI over HTTP is disabled");
                    }
                    TCPTransport.tcpLog.log(Log.BRIEF, "decoding HTTP-wrapped call");
                    bufferedInputStream.reset();
                    try {
                        this.socket = new HttpReceiveSocket(this.socket, bufferedInputStream, null);
                        this.remoteHost = "0.0.0.0";
                        bufferedInputStream = new BufferedInputStream(this.socket.getInputStream());
                        dataInputStream = new DataInputStream(bufferedInputStream);
                        n = dataInputStream.readInt();
                    }
                    catch (final IOException ex3) {
                        throw new RemoteException("Error HTTP-unwrapping call", ex3);
                    }
                }
                final short short1 = dataInputStream.readShort();
                if (n != 1246907721 || short1 != 2) {
                    closeSocket(this.socket);
                    return;
                }
                final OutputStream outputStream = this.socket.getOutputStream();
                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                final DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
                final int port2 = this.socket.getPort();
                if (TCPTransport.tcpLog.isLoggable(Log.BRIEF)) {
                    TCPTransport.tcpLog.log(Log.BRIEF, "accepted socket from [" + this.remoteHost + ":" + port2 + "]");
                }
                switch (dataInputStream.readByte()) {
                    case 76: {
                        TCPTransport.this.handleMessages(new TCPConnection(new TCPChannel(TCPTransport.this, new TCPEndpoint(this.remoteHost, this.socket.getLocalPort(), access$000.getClientSocketFactory(), access$000.getServerSocketFactory())), this.socket, bufferedInputStream, bufferedOutputStream), false);
                        break;
                    }
                    case 75: {
                        dataOutputStream.writeByte(78);
                        if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                            TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + port + ") suggesting " + this.remoteHost + ":" + port2);
                        }
                        dataOutputStream.writeUTF(this.remoteHost);
                        dataOutputStream.writeInt(port2);
                        dataOutputStream.flush();
                        final String utf = dataInputStream.readUTF();
                        final int int1 = dataInputStream.readInt();
                        if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                            TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + port + ") client using " + utf + ":" + int1);
                        }
                        TCPTransport.this.handleMessages(new TCPConnection(new TCPChannel(TCPTransport.this, new TCPEndpoint(this.remoteHost, this.socket.getLocalPort(), access$000.getClientSocketFactory(), access$000.getServerSocketFactory())), this.socket, bufferedInputStream, bufferedOutputStream), true);
                        break;
                    }
                    case 77: {
                        if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                            TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + port + ") accepting multiplex protocol");
                        }
                        dataOutputStream.writeByte(78);
                        if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                            TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + port + ") suggesting " + this.remoteHost + ":" + port2);
                        }
                        dataOutputStream.writeUTF(this.remoteHost);
                        dataOutputStream.writeInt(port2);
                        dataOutputStream.flush();
                        final TCPEndpoint tcpEndpoint = new TCPEndpoint(dataInputStream.readUTF(), dataInputStream.readInt(), access$000.getClientSocketFactory(), access$000.getServerSocketFactory());
                        if (TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                            TCPTransport.tcpLog.log(Log.VERBOSE, "(port " + port + ") client using " + tcpEndpoint.getHost() + ":" + tcpEndpoint.getPort());
                        }
                        final ConnectionMultiplexer connectionMultiplexer;
                        synchronized (TCPTransport.this.channelTable) {
                            final TCPChannel channel = TCPTransport.this.getChannel(tcpEndpoint);
                            connectionMultiplexer = new ConnectionMultiplexer(channel, bufferedInputStream, outputStream, false);
                            channel.useMultiplexer(connectionMultiplexer);
                        }
                        connectionMultiplexer.run();
                        break;
                    }
                    default: {
                        dataOutputStream.writeByte(79);
                        dataOutputStream.flush();
                        break;
                    }
                }
            }
            catch (final IOException ex4) {
                TCPTransport.tcpLog.log(Log.BRIEF, "terminated with exception:", ex4);
            }
            finally {
                closeSocket(this.socket);
            }
        }
    }
}
