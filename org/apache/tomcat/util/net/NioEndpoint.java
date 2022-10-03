package org.apache.tomcat.util.net;

import java.nio.channels.FileChannel;
import java.util.concurrent.Semaphore;
import java.nio.channels.CompletionHandler;
import javax.net.ssl.SSLEngine;
import org.apache.tomcat.util.net.jsse.JSSESupport;
import java.net.InetAddress;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.ConcurrentModificationException;
import java.net.SocketTimeoutException;
import java.nio.channels.WritableByteChannel;
import java.io.FileInputStream;
import java.io.File;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.tomcat.util.collections.SynchronizedQueue;
import java.nio.channels.Selector;
import java.nio.channels.CancelledKeyException;
import org.apache.juli.logging.LogFactory;
import java.nio.channels.SelectionKey;
import java.nio.channels.NetworkChannel;
import java.net.Socket;
import org.apache.tomcat.util.ExceptionUtils;
import java.nio.channels.SocketChannel;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.nio.channels.Channel;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.tomcat.util.IntrospectionUtils;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.util.collections.SynchronizedStack;
import java.util.concurrent.CountDownLatch;
import java.nio.channels.ServerSocketChannel;
import org.apache.juli.logging.Log;

public class NioEndpoint extends AbstractJsseEndpoint<NioChannel>
{
    private static final Log log;
    public static final int OP_REGISTER = 256;
    private NioSelectorPool selectorPool;
    private volatile ServerSocketChannel serverSock;
    private volatile CountDownLatch stopLatch;
    private SynchronizedStack<PollerEvent> eventCache;
    private SynchronizedStack<NioChannel> nioChannels;
    private boolean useInheritedChannel;
    private int pollerThreadPriority;
    private int pollerThreadCount;
    private long selectorTimeout;
    private Poller[] pollers;
    private AtomicInteger pollerRotater;
    
    public NioEndpoint() {
        this.selectorPool = new NioSelectorPool();
        this.serverSock = null;
        this.stopLatch = null;
        this.useInheritedChannel = false;
        this.pollerThreadPriority = 5;
        this.pollerThreadCount = Math.min(2, Runtime.getRuntime().availableProcessors());
        this.selectorTimeout = 1000L;
        this.pollers = null;
        this.pollerRotater = new AtomicInteger(0);
    }
    
    @Override
    public boolean setProperty(final String name, final String value) {
        final String selectorPoolName = "selectorPool.";
        try {
            if (name.startsWith("selectorPool.")) {
                return IntrospectionUtils.setProperty((Object)this.selectorPool, name.substring("selectorPool.".length()), value);
            }
            return super.setProperty(name, value);
        }
        catch (final Exception x) {
            NioEndpoint.log.error((Object)("Unable to set attribute \"" + name + "\" to \"" + value + "\""), (Throwable)x);
            return false;
        }
    }
    
    public void setUseInheritedChannel(final boolean useInheritedChannel) {
        this.useInheritedChannel = useInheritedChannel;
    }
    
    public boolean getUseInheritedChannel() {
        return this.useInheritedChannel;
    }
    
    public void setPollerThreadPriority(final int pollerThreadPriority) {
        this.pollerThreadPriority = pollerThreadPriority;
    }
    
    public int getPollerThreadPriority() {
        return this.pollerThreadPriority;
    }
    
    public void setPollerThreadCount(final int pollerThreadCount) {
        this.pollerThreadCount = pollerThreadCount;
    }
    
    public int getPollerThreadCount() {
        return this.pollerThreadCount;
    }
    
    public void setSelectorTimeout(final long timeout) {
        this.selectorTimeout = timeout;
    }
    
    public long getSelectorTimeout() {
        return this.selectorTimeout;
    }
    
    public Poller getPoller0() {
        final int idx = Math.abs(this.pollerRotater.incrementAndGet()) % this.pollers.length;
        return this.pollers[idx];
    }
    
    public void setSelectorPool(final NioSelectorPool selectorPool) {
        this.selectorPool = selectorPool;
    }
    
    public void setSocketProperties(final SocketProperties socketProperties) {
        this.socketProperties = socketProperties;
    }
    
    public boolean getDeferAccept() {
        return false;
    }
    
