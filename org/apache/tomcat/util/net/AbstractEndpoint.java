package org.apache.tomcat.util.net;

import java.util.Set;
import org.apache.juli.logging.Log;
import javax.management.MalformedObjectNameException;
import org.apache.tomcat.util.modeler.Registry;
import java.util.concurrent.RejectedExecutionException;
import java.net.SocketException;
import java.util.Enumeration;
import java.net.NetworkInterface;
import org.apache.tomcat.util.ExceptionUtils;
import java.io.OutputStreamWriter;
import java.net.SocketAddress;
import java.net.Socket;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.ResizableExecutor;
import org.apache.tomcat.util.IntrospectionUtils;
import java.util.concurrent.ThreadPoolExecutor;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.List;
import java.net.InetAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ConcurrentMap;
import javax.management.ObjectName;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.threads.LimitLatch;
import org.apache.tomcat.util.res.StringManager;

public abstract class AbstractEndpoint<S>
{
    protected static final StringManager sm;
    private static final int INITIAL_ERROR_DELAY = 50;
    private static final int MAX_ERROR_DELAY = 1600;
    protected volatile boolean running;
    protected volatile boolean paused;
    protected volatile boolean internalExecutor;
    private volatile LimitLatch connectionLimitLatch;
    protected SocketProperties socketProperties;
    protected Acceptor[] acceptors;
    protected SynchronizedStack<SocketProcessorBase<S>> processorCache;
    private ObjectName oname;
    private String defaultSSLHostConfigName;
    protected ConcurrentMap<String, SSLHostConfig> sslHostConfigs;
    private boolean useSendfile;
    private long executorTerminationTimeoutMillis;
    protected int acceptorThreadCount;
    protected int acceptorThreadPriority;
    private int maxConnections;
    private Executor executor;
    private int port;
    private InetAddress address;
    private int acceptCount;
    private boolean bindOnInit;
    private volatile BindState bindState;
    private Integer keepAliveTimeout;
    private boolean SSLEnabled;
    private int minSpareThreads;
    private int maxThreads;
    protected int threadPriority;
    private int maxKeepAliveRequests;
    private int maxHeaderCount;
    private String name;
    private String domain;
    private boolean daemon;
    private boolean useAsyncIO;
    protected final List<String> negotiableProtocols;
    private Handler<S> handler;
    protected HashMap<String, Object> attributes;
    
    public AbstractEndpoint() {
        this.running = false;
        this.paused = false;
        this.internalExecutor = true;
        this.connectionLimitLatch = null;
        this.socketProperties = new SocketProperties();
        this.oname = null;
        this.defaultSSLHostConfigName = "_default_";
        this.sslHostConfigs = new ConcurrentHashMap<String, SSLHostConfig>();
        this.useSendfile = true;
        this.executorTerminationTimeoutMillis = 5000L;
        this.acceptorThreadCount = 1;
        this.acceptorThreadPriority = 5;
        this.maxConnections = 10000;
        this.executor = null;
        this.acceptCount = 100;
        this.bindOnInit = true;
        this.bindState = BindState.UNBOUND;
        this.keepAliveTimeout = null;
        this.SSLEnabled = false;
        this.minSpareThreads = 10;
        this.maxThreads = 200;
        this.threadPriority = 5;
        this.maxKeepAliveRequests = 100;
        this.maxHeaderCount = 100;
        this.name = "TP";
        this.daemon = true;
        this.useAsyncIO = true;
        this.negotiableProtocols = new ArrayList<String>();
        this.handler = null;
        this.attributes = new HashMap<String, Object>();
    }
    
    public static long toTimeout(final long timeout) {
        return (timeout > 0L) ? timeout : Long.MAX_VALUE;
    }
    
    public SocketProperties getSocketProperties() {
        return this.socketProperties;
    }
    
    public String getDefaultSSLHostConfigName() {
        return this.defaultSSLHostConfigName;
    }
    
    public void setDefaultSSLHostConfigName(final String defaultSSLHostConfigName) {
        this.defaultSSLHostConfigName = defaultSSLHostConfigName.toLowerCase(Locale.ENGLISH);
    }
    