    public int getKeepAliveCount() {
        if (this.pollers == null) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < this.pollers.length; ++i) {
            sum += this.pollers[i].getKeyCount();
        }
        return sum;
    }
    
    @Override
    public void bind() throws Exception {
        if (!this.getUseInheritedChannel()) {
            this.serverSock = ServerSocketChannel.open();
            this.socketProperties.setProperties(this.serverSock.socket());
            final InetSocketAddress addr = (this.getAddress() != null) ? new InetSocketAddress(this.getAddress(), this.getPort()) : new InetSocketAddress(this.getPort());
            this.serverSock.socket().bind(addr, this.getAcceptCount());
        }
        else {
            final Channel ic = System.inheritedChannel();
            if (ic instanceof ServerSocketChannel) {
                this.serverSock = (ServerSocketChannel)ic;
            }
            if (this.serverSock == null) {
                throw new IllegalArgumentException(NioEndpoint.sm.getString("endpoint.init.bind.inherited"));
            }
        }
        this.serverSock.configureBlocking(true);
        if (this.acceptorThreadCount == 0) {
            this.acceptorThreadCount = 1;
        }
        if (this.pollerThreadCount <= 0) {
            this.pollerThreadCount = 1;
        }
        this.setStopLatch(new CountDownLatch(this.pollerThreadCount));
        this.initialiseSsl();
        this.selectorPool.open();
    }
    
    @Override
    public void startInternal() throws Exception {
        if (!this.running) {
            this.running = true;
            this.paused = false;
            this.processorCache = (SynchronizedStack<SocketProcessorBase<S>>)new SynchronizedStack(128, this.socketProperties.getProcessorCache());
            this.eventCache = (SynchronizedStack<PollerEvent>)new SynchronizedStack(128, this.socketProperties.getEventCache());
            this.nioChannels = (SynchronizedStack<NioChannel>)new SynchronizedStack(128, this.socketProperties.getBufferPool());
            if (this.getExecutor() == null) {
                this.createExecutor();
            }
            this.initializeConnectionLatch();
            this.pollers = new Poller[this.getPollerThreadCount()];
            for (int i = 0; i < this.pollers.length; ++i) {
                this.pollers[i] = new Poller();
                final Thread pollerThread = new Thread(this.pollers[i], this.getName() + "-ClientPoller-" + i);
                pollerThread.setPriority(this.threadPriority);
                pollerThread.setDaemon(true);
                pollerThread.start();
            }
            this.startAcceptorThreads();
        }
    }
    
    @Override
    public void stopInternal() {
        this.releaseConnectionLatch();
        if (!this.paused) {
            this.pause();
        }
        if (this.running) {
            this.running = false;
            this.unlockAccept();
            for (int i = 0; this.pollers != null && i < this.pollers.length; ++i) {
                if (this.pollers[i] != null) {
                    this.pollers[i].destroy();
                    this.pollers[i] = null;
                }
            }
            try {
                if (!this.getStopLatch().await(this.selectorTimeout + 100L, TimeUnit.MILLISECONDS)) {
                    NioEndpoint.log.warn((Object)NioEndpoint.sm.getString("endpoint.nio.stopLatchAwaitFail"));
                }
            }
            catch (final InterruptedException e) {
                NioEndpoint.log.warn((Object)NioEndpoint.sm.getString("endpoint.nio.stopLatchAwaitInterrupted"), (Throwable)e);
            }
            this.shutdownExecutor();
            this.eventCache.clear();
            this.nioChannels.clear();
            this.processorCache.clear();
        }
    }
    
    @Override
    public void unbind() throws Exception {
        if (NioEndpoint.log.isDebugEnabled()) {
            NioEndpoint.log.debug((Object)("Destroy initiated for " + new InetSocketAddress(this.getAddress(), this.getPort())));
        }
        if (this.running) {
            this.stop();
        }
        try {
            this.doCloseServerSocket();
        }
        catch (final IOException ioe) {
            this.getLog().warn((Object)NioEndpoint.sm.getString("endpoint.serverSocket.closeFailed", new Object[] { this.getName() }), (Throwable)ioe);
        }
        this.destroySsl();
        super.unbind();
        if (this.getHandler() != null) {
            this.getHandler().recycle();
        }
        this.selectorPool.close();
        if (NioEndpoint.log.isDebugEnabled()) {
            NioEndpoint.log.debug((Object)("Destroy completed for " + new InetSocketAddress(this.getAddress(), this.getPort())));
        }
    }
    
    @Override
    protected void doCloseServerSocket() throws IOException {
        if (!this.getUseInheritedChannel() && this.serverSock != null) {
            this.serverSock.socket().close();
            this.serverSock.close();
        }
        this.serverSock = null;
    }
    
    public int getWriteBufSize() {
        return this.socketProperties.getTxBufSize();
    }
    
    public int getReadBufSize() {
        return this.socketProperties.getRxBufSize();
    }
    
    public NioSelectorPool getSelectorPool() {
        return this.selectorPool;
    }
    
    @Override
    protected AbstractEndpoint.Acceptor createAcceptor() {
        return new Acceptor();
    }
    
    protected CountDownLatch getStopLatch() {
        return this.stopLatch;
    }
    
    protected void setStopLatch(final CountDownLatch stopLatch) {
        this.stopLatch = stopLatch;
    }
    
    protected boolean setSocketOptions(final SocketChannel socket) {
        try {
            socket.configureBlocking(false);
            final Socket sock = socket.socket();
            this.socketProperties.setProperties(sock);
            NioChannel channel = (NioChannel)this.nioChannels.pop();
            if (channel == null) {
                final SocketBufferHandler bufhandler = new SocketBufferHandler(this.socketProperties.getAppReadBufSize(), this.socketProperties.getAppWriteBufSize(), this.socketProperties.getDirectBuffer());
                if (this.isSSLEnabled()) {
                    channel = new SecureNioChannel(socket, bufhandler, this.selectorPool, this);
                }
                else {
                    channel = new NioChannel(socket, bufhandler);
                }
            }
            else {
                channel.setIOChannel(socket);
                channel.reset();
            }
            this.getPoller0().register(channel);
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
            try {
                NioEndpoint.log.error((Object)"", t);
            }
            catch (final Throwable tt) {
                ExceptionUtils.handleThrowable(tt);
            }
            return false;
        }
        return true;
    }
    
    @Override
    protected Log getLog() {
        return NioEndpoint.log;
    }
    
    @Override
    protected NetworkChannel getServerSocket() {
        return this.serverSock;
    }
    
    @Override
    protected SocketProcessorBase<NioChannel> createSocketProcessor(final SocketWrapperBase<NioChannel> socketWrapper, final SocketEvent event) {
        return new SocketProcessor(socketWrapper, event);
    }
    
    private void close(final NioChannel socket, final SelectionKey key) {
        try {
            if (socket.getPoller().cancelledKey(key) != null) {
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.debug((Object)("Socket: [" + socket + "] closed"));
                }
                if (this.running && !this.paused && !this.nioChannels.push((Object)socket)) {
                    socket.free();
                }
            }
        }
        catch (final Exception x) {
            NioEndpoint.log.error((Object)"", (Throwable)x);
        }
    }
    
    static {
        log = LogFactory.getLog((Class)NioEndpoint.class);
    }
    
    protected class Acceptor extends AbstractEndpoint.Acceptor
    {
        @Override
        public void run() {
            int errorDelay = 0;
            while (NioEndpoint.this.running) {
                while (NioEndpoint.this.paused && NioEndpoint.this.running) {
                    this.state = AcceptorState.PAUSED;
                    try {
                        Thread.sleep(50L);
                    }
                    catch (final InterruptedException ex) {}
                }
                if (!NioEndpoint.this.running) {
                    break;
                }
                this.state = AcceptorState.RUNNING;
                try {
                    NioEndpoint.this.countUpOrAwaitConnection();
                    SocketChannel socket = null;
                    try {
                        socket = NioEndpoint.this.serverSock.accept();
                    }
                    catch (final IOException ioe) {
                        NioEndpoint.this.countDownConnection();
                        if (NioEndpoint.this.running) {
                            errorDelay = NioEndpoint.this.handleExceptionWithDelay(errorDelay);
                            throw ioe;
                        }
                        break;
                    }
                    errorDelay = 0;
                    if (NioEndpoint.this.running && !NioEndpoint.this.paused) {
                        if (NioEndpoint.this.setSocketOptions(socket)) {
                            continue;
                        }
                        this.closeSocket(socket);
                    }
                    else {
                        this.closeSocket(socket);
                    }
                }
                catch (final Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    NioEndpoint.log.error((Object)AbstractEndpoint.sm.getString("endpoint.accept.fail"), t);
                }
            }
            this.state = AcceptorState.ENDED;
        }
        
        private void closeSocket(final SocketChannel socket) {
            NioEndpoint.this.countDownConnection();
            try {
                socket.socket().close();
            }
            catch (final IOException ioe) {
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.debug((Object)AbstractEndpoint.sm.getString("endpoint.err.close"), (Throwable)ioe);
                }
            }
            try {
                socket.close();
            }
            catch (final IOException ioe) {
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.debug((Object)AbstractEndpoint.sm.getString("endpoint.err.close"), (Throwable)ioe);
                }
            }
        }
    }
    
    public static class PollerEvent implements Runnable
    {
        private NioChannel socket;
        private int interestOps;
        private NioSocketWrapper socketWrapper;
        
        public PollerEvent(final NioChannel ch, final NioSocketWrapper w, final int intOps) {
            this.reset(ch, w, intOps);
        }
        
        public void reset(final NioChannel ch, final NioSocketWrapper w, final int intOps) {
            this.socket = ch;
            this.interestOps = intOps;
            this.socketWrapper = w;
        }
        
        public void reset() {
            this.reset(null, null, 0);
        }
        
        @Override
        public void run() {
            if (this.interestOps == 256) {
                try {
                    this.socket.getIOChannel().register(this.socket.getPoller().getSelector(), 1, this.socketWrapper);
                }
                catch (final Exception x) {
                    NioEndpoint.log.error((Object)AbstractEndpoint.sm.getString("endpoint.nio.registerFail"), (Throwable)x);
                }
            }
            else {
                final SelectionKey key = this.socket.getIOChannel().keyFor(this.socket.getPoller().getSelector());
                try {
                    if (key == null) {
                        this.socket.socketWrapper.getEndpoint().countDownConnection();
                        ((NioSocketWrapper)this.socket.socketWrapper).closed = true;
                    }
                    else {
                        final NioSocketWrapper socketWrapper = (NioSocketWrapper)key.attachment();
                        if (socketWrapper != null) {
                            final int ops = key.interestOps() | this.interestOps;
                            socketWrapper.interestOps(ops);
                            key.interestOps(ops);
                        }
                        else {
                            this.socket.getPoller().cancelledKey(key);
                        }
                    }
                }
                catch (final CancelledKeyException ckx) {
                    try {
                        this.socket.getPoller().cancelledKey(key);
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        
        @Override
        public String toString() {
            return "Poller event: socket [" + this.socket + "], socketWrapper [" + this.socketWrapper + "], interestOps [" + this.interestOps + "]";
        }
    }
    
    public class Poller implements Runnable
    {
        private Selector selector;
        private final SynchronizedQueue<PollerEvent> events;
        private volatile boolean close;
        private long nextExpiration;
        private AtomicLong wakeupCounter;
        private volatile int keyCount;
        
        public Poller() throws IOException {
            this.events = (SynchronizedQueue<PollerEvent>)new SynchronizedQueue();
            this.close = false;
            this.nextExpiration = 0L;
            this.wakeupCounter = new AtomicLong(0L);
            this.keyCount = 0;
            this.selector = Selector.open();
        }
        
        public int getKeyCount() {
            return this.keyCount;
        }
        
        public Selector getSelector() {
            return this.selector;
        }
        
        protected void destroy() {
            this.close = true;
            this.selector.wakeup();
        }
        
        private void addEvent(final PollerEvent event) {
            this.events.offer((Object)event);
            if (this.wakeupCounter.incrementAndGet() == 0L) {
                this.selector.wakeup();
            }
        }
        
        public void add(final NioChannel socket, final int interestOps) {
            PollerEvent r = (PollerEvent)NioEndpoint.this.eventCache.pop();
            if (r == null) {
                r = new PollerEvent(socket, null, interestOps);
            }
            else {
                r.reset(socket, null, interestOps);
            }
            this.addEvent(r);
            if (this.close) {
                final NioSocketWrapper ka = (NioSocketWrapper)socket.getAttachment();
                NioEndpoint.this.processSocket(ka, SocketEvent.STOP, false);
            }
        }
        
        public boolean events() {
            boolean result = false;
            PollerEvent pe = null;
            for (int i = 0, size = this.events.size(); i < size && (pe = (PollerEvent)this.events.poll()) != null; ++i) {
                result = true;
                try {
                    pe.run();
                    pe.reset();
                    if (NioEndpoint.this.running && !NioEndpoint.this.paused) {
                        NioEndpoint.this.eventCache.push((Object)pe);
                    }
                }
                catch (final Throwable x) {
                    NioEndpoint.log.error((Object)"", x);
                }
            }
            return result;
        }
        
        public void register(final NioChannel socket) {
            socket.setPoller(this);
            final NioSocketWrapper ka = new NioSocketWrapper(socket, NioEndpoint.this);
            socket.setSocketWrapper(ka);
            ka.setPoller(this);
            ka.setReadTimeout(NioEndpoint.this.getSocketProperties().getSoTimeout());
            ka.setWriteTimeout(NioEndpoint.this.getSocketProperties().getSoTimeout());
            ka.setKeepAliveLeft(NioEndpoint.this.getMaxKeepAliveRequests());
            ka.setReadTimeout(NioEndpoint.this.getConnectionTimeout());
            ka.setWriteTimeout(NioEndpoint.this.getConnectionTimeout());
            PollerEvent r = (PollerEvent)NioEndpoint.this.eventCache.pop();
            ka.interestOps(1);
            if (r == null) {
                r = new PollerEvent(socket, ka, 256);
            }
            else {
                r.reset(socket, ka, 256);
            }
            this.addEvent(r);
        }
        
        public NioSocketWrapper cancelledKey(final SelectionKey key) {
            NioSocketWrapper ka = null;
            try {
                if (key == null) {
                    return null;
                }
                ka = (NioSocketWrapper)key.attach(null);
                if (ka != null) {
                    NioEndpoint.this.getHandler().release(ka);
                }
                if (key.isValid()) {
                    key.cancel();
                }
                if (ka != null) {
                    try {
                        ka.getSocket().close(true);
                    }
                    catch (final Exception e) {
                        if (NioEndpoint.log.isDebugEnabled()) {
                            NioEndpoint.log.debug((Object)AbstractEndpoint.sm.getString("endpoint.debug.socketCloseFail"), (Throwable)e);
                        }
                    }
                }
                if (key.channel().isOpen()) {
                    try {
                        key.channel().close();
                    }
                    catch (final Exception e) {
                        if (NioEndpoint.log.isDebugEnabled()) {
                            NioEndpoint.log.debug((Object)AbstractEndpoint.sm.getString("endpoint.debug.channelCloseFail"), (Throwable)e);
                        }
                    }
                }
                try {
                    if (ka != null && ka.getSendfileData() != null && ka.getSendfileData().fchannel != null && ka.getSendfileData().fchannel.isOpen()) {
                        ka.getSendfileData().fchannel.close();
                    }
                }
                catch (final Exception ex) {}
                if (ka != null) {
                    NioEndpoint.this.countDownConnection();
                    ka.closed = true;
                }
            }
            catch (final Throwable e2) {
                ExceptionUtils.handleThrowable(e2);
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.error((Object)"", e2);
                }
            }
            return ka;
        }
        
        @Override
        public void run() {
            while (true) {
                boolean hasEvents = false;
                try {
                    if (!this.close) {
                        hasEvents = this.events();
                        if (this.wakeupCounter.getAndSet(-1L) > 0L) {
                            this.keyCount = this.selector.selectNow();
                        }
                        else {
                            this.keyCount = this.selector.select(NioEndpoint.this.selectorTimeout);
                        }
                        this.wakeupCounter.set(0L);
                    }
                    if (this.close) {
                        this.events();
                        this.timeout(0, false);
                        try {
                            this.selector.close();
                        }
                        catch (final IOException ioe) {
                            NioEndpoint.log.error((Object)AbstractEndpoint.sm.getString("endpoint.nio.selectorCloseFail"), (Throwable)ioe);
                        }
                        break;
                    }
                }
                catch (final Throwable x) {
                    ExceptionUtils.handleThrowable(x);
                    NioEndpoint.log.error((Object)"", x);
                    continue;
                }
                if (this.keyCount == 0) {
                    hasEvents |= this.events();
                }
                final Iterator<SelectionKey> iterator = (this.keyCount > 0) ? this.selector.selectedKeys().iterator() : null;
                while (iterator != null && iterator.hasNext()) {
                    final SelectionKey sk = iterator.next();
                    iterator.remove();
                    final NioSocketWrapper socketWrapper = (NioSocketWrapper)sk.attachment();
                    if (socketWrapper != null) {
                        this.processKey(sk, socketWrapper);
                    }
                }
                this.timeout(this.keyCount, hasEvents);
            }
            NioEndpoint.this.getStopLatch().countDown();
        }
        
        protected void processKey(final SelectionKey sk, final NioSocketWrapper attachment) {
            try {
                if (this.close) {
                    this.cancelledKey(sk);
                }
                else if (sk.isValid() && attachment != null) {
                    if (sk.isReadable() || sk.isWritable()) {
                        if (attachment.getSendfileData() != null) {
                            this.processSendfile(sk, attachment, false);
                        }
                        else {
                            this.unreg(sk, attachment, sk.readyOps());
                            boolean closeSocket = false;
                            if (sk.isReadable() && !NioEndpoint.this.processSocket(attachment, SocketEvent.OPEN_READ, true)) {
                                closeSocket = true;
                            }
                            if (!closeSocket && sk.isWritable() && !NioEndpoint.this.processSocket(attachment, SocketEvent.OPEN_WRITE, true)) {
                                closeSocket = true;
                            }
                            if (closeSocket) {
                                this.cancelledKey(sk);
                            }
                        }
                    }
                }
                else {
                    this.cancelledKey(sk);
                }
            }
            catch (final CancelledKeyException ckx) {
                this.cancelledKey(sk);
            }
            catch (final Throwable t) {
                ExceptionUtils.handleThrowable(t);
                NioEndpoint.log.error((Object)"", t);
            }
        }
        
        public SendfileState processSendfile(final SelectionKey sk, final NioSocketWrapper socketWrapper, final boolean calledByProcessor) {
            NioChannel sc = null;
            try {
                this.unreg(sk, socketWrapper, sk.readyOps());
                final SendfileData sd = socketWrapper.getSendfileData();
                if (NioEndpoint.log.isTraceEnabled()) {
                    NioEndpoint.log.trace((Object)("Processing send file for: " + sd.fileName));
                }
                if (sd.fchannel == null) {
                    final File f = new File(sd.fileName);
                    final FileInputStream fis = new FileInputStream(f);
                    sd.fchannel = fis.getChannel();
                }
                sc = socketWrapper.getSocket();
                final WritableByteChannel wc = (sc instanceof SecureNioChannel) ? sc : sc.getIOChannel();
                if (sc.getOutboundRemaining() > 0) {
                    if (sc.flushOutbound()) {
                        socketWrapper.updateLastWrite();
                    }
                }
                else {
                    final long written = sd.fchannel.transferTo(sd.pos, sd.length, wc);
                    if (written > 0L) {
                        final SendfileData sendfileData = sd;
                        sendfileData.pos += written;
                        final SendfileData sendfileData2 = sd;
                        sendfileData2.length -= written;
                        socketWrapper.updateLastWrite();
                    }
                    else if (sd.fchannel.size() <= sd.pos) {
                        throw new IOException("Sendfile configured to send more data than was available");
                    }
                }
                if (sd.length <= 0L && sc.getOutboundRemaining() <= 0) {
                    if (NioEndpoint.log.isDebugEnabled()) {
                        NioEndpoint.log.debug((Object)("Send file complete for: " + sd.fileName));
                    }
                    socketWrapper.setSendfileData(null);
                    try {
                        sd.fchannel.close();
                    }
                    catch (final Exception ex) {}
                    if (!calledByProcessor) {
                        switch (sd.keepAliveState) {
                            case NONE: {
                                if (NioEndpoint.log.isDebugEnabled()) {
                                    NioEndpoint.log.debug((Object)"Send file connection is being closed");
                                }
                                NioEndpoint.this.close(sc, sk);
                                break;
                            }
                            case PIPELINED: {
                                if (NioEndpoint.log.isDebugEnabled()) {
                                    NioEndpoint.log.debug((Object)"Connection is keep alive, processing pipe-lined data");
                                }
                                if (!NioEndpoint.this.processSocket(socketWrapper, SocketEvent.OPEN_READ, true)) {
                                    NioEndpoint.this.close(sc, sk);
                                    break;
                                }
                                break;
                            }
                            case OPEN: {
                                if (NioEndpoint.log.isDebugEnabled()) {
                                    NioEndpoint.log.debug((Object)"Connection is keep alive, registering back for OP_READ");
                                }
                                this.reg(sk, socketWrapper, 1);
                                break;
                            }
                        }
                    }
                    return SendfileState.DONE;
                }
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.debug((Object)("OP_WRITE for sendfile: " + sd.fileName));
                }
                if (calledByProcessor) {
                    this.add(socketWrapper.getSocket(), 4);
                }
                else {
                    this.reg(sk, socketWrapper, 4);
                }
                return SendfileState.PENDING;
            }
            catch (final IOException x) {
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.debug((Object)"Unable to complete sendfile request:", (Throwable)x);
                }
                if (!calledByProcessor && sc != null) {
                    NioEndpoint.this.close(sc, sk);
                }
                return SendfileState.ERROR;
            }
            catch (final Throwable t) {
                NioEndpoint.log.error((Object)"", t);
                if (!calledByProcessor && sc != null) {
                    NioEndpoint.this.close(sc, sk);
                }
                return SendfileState.ERROR;
            }
        }
        
        protected void unreg(final SelectionKey sk, final NioSocketWrapper socketWrapper, final int readyOps) {
            this.reg(sk, socketWrapper, sk.interestOps() & ~readyOps);
        }
        
        protected void reg(final SelectionKey sk, final NioSocketWrapper socketWrapper, final int intops) {
            sk.interestOps(intops);
            socketWrapper.interestOps(intops);
        }
        
        protected void timeout(final int keyCount, final boolean hasEvents) {
            final long now = System.currentTimeMillis();
            if (this.nextExpiration > 0L && (keyCount > 0 || hasEvents) && now < this.nextExpiration && !this.close) {
                return;
            }
            int keycount = 0;
            try {
                for (final SelectionKey key : this.selector.keys()) {
                    ++keycount;
                    try {
                        final NioSocketWrapper ka = (NioSocketWrapper)key.attachment();
                        if (ka == null) {
                            this.cancelledKey(key);
                        }
                        else if (this.close) {
                            key.interestOps(0);
                            ka.interestOps(0);
                            this.processKey(key, ka);
                        }
                        else {
                            if ((ka.interestOps() & 0x1) != 0x1 && (ka.interestOps() & 0x4) != 0x4) {
                                continue;
                            }
                            boolean isTimedOut = false;
                            if ((ka.interestOps() & 0x1) == 0x1) {
                                final long delta = now - ka.getLastRead();
                                final long timeout = ka.getReadTimeout();
                                isTimedOut = (timeout > 0L && delta > timeout);
                            }
                            if (!isTimedOut && (ka.interestOps() & 0x4) == 0x4) {
                                final long delta = now - ka.getLastWrite();
                                final long timeout = ka.getWriteTimeout();
                                isTimedOut = (timeout > 0L && delta > timeout);
                            }
                            if (!isTimedOut) {
                                continue;
                            }
                            key.interestOps(0);
                            ka.interestOps(0);
                            ka.setError(new SocketTimeoutException());
                            if (NioEndpoint.this.processSocket(ka, SocketEvent.ERROR, true)) {
                                continue;
                            }
                            this.cancelledKey(key);
                        }
                    }
                    catch (final CancelledKeyException ckx) {
                        this.cancelledKey(key);
                    }
                }
            }
            catch (final ConcurrentModificationException cme) {
                NioEndpoint.log.warn((Object)AbstractEndpoint.sm.getString("endpoint.nio.timeoutCme"), (Throwable)cme);
            }
            final long prevExp = this.nextExpiration;
            this.nextExpiration = System.currentTimeMillis() + NioEndpoint.this.socketProperties.getTimeoutInterval();
            if (NioEndpoint.log.isTraceEnabled()) {
                NioEndpoint.log.trace((Object)("timeout completed: keys processed=" + keycount + "; now=" + now + "; nextExpiration=" + prevExp + "; keyCount=" + keyCount + "; hasEvents=" + hasEvents + "; eval=" + (now < prevExp && (keyCount > 0 || hasEvents) && !this.close)));
            }
        }
    }
    
    public static class NioSocketWrapper extends SocketWrapperBase<NioChannel>
    {
        private final NioSelectorPool pool;
        private Poller poller;
        private int interestOps;
        private CountDownLatch readLatch;
        private CountDownLatch writeLatch;
        private volatile SendfileData sendfileData;
        private volatile long lastRead;
        private volatile long lastWrite;
        private volatile boolean closed;
        
        public NioSocketWrapper(final NioChannel channel, final NioEndpoint endpoint) {
            super(channel, endpoint);
            this.poller = null;
            this.interestOps = 0;
            this.readLatch = null;
            this.writeLatch = null;
            this.sendfileData = null;
            this.lastRead = System.currentTimeMillis();
            this.lastWrite = this.lastRead;
            this.closed = false;
            this.pool = endpoint.getSelectorPool();
            this.socketBufferHandler = channel.getBufHandler();
        }
        
        public Poller getPoller() {
            return this.poller;
        }
        
        public void setPoller(final Poller poller) {
            this.poller = poller;
        }
        
        public int interestOps() {
            return this.interestOps;
        }
        
        public int interestOps(final int ops) {
            return this.interestOps = ops;
        }
        
        public CountDownLatch getReadLatch() {
            return this.readLatch;
        }
        
        public CountDownLatch getWriteLatch() {
            return this.writeLatch;
        }
        
        protected CountDownLatch resetLatch(final CountDownLatch latch) {
            if (latch == null || latch.getCount() == 0L) {
                return null;
            }
            throw new IllegalStateException("Latch must be at count 0");
        }
        
        public void resetReadLatch() {
            this.readLatch = this.resetLatch(this.readLatch);
        }
        
        public void resetWriteLatch() {
            this.writeLatch = this.resetLatch(this.writeLatch);
        }
        
        protected CountDownLatch startLatch(final CountDownLatch latch, final int cnt) {
            if (latch == null || latch.getCount() == 0L) {
                return new CountDownLatch(cnt);
            }
            throw new IllegalStateException("Latch must be at count 0 or null.");
        }
        
        public void startReadLatch(final int cnt) {
            this.readLatch = this.startLatch(this.readLatch, cnt);
        }
        
        public void startWriteLatch(final int cnt) {
            this.writeLatch = this.startLatch(this.writeLatch, cnt);
        }
        
        protected void awaitLatch(final CountDownLatch latch, final long timeout, final TimeUnit unit) throws InterruptedException {
            if (latch == null) {
                throw new IllegalStateException("Latch cannot be null");
            }
            latch.await(timeout, unit);
        }
        
        public void awaitReadLatch(final long timeout, final TimeUnit unit) throws InterruptedException {
            this.awaitLatch(this.readLatch, timeout, unit);
        }
        
        public void awaitWriteLatch(final long timeout, final TimeUnit unit) throws InterruptedException {
            this.awaitLatch(this.writeLatch, timeout, unit);
        }
        
        public void setSendfileData(final SendfileData sf) {
            this.sendfileData = sf;
        }
        
        public SendfileData getSendfileData() {
            return this.sendfileData;
        }
        
        public void updateLastWrite() {
            this.lastWrite = System.currentTimeMillis();
        }
        
        public long getLastWrite() {
            return this.lastWrite;
        }
        
        public void updateLastRead() {
            this.lastRead = System.currentTimeMillis();
        }
        
        public long getLastRead() {
            return this.lastRead;
        }
        
        @Override
        public boolean isReadyForRead() throws IOException {
            this.socketBufferHandler.configureReadBufferForRead();
            if (this.socketBufferHandler.getReadBuffer().remaining() > 0) {
                return true;
            }
            this.fillReadBuffer(false);
            final boolean isReady = this.socketBufferHandler.getReadBuffer().position() > 0;
            return isReady;
        }
        
        @Override
        public int read(final boolean block, final byte[] b, final int off, final int len) throws IOException {
            int nRead = this.populateReadBuffer(b, off, len);
            if (nRead > 0) {
                return nRead;
            }
            nRead = this.fillReadBuffer(block);
            this.updateLastRead();
            if (nRead > 0) {
                this.socketBufferHandler.configureReadBufferForRead();
                nRead = Math.min(nRead, len);
                this.socketBufferHandler.getReadBuffer().get(b, off, nRead);
            }
            return nRead;
        }
        
        @Override
        public int read(final boolean block, final ByteBuffer to) throws IOException {
            int nRead = this.populateReadBuffer(to);
            if (nRead > 0) {
                return nRead;
            }
            final int limit = this.socketBufferHandler.getReadBuffer().capacity();
            if (to.remaining() >= limit) {
                to.limit(to.position() + limit);
                nRead = this.fillReadBuffer(block, to);
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.debug((Object)("Socket: [" + this + "], Read direct from socket: [" + nRead + "]"));
                }
                this.updateLastRead();
            }
            else {
                nRead = this.fillReadBuffer(block);
                if (NioEndpoint.log.isDebugEnabled()) {
                    NioEndpoint.log.debug((Object)("Socket: [" + this + "], Read into buffer: [" + nRead + "]"));
                }
                this.updateLastRead();
                if (nRead > 0) {
                    nRead = this.populateReadBuffer(to);
                }
            }
            return nRead;
        }
        
        @Override
        public void close() throws IOException {
            this.getSocket().close();
            this.getEndpoint().getHandler().release(this);
        }
        
        @Override
        public boolean isClosed() {
            return this.closed;
        }
        
        private int fillReadBuffer(final boolean block) throws IOException {
            this.socketBufferHandler.configureReadBufferForWrite();
            return this.fillReadBuffer(block, this.socketBufferHandler.getReadBuffer());
        }
        
        private int fillReadBuffer(final boolean block, final ByteBuffer to) throws IOException {
            final NioChannel channel = this.getSocket();
            int nRead;
            if (block) {
                Selector selector = null;
                try {
                    selector = this.pool.get();
                }
                catch (final IOException ex) {}
                try {
                    final NioSocketWrapper att = (NioSocketWrapper)channel.getAttachment();
                    if (att == null) {
                        throw new IOException("Key must be cancelled.");
                    }
                    nRead = this.pool.read(to, channel, selector, att.getReadTimeout());
                }
                finally {
                    if (selector != null) {
                        this.pool.put(selector);
                    }
                }
            }
            else {
                nRead = channel.read(to);
                if (nRead == -1) {
                    throw new EOFException();
                }
            }
            return nRead;
        }
        
        @Override
        protected void doWrite(final boolean block, final ByteBuffer from) throws IOException {
            final long writeTimeout = this.getWriteTimeout();
            Selector selector = null;
            try {
                selector = this.pool.get();
            }
            catch (final IOException ex) {}
            try {
                this.pool.write(from, this.getSocket(), selector, writeTimeout, block);
                if (block) {
                    while (!this.getSocket().flush(true, selector, writeTimeout)) {}
                }
                this.updateLastWrite();
            }
            finally {
                if (selector != null) {
                    this.pool.put(selector);
                }
            }
        }
        
        @Override
        public void registerReadInterest() {
            if (NioEndpoint.log.isDebugEnabled()) {
                NioEndpoint.log.debug((Object)NioSocketWrapper.sm.getString("endpoint.debug.registerRead", new Object[] { this }));
            }
            this.getPoller().add(this.getSocket(), 1);
        }
        
        @Override
        public void registerWriteInterest() {
            if (NioEndpoint.log.isDebugEnabled()) {
                NioEndpoint.log.debug((Object)NioSocketWrapper.sm.getString("endpoint.debug.registerWrite", new Object[] { this }));
            }
            this.getPoller().add(this.getSocket(), 4);
        }
        
        @Override
        public SendfileDataBase createSendfileData(final String filename, final long pos, final long length) {
            return new SendfileData(filename, pos, length);
        }
        
        @Override
        public SendfileState processSendfile(final SendfileDataBase sendfileData) {
            this.setSendfileData((SendfileData)sendfileData);
            final SelectionKey key = this.getSocket().getIOChannel().keyFor(this.getSocket().getPoller().getSelector());
            return this.getSocket().getPoller().processSendfile(key, this, true);
        }
        
        @Override
        protected void populateRemoteAddr() {
            final InetAddress inetAddr = this.getSocket().getIOChannel().socket().getInetAddress();
            if (inetAddr != null) {
                this.remoteAddr = inetAddr.getHostAddress();
            }
        }
        
        @Override
        protected void populateRemoteHost() {
            final InetAddress inetAddr = this.getSocket().getIOChannel().socket().getInetAddress();
            if (inetAddr != null) {
                this.remoteHost = inetAddr.getHostName();
                if (this.remoteAddr == null) {
                    this.remoteAddr = inetAddr.getHostAddress();
                }
            }
        }
        
        @Override
        protected void populateRemotePort() {
            this.remotePort = this.getSocket().getIOChannel().socket().getPort();
        }
        
        @Override
        protected void populateLocalName() {
            final InetAddress inetAddr = this.getSocket().getIOChannel().socket().getLocalAddress();
            if (inetAddr != null) {
                this.localName = inetAddr.getHostName();
            }
        }
        
        @Override
        protected void populateLocalAddr() {
            final InetAddress inetAddr = this.getSocket().getIOChannel().socket().getLocalAddress();
            if (inetAddr != null) {
                this.localAddr = inetAddr.getHostAddress();
            }
        }
        
        @Override
        protected void populateLocalPort() {
            this.localPort = this.getSocket().getIOChannel().socket().getLocalPort();
        }
        
        @Override
        public SSLSupport getSslSupport(final String clientCertProvider) {
            if (this.getSocket() instanceof SecureNioChannel) {
                final SecureNioChannel ch = ((SocketWrapperBase<SecureNioChannel>)this).getSocket();
                return ch.getSSLSupport();
            }
            return null;
        }
        
        @Override
        public void doClientAuth(final SSLSupport sslSupport) throws IOException {
            final SecureNioChannel sslChannel = ((SocketWrapperBase<SecureNioChannel>)this).getSocket();
            final SSLEngine engine = sslChannel.getSslEngine();
            if (!engine.getNeedClientAuth()) {
                engine.setNeedClientAuth(true);
                sslChannel.rehandshake(this.getEndpoint().getConnectionTimeout());
                ((JSSESupport)sslSupport).setSession(engine.getSession());
            }
        }
        
        @Override
        public void setAppReadBufHandler(final ApplicationBufferHandler handler) {
            this.getSocket().setAppReadBufHandler(handler);
        }
        
        @Override
        protected <A> OperationState<A> newOperationState(final boolean read, final ByteBuffer[] buffers, final int offset, final int length, final BlockingMode block, final long timeout, final TimeUnit unit, final A attachment, final CompletionCheck check, final CompletionHandler<Long, ? super A> handler, final Semaphore semaphore, final VectoredIOCompletionHandler<A> completion) {
            return new NioOperationState<A>(read, buffers, offset, length, block, timeout, unit, (Object)attachment, check, (CompletionHandler)handler, semaphore, (VectoredIOCompletionHandler)completion);
        }
        
        private class NioOperationState<A> extends OperationState<A>
        {
            private volatile boolean inline;
            
            private NioOperationState(final boolean read, final ByteBuffer[] buffers, final int offset, final int length, final BlockingMode block, final long timeout, final TimeUnit unit, final A attachment, final CompletionCheck check, final CompletionHandler<Long, ? super A> handler, final Semaphore semaphore, final VectoredIOCompletionHandler<A> completion) {
                super(read, buffers, offset, length, block, timeout, unit, attachment, check, handler, semaphore, completion);
                this.inline = true;
            }
            
            @Override
            protected boolean isInline() {
                return this.inline;
            }
            
            @Override
            public void run() {
                long nBytes = 0L;
                if (NioSocketWrapper.this.getError() == null) {
                    try {
                        synchronized (this) {
                            if (!this.completionDone) {
                                if (NioEndpoint.log.isDebugEnabled()) {
                                    NioEndpoint.log.debug((Object)("Skip concurrent " + (this.read ? "read" : "write") + " notification"));
                                }
                                return;
                            }
                            if (this.read) {
                                if (!NioSocketWrapper.this.socketBufferHandler.isReadBufferEmpty()) {
                                    NioSocketWrapper.this.socketBufferHandler.configureReadBufferForRead();
                                    for (int i = 0; i < this.length && !NioSocketWrapper.this.socketBufferHandler.isReadBufferEmpty(); ++i) {
                                        nBytes += SocketWrapperBase.transfer(NioSocketWrapper.this.socketBufferHandler.getReadBuffer(), this.buffers[this.offset + i]);
                                    }
                                }
                                if (nBytes == 0L) {
                                    nBytes = NioSocketWrapper.this.getSocket().read(this.buffers, this.offset, this.length);
                                    NioSocketWrapper.this.updateLastRead();
                                }
                            }
                            else {
                                boolean doWrite = true;
                                if (!NioSocketWrapper.this.socketBufferHandler.isWriteBufferEmpty()) {
                                    NioSocketWrapper.this.socketBufferHandler.configureWriteBufferForRead();
                                    do {
                                        nBytes = NioSocketWrapper.this.getSocket().write(NioSocketWrapper.this.socketBufferHandler.getWriteBuffer());
                                    } while (!NioSocketWrapper.this.socketBufferHandler.isWriteBufferEmpty() && nBytes > 0L);
                                    if (!NioSocketWrapper.this.socketBufferHandler.isWriteBufferEmpty()) {
                                        doWrite = false;
                                    }
                                    if (nBytes > 0L) {
                                        nBytes = 0L;
                                    }
                                }
                                if (doWrite) {
                                    long n = 0L;
                                    do {
                                        n = NioSocketWrapper.this.getSocket().write(this.buffers, this.offset, this.length);
                                        if (n == -1L) {
                                            nBytes = n;
                                        }
                                        else {
                                            nBytes += n;
                                        }
                                    } while (n > 0L);
                                    NioSocketWrapper.this.updateLastWrite();
                                }
                            }
                            if (nBytes != 0L || !SocketWrapperBase.buffersArrayHasRemaining(this.buffers, this.offset, this.length)) {
                                this.completionDone = false;
                            }
                        }
                    }
                    catch (final IOException e) {
                        NioSocketWrapper.this.setError(e);
                    }
                }
                if (nBytes > 0L || (nBytes == 0L && !SocketWrapperBase.buffersArrayHasRemaining(this.buffers, this.offset, this.length))) {
                    this.completion.completed(Long.valueOf(nBytes), (OperationState<A>)this);
                }
                else if (nBytes < 0L || NioSocketWrapper.this.getError() != null) {
                    IOException error = NioSocketWrapper.this.getError();
                    if (error == null) {
                        error = new EOFException();
                    }
                    this.completion.failed((Throwable)error, (OperationState<A>)this);
                }
                else {
                    this.inline = false;
                    if (this.read) {
                        NioSocketWrapper.this.registerReadInterest();
                    }
                    else {
                        NioSocketWrapper.this.registerWriteInterest();
                    }
                }
            }
        }
    }
    
    protected class SocketProcessor extends SocketProcessorBase<NioChannel>
    {
        public SocketProcessor(final SocketWrapperBase<NioChannel> socketWrapper, final SocketEvent event) {
            super(socketWrapper, event);
        }
        
        @Override
        protected void doRun() {
            final NioChannel socket = (NioChannel)this.socketWrapper.getSocket();
            final SelectionKey key = socket.getIOChannel().keyFor(socket.getPoller().getSelector());
            try {
                int handshake = -1;
                try {
                    if (key != null) {
                        if (socket.isHandshakeComplete()) {
                            handshake = 0;
                        }
                        else if (this.event == SocketEvent.STOP || this.event == SocketEvent.DISCONNECT || this.event == SocketEvent.ERROR) {
                            handshake = -1;
                        }
                        else {
                            handshake = socket.handshake(key.isReadable(), key.isWritable());
                            this.event = SocketEvent.OPEN_READ;
                        }
                    }
                }
                catch (final IOException x) {
                    handshake = -1;
                    if (NioEndpoint.log.isDebugEnabled()) {
                        NioEndpoint.log.debug((Object)"Error during SSL handshake", (Throwable)x);
                    }
                }
                catch (final CancelledKeyException ckx) {
                    handshake = -1;
                }
                if (handshake == 0) {
                    Handler.SocketState state = Handler.SocketState.OPEN;
                    if (this.event == null) {
                        state = NioEndpoint.this.getHandler().process((SocketWrapperBase<NioChannel>)this.socketWrapper, SocketEvent.OPEN_READ);
                    }
                    else {
                        state = NioEndpoint.this.getHandler().process((SocketWrapperBase<NioChannel>)this.socketWrapper, this.event);
                    }
                    if (state == Handler.SocketState.CLOSED) {
                        NioEndpoint.this.close(socket, key);
                    }
                }
                else if (handshake == -1) {
                    NioEndpoint.this.getHandler().process((SocketWrapperBase<NioChannel>)this.socketWrapper, SocketEvent.CONNECT_FAIL);
                    NioEndpoint.this.close(socket, key);
                }
                else if (handshake == 1) {
                    this.socketWrapper.registerReadInterest();
                }
                else if (handshake == 4) {
                    this.socketWrapper.registerWriteInterest();
                }
            }
            catch (final CancelledKeyException cx) {
                socket.getPoller().cancelledKey(key);
            }
            catch (final VirtualMachineError vme) {
                ExceptionUtils.handleThrowable((Throwable)vme);
            }
            catch (final Throwable t) {
                NioEndpoint.log.error((Object)"", t);
                socket.getPoller().cancelledKey(key);
            }
            finally {
                this.socketWrapper = null;
                this.event = null;
                if (NioEndpoint.this.running && !NioEndpoint.this.paused) {
                    NioEndpoint.this.processorCache.push((Object)this);
                }
            }
        }
    }
    
    public static class SendfileData extends SendfileDataBase
    {
        protected volatile FileChannel fchannel;
        
        public SendfileData(final String filename, final long pos, final long length) {
            super(filename, pos, length);
        }
    }
}