    public void addSslHostConfig(final SSLHostConfig sslHostConfig) throws IllegalArgumentException {
        this.addSslHostConfig(sslHostConfig, false);
    }
    
    public void addSslHostConfig(final SSLHostConfig sslHostConfig, final boolean replace) throws IllegalArgumentException {
        final String key = sslHostConfig.getHostName();
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException(AbstractEndpoint.sm.getString("endpoint.noSslHostName"));
        }
        if (this.bindState != BindState.UNBOUND && this.bindState != BindState.SOCKET_CLOSED_ON_STOP && this.isSSLEnabled()) {
            try {
                this.createSSLContext(sslHostConfig);
            }
            catch (final Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (replace) {
            final SSLHostConfig previous = this.sslHostConfigs.put(key, sslHostConfig);
            if (previous != null) {
                this.unregisterJmx(sslHostConfig);
            }
            this.registerJmx(sslHostConfig);
        }
        else {
            final SSLHostConfig duplicate = this.sslHostConfigs.putIfAbsent(key, sslHostConfig);
            if (duplicate != null) {
                this.releaseSSLContext(sslHostConfig);
                throw new IllegalArgumentException(AbstractEndpoint.sm.getString("endpoint.duplicateSslHostName", new Object[] { key }));
            }
            this.registerJmx(sslHostConfig);
        }
    }
    
    public SSLHostConfig removeSslHostConfig(final String hostName) {
        if (hostName == null) {
            return null;
        }
        final String hostNameLower = hostName.toLowerCase(Locale.ENGLISH);
        if (hostNameLower.equals(this.getDefaultSSLHostConfigName())) {
            throw new IllegalArgumentException(AbstractEndpoint.sm.getString("endpoint.removeDefaultSslHostConfig", new Object[] { hostName }));
        }
        final SSLHostConfig sslHostConfig = this.sslHostConfigs.remove(hostNameLower);
        this.unregisterJmx(sslHostConfig);
        return sslHostConfig;
    }
    
    public void reloadSslHostConfig(final String hostName) {
        final SSLHostConfig sslHostConfig = this.sslHostConfigs.get(hostName.toLowerCase(Locale.ENGLISH));
        if (sslHostConfig == null) {
            throw new IllegalArgumentException(AbstractEndpoint.sm.getString("endpoint.unknownSslHostName", new Object[] { hostName }));
        }
        this.addSslHostConfig(sslHostConfig, true);
    }
    
    public void reloadSslHostConfigs() {
        for (final String hostName : this.sslHostConfigs.keySet()) {
            this.reloadSslHostConfig(hostName);
        }
    }
    
    public SSLHostConfig[] findSslHostConfigs() {
        return this.sslHostConfigs.values().toArray(new SSLHostConfig[0]);
    }
    
    protected abstract void createSSLContext(final SSLHostConfig p0) throws Exception;
    
    protected void destroySsl() throws Exception {
        if (this.isSSLEnabled()) {
            for (final SSLHostConfig sslHostConfig : this.sslHostConfigs.values()) {
                this.releaseSSLContext(sslHostConfig);
            }
        }
    }
    
    protected void releaseSSLContext(final SSLHostConfig sslHostConfig) {
        for (final SSLHostConfigCertificate certificate : sslHostConfig.getCertificates(true)) {
            if (certificate.getSslContext() != null) {
                final SSLContext sslContext = certificate.getSslContext();
                if (sslContext == null) {
                    continue;
                }
                sslContext.destroy();
            }
        }
    }
    
    protected SSLHostConfig getSSLHostConfig(final String sniHostName) {
        SSLHostConfig result = null;
        if (sniHostName != null) {
            result = this.sslHostConfigs.get(sniHostName);
            if (result != null) {
                return result;
            }
            final int indexOfDot = sniHostName.indexOf(46);
            if (indexOfDot > -1) {
                result = this.sslHostConfigs.get("*" + sniHostName.substring(indexOfDot));
            }
        }
        if (result == null) {
            result = this.sslHostConfigs.get(this.getDefaultSSLHostConfigName());
        }
        if (result == null) {
            throw new IllegalStateException();
        }
        return result;
    }
    
    public boolean getUseSendfile() {
        return this.useSendfile;
    }
    
    public void setUseSendfile(final boolean useSendfile) {
        this.useSendfile = useSendfile;
    }
    
    public long getExecutorTerminationTimeoutMillis() {
        return this.executorTerminationTimeoutMillis;
    }
    
    public void setExecutorTerminationTimeoutMillis(final long executorTerminationTimeoutMillis) {
        this.executorTerminationTimeoutMillis = executorTerminationTimeoutMillis;
    }
    
    public void setAcceptorThreadCount(final int acceptorThreadCount) {
        this.acceptorThreadCount = acceptorThreadCount;
    }
    
    public int getAcceptorThreadCount() {
        return this.acceptorThreadCount;
    }
    
    public void setAcceptorThreadPriority(final int acceptorThreadPriority) {
        this.acceptorThreadPriority = acceptorThreadPriority;
    }
    
    public int getAcceptorThreadPriority() {
        return this.acceptorThreadPriority;
    }
    
    public void setMaxConnections(final int maxCon) {
        this.maxConnections = maxCon;
        final LimitLatch latch = this.connectionLimitLatch;
        if (latch != null) {
            if (maxCon == -1) {
                this.releaseConnectionLatch();
            }
            else {
                latch.setLimit((long)maxCon);
            }
        }
        else if (maxCon > 0) {
            this.initializeConnectionLatch();
        }
    }
    
    public int getMaxConnections() {
        return this.maxConnections;
    }
    
    public long getConnectionCount() {
        final LimitLatch latch = this.connectionLimitLatch;
        if (latch != null) {
            return latch.getCount();
        }
        return -1L;
    }
    
    public void setExecutor(final Executor executor) {
        this.executor = executor;
        this.internalExecutor = (executor == null);
    }
    
    public Executor getExecutor() {
        return this.executor;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public final int getLocalPort() {
        try {
            final InetSocketAddress localAddress = this.getLocalAddress();
            if (localAddress == null) {
                return -1;
            }
            return localAddress.getPort();
        }
        catch (final IOException ioe) {
            return -1;
        }
    }
    
    public InetAddress getAddress() {
        return this.address;
    }
    
    public void setAddress(final InetAddress address) {
        this.address = address;
    }
    
    protected abstract InetSocketAddress getLocalAddress() throws IOException;
    
    public void setAcceptCount(final int acceptCount) {
        if (acceptCount > 0) {
            this.acceptCount = acceptCount;
        }
    }
    
    public int getAcceptCount() {
        return this.acceptCount;
    }
    
    @Deprecated
    public void setBacklog(final int backlog) {
        this.setAcceptCount(backlog);
    }
    
    @Deprecated
    public int getBacklog() {
        return this.getAcceptCount();
    }
    
    public boolean getBindOnInit() {
        return this.bindOnInit;
    }
    
    public void setBindOnInit(final boolean b) {
        this.bindOnInit = b;
    }
    
    public int getKeepAliveTimeout() {
        if (this.keepAliveTimeout == null) {
            return this.getConnectionTimeout();
        }
        return this.keepAliveTimeout;
    }
    
    public void setKeepAliveTimeout(final int keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }
    
    public boolean getTcpNoDelay() {
        return this.socketProperties.getTcpNoDelay();
    }
    
    public void setTcpNoDelay(final boolean tcpNoDelay) {
        this.socketProperties.setTcpNoDelay(tcpNoDelay);
    }
    
    public int getConnectionLinger() {
        return this.socketProperties.getSoLingerTime();
    }
    
    public void setConnectionLinger(final int connectionLinger) {
        this.socketProperties.setSoLingerTime(connectionLinger);
        this.socketProperties.setSoLingerOn(connectionLinger >= 0);
    }
    
    @Deprecated
    public int getSoLinger() {
        return this.getConnectionLinger();
    }
    
    @Deprecated
    public void setSoLinger(final int soLinger) {
        this.setConnectionLinger(soLinger);
    }
    
    public int getConnectionTimeout() {
        return this.socketProperties.getSoTimeout();
    }
    
    public void setConnectionTimeout(final int soTimeout) {
        this.socketProperties.setSoTimeout(soTimeout);
    }
    
    @Deprecated
    public int getSoTimeout() {
        return this.getConnectionTimeout();
    }
    
    @Deprecated
    public void setSoTimeout(final int soTimeout) {
        this.setConnectionTimeout(soTimeout);
    }
    
    public boolean isSSLEnabled() {
        return this.SSLEnabled;
    }
    
    public void setSSLEnabled(final boolean SSLEnabled) {
        this.SSLEnabled = SSLEnabled;
    }
    
    public abstract boolean isAlpnSupported();
    
    public void setMinSpareThreads(final int minSpareThreads) {
        this.minSpareThreads = minSpareThreads;
        final Executor executor = this.executor;
        if (this.internalExecutor && executor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor)executor).setCorePoolSize(minSpareThreads);
        }
    }
    
    public int getMinSpareThreads() {
        return Math.min(this.getMinSpareThreadsInternal(), this.getMaxThreads());
    }
    
    private int getMinSpareThreadsInternal() {
        if (this.internalExecutor) {
            return this.minSpareThreads;
        }
        return -1;
    }
    
    public void setMaxThreads(final int maxThreads) {
        this.maxThreads = maxThreads;
        final Executor executor = this.executor;
        if (this.internalExecutor && executor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor)executor).setMaximumPoolSize(maxThreads);
        }
    }
    
    public int getMaxThreads() {
        if (this.internalExecutor) {
            return this.maxThreads;
        }
        return -1;
    }
    
    public void setThreadPriority(final int threadPriority) {
        this.threadPriority = threadPriority;
    }
    
    public int getThreadPriority() {
        if (this.internalExecutor) {
            return this.threadPriority;
        }
        return -1;
    }
    
    public int getMaxKeepAliveRequests() {
        return this.maxKeepAliveRequests;
    }
    
    public void setMaxKeepAliveRequests(final int maxKeepAliveRequests) {
        this.maxKeepAliveRequests = maxKeepAliveRequests;
    }
    
    public int getMaxHeaderCount() {
        return this.maxHeaderCount;
    }
    
    public void setMaxHeaderCount(final int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setDomain(final String domain) {
        this.domain = domain;
    }
    
    public String getDomain() {
        return this.domain;
    }
    
    public void setDaemon(final boolean b) {
        this.daemon = b;
    }
    
    public boolean getDaemon() {
        return this.daemon;
    }
    
    public void setUseAsyncIO(final boolean useAsyncIO) {
        this.useAsyncIO = useAsyncIO;
    }
    
    public boolean getUseAsyncIO() {
        return this.useAsyncIO;
    }
    
    protected abstract boolean getDeferAccept();
    
    public void addNegotiatedProtocol(final String negotiableProtocol) {
        this.negotiableProtocols.add(negotiableProtocol);
    }
    
    public boolean hasNegotiableProtocols() {
        return this.negotiableProtocols.size() > 0;
    }
    
    public void setHandler(final Handler<S> handler) {
        this.handler = handler;
    }
    
    public Handler<S> getHandler() {
        return this.handler;
    }
    
    public void setAttribute(final String name, final Object value) {
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)AbstractEndpoint.sm.getString("endpoint.setAttribute", new Object[] { name, value }));
        }
        this.attributes.put(name, value);
    }
    
    public Object getAttribute(final String key) {
        final Object value = this.attributes.get(key);
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)AbstractEndpoint.sm.getString("endpoint.getAttribute", new Object[] { key, value }));
        }
        return value;
    }
    
    public boolean setProperty(final String name, final String value) {
        this.setAttribute(name, value);
        final String socketName = "socket.";
        try {
            if (name.startsWith("socket.")) {
                return IntrospectionUtils.setProperty((Object)this.socketProperties, name.substring("socket.".length()), value);
            }
            return IntrospectionUtils.setProperty((Object)this, name, value, false);
        }
        catch (final Exception x) {
            this.getLog().error((Object)("Unable to set attribute \"" + name + "\" to \"" + value + "\""), (Throwable)x);
            return false;
        }
    }
    
    public String getProperty(final String name) {
        String value = (String)this.getAttribute(name);
        final String socketName = "socket.";
        if (value == null && name.startsWith("socket.")) {
            final Object result = IntrospectionUtils.getProperty((Object)this.socketProperties, name.substring("socket.".length()));
            if (result != null) {
                value = result.toString();
            }
        }
        return value;
    }
    
    public int getCurrentThreadCount() {
        final Executor executor = this.executor;
        if (executor == null) {
            return -2;
        }
        if (executor instanceof org.apache.tomcat.util.threads.ThreadPoolExecutor) {
            return ((org.apache.tomcat.util.threads.ThreadPoolExecutor)executor).getPoolSize();
        }
        if (executor instanceof ResizableExecutor) {
            return ((ResizableExecutor)executor).getPoolSize();
        }
        return -1;
    }
    
    public int getCurrentThreadsBusy() {
        final Executor executor = this.executor;
        if (executor == null) {
            return -2;
        }
        if (executor instanceof org.apache.tomcat.util.threads.ThreadPoolExecutor) {
            return ((org.apache.tomcat.util.threads.ThreadPoolExecutor)executor).getActiveCount();
        }
        if (executor instanceof ResizableExecutor) {
            return ((ResizableExecutor)executor).getActiveCount();
        }
        return -1;
    }
    
    public boolean isRunning() {
        return this.running;
    }
    
    public boolean isPaused() {
        return this.paused;
    }
    
    public void createExecutor() {
        this.internalExecutor = true;
        final TaskQueue taskqueue = new TaskQueue();
        final TaskThreadFactory tf = new TaskThreadFactory(this.getName() + "-exec-", this.daemon, this.getThreadPriority());
        this.executor = (Executor)new org.apache.tomcat.util.threads.ThreadPoolExecutor(this.getMinSpareThreads(), this.getMaxThreads(), 60L, TimeUnit.SECONDS, (BlockingQueue)taskqueue, (ThreadFactory)tf);
        taskqueue.setParent((org.apache.tomcat.util.threads.ThreadPoolExecutor)this.executor);
    }
    
    public void shutdownExecutor() {
        final Executor executor = this.executor;
        if (executor != null && this.internalExecutor) {
            this.executor = null;
            if (executor instanceof org.apache.tomcat.util.threads.ThreadPoolExecutor) {
                final org.apache.tomcat.util.threads.ThreadPoolExecutor tpe = (org.apache.tomcat.util.threads.ThreadPoolExecutor)executor;
                tpe.shutdownNow();
                final long timeout = this.getExecutorTerminationTimeoutMillis();
                if (timeout > 0L) {
                    try {
                        tpe.awaitTermination(timeout, TimeUnit.MILLISECONDS);
                    }
                    catch (final InterruptedException ex) {}
                    if (tpe.isTerminating()) {
                        this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.warn.executorShutdown", new Object[] { this.getName() }));
                    }
                }
                final TaskQueue queue = (TaskQueue)tpe.getQueue();
                queue.setParent((org.apache.tomcat.util.threads.ThreadPoolExecutor)null);
            }
        }
    }
    
    protected void unlockAccept() {
        int unlocksRequired = 0;
        for (final Acceptor acceptor : this.acceptors) {
            if (acceptor.getState() == Acceptor.AcceptorState.RUNNING) {
                ++unlocksRequired;
            }
        }
        if (unlocksRequired == 0) {
            return;
        }
        InetSocketAddress unlockAddress = null;
        InetSocketAddress localAddress = null;
        try {
            localAddress = this.getLocalAddress();
        }
        catch (final IOException ioe) {
            this.getLog().debug((Object)AbstractEndpoint.sm.getString("endpoint.debug.unlock.localFail", new Object[] { this.getName() }), (Throwable)ioe);
        }
        if (localAddress == null) {
            this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.debug.unlock.localNone", new Object[] { this.getName() }));
            return;
        }
        try {
            unlockAddress = getUnlockAddress(localAddress);
            for (int i = 0; i < unlocksRequired; ++i) {
                try (final Socket s = new Socket()) {
                    int stmo = 2000;
                    int utmo = 2000;
                    if (this.getSocketProperties().getSoTimeout() > stmo) {
                        stmo = this.getSocketProperties().getSoTimeout();
                    }
                    if (this.getSocketProperties().getUnlockTimeout() > utmo) {
                        utmo = this.getSocketProperties().getUnlockTimeout();
                    }
                    s.setSoTimeout(stmo);
                    s.setSoLinger(this.getSocketProperties().getSoLingerOn(), this.getSocketProperties().getSoLingerTime());
                    if (this.getLog().isDebugEnabled()) {
                        this.getLog().debug((Object)("About to unlock socket for:" + unlockAddress));
                    }
                    s.connect(unlockAddress, utmo);
                    if (this.getDeferAccept()) {
                        final OutputStreamWriter sw = new OutputStreamWriter(s.getOutputStream(), "ISO-8859-1");
                        sw.write("OPTIONS * HTTP/1.0\r\nUser-Agent: Tomcat wakeup connection\r\n\r\n");
                        sw.flush();
                    }
                    if (this.getLog().isDebugEnabled()) {
                        this.getLog().debug((Object)("Socket unlock completed for:" + unlockAddress));
                    }
                }
            }
            long waitLeft = 1000L;
            for (final Acceptor acceptor2 : this.acceptors) {
                while (waitLeft > 0L && acceptor2.getState() == Acceptor.AcceptorState.RUNNING) {
                    Thread.sleep(5L);
                    waitLeft -= 5L;
                }
            }
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            if (this.getLog().isDebugEnabled()) {
                this.getLog().debug((Object)AbstractEndpoint.sm.getString("endpoint.debug.unlock.fail", new Object[] { "" + this.getPort() }), t);
            }
        }
    }
    
    private static InetSocketAddress getUnlockAddress(final InetSocketAddress localAddress) throws SocketException {
        if (!localAddress.getAddress().isAnyLocalAddress()) {
            return localAddress;
        }
        InetAddress loopbackUnlockAddress = null;
        InetAddress linkLocalUnlockAddress = null;
        final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = networkInterfaces.nextElement();
            final Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                final InetAddress inetAddress = inetAddresses.nextElement();
                if (localAddress.getAddress().getClass().isAssignableFrom(inetAddress.getClass())) {
                    if (inetAddress.isLoopbackAddress()) {
                        if (loopbackUnlockAddress != null) {
                            continue;
                        }
                        loopbackUnlockAddress = inetAddress;
                    }
                    else {
                        if (!inetAddress.isLinkLocalAddress()) {
                            return new InetSocketAddress(inetAddress, localAddress.getPort());
                        }
                        if (linkLocalUnlockAddress != null) {
                            continue;
                        }
                        linkLocalUnlockAddress = inetAddress;
                    }
                }
            }
        }
        if (loopbackUnlockAddress != null) {
            return new InetSocketAddress(loopbackUnlockAddress, localAddress.getPort());
        }
        if (linkLocalUnlockAddress != null) {
            return new InetSocketAddress(linkLocalUnlockAddress, localAddress.getPort());
        }
        return new InetSocketAddress("localhost", localAddress.getPort());
    }
    
    public boolean processSocket(final SocketWrapperBase<S> socketWrapper, final SocketEvent event, final boolean dispatch) {
        try {
            if (socketWrapper == null) {
                return false;
            }
            SocketProcessorBase<S> sc = (SocketProcessorBase<S>)this.processorCache.pop();
            if (sc == null) {
                sc = this.createSocketProcessor(socketWrapper, event);
            }
            else {
                sc.reset(socketWrapper, event);
            }
            final Executor executor = this.getExecutor();
            if (dispatch && executor != null) {
                executor.execute(sc);
            }
            else {
                sc.run();
            }
        }
        catch (final RejectedExecutionException ree) {
            this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.executor.fail", new Object[] { socketWrapper }), (Throwable)ree);
            return false;
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            this.getLog().error((Object)AbstractEndpoint.sm.getString("endpoint.process.fail"), t);
            return false;
        }
        return true;
    }
    
    protected abstract SocketProcessorBase<S> createSocketProcessor(final SocketWrapperBase<S> p0, final SocketEvent p1);
    
    public abstract void bind() throws Exception;
    
    public abstract void unbind() throws Exception;
    
    public abstract void startInternal() throws Exception;
    
    public abstract void stopInternal() throws Exception;
    
    public void init() throws Exception {
        if (this.bindOnInit) {
            this.bind();
            this.bindState = BindState.BOUND_ON_INIT;
        }
        if (this.domain != null) {
            this.oname = new ObjectName(this.domain + ":type=ThreadPool,name=\"" + this.getName() + "\"");
            Registry.getRegistry(null, null).registerComponent(this, this.oname, null);
            final ObjectName socketPropertiesOname = new ObjectName(this.domain + ":type=SocketProperties,name=\"" + this.getName() + "\"");
            this.socketProperties.setObjectName(socketPropertiesOname);
            Registry.getRegistry(null, null).registerComponent(this.socketProperties, socketPropertiesOname, null);
            for (final SSLHostConfig sslHostConfig : this.findSslHostConfigs()) {
                this.registerJmx(sslHostConfig);
            }
        }
    }
    
    private void registerJmx(final SSLHostConfig sslHostConfig) {
        if (this.domain == null) {
            return;
        }
        ObjectName sslOname = null;
        try {
            sslOname = new ObjectName(this.domain + ":type=SSLHostConfig,ThreadPool=\"" + this.getName() + "\",name=" + ObjectName.quote(sslHostConfig.getHostName()));
            sslHostConfig.setObjectName(sslOname);
            try {
                Registry.getRegistry(null, null).registerComponent(sslHostConfig, sslOname, null);
            }
            catch (final Exception e) {
                this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.jmxRegistrationFailed", new Object[] { sslOname }), (Throwable)e);
            }
        }
        catch (final MalformedObjectNameException e2) {
            this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.invalidJmxNameSslHost", new Object[] { sslHostConfig.getHostName() }), (Throwable)e2);
        }
        for (final SSLHostConfigCertificate sslHostConfigCert : sslHostConfig.getCertificates()) {
            ObjectName sslCertOname = null;
            try {
                sslCertOname = new ObjectName(this.domain + ":type=SSLHostConfigCertificate,ThreadPool=\"" + this.getName() + "\",Host=" + ObjectName.quote(sslHostConfig.getHostName()) + ",name=" + sslHostConfigCert.getType());
                sslHostConfigCert.setObjectName(sslCertOname);
                try {
                    Registry.getRegistry(null, null).registerComponent(sslHostConfigCert, sslCertOname, null);
                }
                catch (final Exception e3) {
                    this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.jmxRegistrationFailed", new Object[] { sslCertOname }), (Throwable)e3);
                }
            }
            catch (final MalformedObjectNameException e4) {
                this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.invalidJmxNameSslHostCert", new Object[] { sslHostConfig.getHostName(), sslHostConfigCert.getType() }), (Throwable)e4);
            }
        }
    }
    
    private void unregisterJmx(final SSLHostConfig sslHostConfig) {
        final Registry registry = Registry.getRegistry(null, null);
        registry.unregisterComponent(sslHostConfig.getObjectName());
        for (final SSLHostConfigCertificate sslHostConfigCert : sslHostConfig.getCertificates()) {
            registry.unregisterComponent(sslHostConfigCert.getObjectName());
        }
    }
    
    public final void start() throws Exception {
        if (this.bindState == BindState.UNBOUND) {
            this.bind();
            this.bindState = BindState.BOUND_ON_START;
        }
        this.startInternal();
    }
    
    protected final void startAcceptorThreads() {
        final int count = this.getAcceptorThreadCount();
        this.acceptors = new Acceptor[count];
        for (int i = 0; i < count; ++i) {
            this.acceptors[i] = this.createAcceptor();
            final String threadName = this.getName() + "-Acceptor-" + i;
            this.acceptors[i].setThreadName(threadName);
            final Thread t = new Thread(this.acceptors[i], threadName);
            t.setPriority(this.getAcceptorThreadPriority());
            t.setDaemon(this.getDaemon());
            t.start();
        }
    }
    
    protected abstract Acceptor createAcceptor();
    
    public void pause() {
        if (this.running && !this.paused) {
            this.paused = true;
            this.unlockAccept();
            this.getHandler().pause();
        }
    }
    
    public void resume() {
        if (this.running) {
            this.paused = false;
        }
    }
    
    public final void stop() throws Exception {
        this.stopInternal();
        if (this.bindState == BindState.BOUND_ON_START || this.bindState == BindState.SOCKET_CLOSED_ON_STOP) {
            this.unbind();
            this.bindState = BindState.UNBOUND;
        }
    }
    
    public final void destroy() throws Exception {
        if (this.bindState == BindState.BOUND_ON_INIT) {
            this.unbind();
            this.bindState = BindState.UNBOUND;
        }
        final Registry registry = Registry.getRegistry(null, null);
        registry.unregisterComponent(this.oname);
        registry.unregisterComponent(this.socketProperties.getObjectName());
        for (final SSLHostConfig sslHostConfig : this.findSslHostConfigs()) {
            this.unregisterJmx(sslHostConfig);
        }
    }
    
    protected abstract Log getLog();
    
    protected LimitLatch initializeConnectionLatch() {
        if (this.maxConnections == -1) {
            return null;
        }
        if (this.connectionLimitLatch == null) {
            this.connectionLimitLatch = new LimitLatch((long)this.getMaxConnections());
        }
        return this.connectionLimitLatch;
    }
    
    protected void releaseConnectionLatch() {
        final LimitLatch latch = this.connectionLimitLatch;
        if (latch != null) {
            latch.releaseAll();
        }
        this.connectionLimitLatch = null;
    }
    
    protected void countUpOrAwaitConnection() throws InterruptedException {
        if (this.maxConnections == -1) {
            return;
        }
        final LimitLatch latch = this.connectionLimitLatch;
        if (latch != null) {
            latch.countUpOrAwait();
        }
    }
    
    protected long countDownConnection() {
        if (this.maxConnections == -1) {
            return -1L;
        }
        final LimitLatch latch = this.connectionLimitLatch;
        if (latch != null) {
            final long result = latch.countDown();
            if (result < 0L) {
                this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.warn.incorrectConnectionCount"));
            }
            return result;
        }
        return -1L;
    }
    
    protected int handleExceptionWithDelay(final int currentErrorDelay) {
        if (currentErrorDelay > 0) {
            try {
                Thread.sleep(currentErrorDelay);
            }
            catch (final InterruptedException ex) {}
        }
        if (currentErrorDelay == 0) {
            return 50;
        }
        if (currentErrorDelay < 1600) {
            return currentErrorDelay * 2;
        }
        return 1600;
    }
    
    public final void closeServerSocketGraceful() {
        if (this.bindState == BindState.BOUND_ON_START) {
            this.bindState = BindState.SOCKET_CLOSED_ON_STOP;
            try {
                this.doCloseServerSocket();
            }
            catch (final IOException ioe) {
                this.getLog().warn((Object)AbstractEndpoint.sm.getString("endpoint.serverSocket.closeFailed", new Object[] { this.getName() }), (Throwable)ioe);
            }
        }
    }
    
    protected abstract void doCloseServerSocket() throws IOException;
    
    static {
        sm = StringManager.getManager((Class)AbstractEndpoint.class);
    }
    
    protected enum BindState
    {
        UNBOUND, 
        BOUND_ON_INIT, 
        BOUND_ON_START, 
        SOCKET_CLOSED_ON_STOP;
    }
    
    public abstract static class Acceptor implements Runnable
    {
        protected volatile AcceptorState state;
        private String threadName;
        
        public Acceptor() {
            this.state = AcceptorState.NEW;
        }
        
        public final AcceptorState getState() {
            return this.state;
        }
        
        protected final void setThreadName(final String threadName) {
            this.threadName = threadName;
        }
        
        protected final String getThreadName() {
            return this.threadName;
        }
        
        public enum AcceptorState
        {
            NEW, 
            RUNNING, 
            PAUSED, 
            ENDED;
        }
    }
    
    public interface Handler<S>
    {
        SocketState process(final SocketWrapperBase<S> p0, final SocketEvent p1);
        
        Object getGlobal();
        
        Set<S> getOpenSockets();
        
        void release(final SocketWrapperBase<S> p0);
        
        void pause();
        
        void recycle();
        
        public enum SocketState
        {
            OPEN, 
            CLOSED, 
            LONG, 
            ASYNC_END, 
            SENDFILE, 
            UPGRADING, 
            UPGRADED, 
            SUSPENDED;
        }
    }
}
