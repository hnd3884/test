package org.apache.catalina.tribes.transport.nio;

import org.apache.juli.logging.LogFactory;
import org.apache.catalina.tribes.util.ExceptionUtils;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.util.Iterator;
import java.util.Set;
import java.nio.channels.CancelledKeyException;
import java.sql.Timestamp;
import java.nio.channels.SocketChannel;
import org.apache.catalina.tribes.io.ObjectReader;
import java.nio.channels.SelectionKey;
import java.net.ServerSocket;
import org.apache.catalina.tribes.io.ListenCallback;
import org.apache.catalina.tribes.transport.AbstractRxTask;
import java.io.IOException;
import org.apache.catalina.tribes.transport.RxTaskPool;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Deque;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.Selector;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.catalina.tribes.transport.ReceiverBase;

public class NioReceiver extends ReceiverBase implements Runnable, NioReceiverMBean
{
    private static final Log log;
    protected static final StringManager sm;
    private volatile boolean running;
    private AtomicReference<Selector> selector;
    private ServerSocketChannel serverChannel;
    private DatagramChannel datagramChannel;
    protected final Deque<Runnable> events;
    protected long lastCheck;
    
    public NioReceiver() {
        this.running = false;
        this.selector = new AtomicReference<Selector>();
        this.serverChannel = null;
        this.datagramChannel = null;
        this.events = new ConcurrentLinkedDeque<Runnable>();
        this.lastCheck = System.currentTimeMillis();
    }
    
    @Override
    public void stop() {
        this.stopListening();
        super.stop();
    }
    
    @Override
    public void start() throws IOException {
        super.start();
        try {
            this.setPool(new RxTaskPool(this.getMaxThreads(), this.getMinThreads(), this));
        }
        catch (final Exception x) {
            NioReceiver.log.fatal((Object)NioReceiver.sm.getString("nioReceiver.threadpool.fail"), (Throwable)x);
            if (x instanceof IOException) {
                throw (IOException)x;
            }
            throw new IOException(x.getMessage());
        }
        try {
            this.getBind();
            this.bind();
            String channelName = "";
            if (this.getChannel().getName() != null) {
                channelName = "[" + this.getChannel().getName() + "]";
            }
            final Thread t = new Thread(this, "NioReceiver" + channelName);
            t.setDaemon(true);
            t.start();
        }
        catch (final Exception x) {
            NioReceiver.log.fatal((Object)NioReceiver.sm.getString("nioReceiver.start.fail"), (Throwable)x);
            if (x instanceof IOException) {
                throw (IOException)x;
            }
            throw new IOException(x.getMessage());
        }
    }
    
    @Override
    public AbstractRxTask createRxTask() {
        final NioReplicationTask thread = new NioReplicationTask(this, this);
        thread.setUseBufferPool(this.getUseBufferPool());
        thread.setRxBufSize(this.getRxBufSize());
        thread.setOptions(this.getWorkerThreadOptions());
        return thread;
    }
    
    protected void bind() throws IOException {
        this.serverChannel = ServerSocketChannel.open();
        final ServerSocket serverSocket = this.serverChannel.socket();
        this.selector.set(Selector.open());
        this.bind(serverSocket, this.getPort(), this.getAutoBind());
        this.serverChannel.configureBlocking(false);
        this.serverChannel.register(this.selector.get(), 16);
        if (this.getUdpPort() > 0) {
            this.datagramChannel = DatagramChannel.open();
            this.configureDatagraChannel();
            this.bindUdp(this.datagramChannel.socket(), this.getUdpPort(), this.getAutoBind());
        }
    }
    
    private void configureDatagraChannel() throws IOException {
        this.datagramChannel.configureBlocking(false);
        this.datagramChannel.socket().setSendBufferSize(this.getUdpTxBufSize());
        this.datagramChannel.socket().setReceiveBufferSize(this.getUdpRxBufSize());
        this.datagramChannel.socket().setReuseAddress(this.getSoReuseAddress());
        this.datagramChannel.socket().setSoTimeout(this.getTimeout());
        this.datagramChannel.socket().setTrafficClass(this.getSoTrafficClass());
    }
    
    public void addEvent(final Runnable event) {
        final Selector selector = this.selector.get();
        if (selector != null) {
            this.events.add(event);
            if (NioReceiver.log.isTraceEnabled()) {
                NioReceiver.log.trace((Object)("Adding event to selector:" + event));
            }
            if (this.isListening()) {
                selector.wakeup();
            }
        }
    }
    
    public void events() {
        if (this.events.isEmpty()) {
            return;
        }
        Runnable r = null;
        while ((r = this.events.pollFirst()) != null) {
            try {
                if (NioReceiver.log.isTraceEnabled()) {
                    NioReceiver.log.trace((Object)("Processing event in selector:" + r));
                }
                r.run();
            }
            catch (final Exception x) {
                NioReceiver.log.error((Object)"", (Throwable)x);
            }
        }
    }
    
    public static void cancelledKey(final SelectionKey key) {
        final ObjectReader reader = (ObjectReader)key.attachment();
        if (reader != null) {
            reader.setCancelled(true);
            reader.finish();
        }
        key.cancel();
        key.attach(null);
        if (key.channel() instanceof SocketChannel) {
            try {
                ((SocketChannel)key.channel()).socket().close();
            }
            catch (final IOException e) {
                if (NioReceiver.log.isDebugEnabled()) {
                    NioReceiver.log.debug((Object)"", (Throwable)e);
                }
            }
        }
        if (key.channel() instanceof DatagramChannel) {
            try {
                ((DatagramChannel)key.channel()).socket().close();
            }
            catch (final Exception e2) {
                if (NioReceiver.log.isDebugEnabled()) {
                    NioReceiver.log.debug((Object)"", (Throwable)e2);
                }
            }
        }
        try {
            key.channel().close();
        }
        catch (final IOException e) {
            if (NioReceiver.log.isDebugEnabled()) {
                NioReceiver.log.debug((Object)"", (Throwable)e);
            }
        }
    }
    
    protected void socketTimeouts() {
        final long now = System.currentTimeMillis();
        if (now - this.lastCheck < this.getSelectorTimeout()) {
            return;
        }
        final Selector tmpsel = this.selector.get();
        final Set<SelectionKey> keys = (this.isListening() && tmpsel != null) ? tmpsel.keys() : null;
        if (keys == null) {
            return;
        }
        for (final SelectionKey key : keys) {
            try {
                if (key.interestOps() != 0) {
                    continue;
                }
                final ObjectReader ka = (ObjectReader)key.attachment();
                if (ka != null) {
                    final long delta = now - ka.getLastAccess();
                    if (delta <= this.getTimeout() || ka.isAccessed()) {
                        continue;
                    }
                    if (NioReceiver.log.isWarnEnabled()) {
                        NioReceiver.log.warn((Object)NioReceiver.sm.getString("nioReceiver.threadsExhausted", this.getTimeout(), ka.isCancelled(), key, new Timestamp(ka.getLastAccess())));
                    }
                    ka.setLastAccess(now);
                }
                else {
                    cancelledKey(key);
                }
            }
            catch (final CancelledKeyException ckx) {
                cancelledKey(key);
            }
        }
        this.lastCheck = System.currentTimeMillis();
    }
    
    protected void listen() throws Exception {
        if (this.doListen()) {
            NioReceiver.log.warn((Object)NioReceiver.sm.getString("nioReceiver.alreadyStarted"));
            return;
        }
        this.setListen(true);
        final Selector selector = this.selector.get();
        if (selector != null && this.datagramChannel != null) {
            final ObjectReader oreader = new ObjectReader(65535);
            this.registerChannel(selector, this.datagramChannel, 1, oreader);
        }
        while (this.doListen() && selector != null) {
            try {
                this.events();
                this.socketTimeouts();
                final int n = selector.select(this.getSelectorTimeout());
                if (n == 0) {
                    continue;
                }
                final Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it != null && it.hasNext()) {
                    final SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        final ServerSocketChannel server = (ServerSocketChannel)key.channel();
                        final SocketChannel channel = server.accept();
                        channel.socket().setReceiveBufferSize(this.getRxBufSize());
                        channel.socket().setSendBufferSize(this.getTxBufSize());
                        channel.socket().setTcpNoDelay(this.getTcpNoDelay());
                        channel.socket().setKeepAlive(this.getSoKeepAlive());
                        channel.socket().setOOBInline(this.getOoBInline());
                        channel.socket().setReuseAddress(this.getSoReuseAddress());
                        channel.socket().setSoLinger(this.getSoLingerOn(), this.getSoLingerTime());
                        channel.socket().setSoTimeout(this.getTimeout());
                        final Object attach = new ObjectReader(channel);
                        this.registerChannel(selector, channel, 1, attach);
                    }
                    if (key.isReadable()) {
                        this.readDataFromSocket(key);
                    }
                    else {
                        key.interestOps(key.interestOps() & 0xFFFFFFFB);
                    }
                    it.remove();
                }
            }
            catch (final ClosedSelectorException ex) {}
            catch (final CancelledKeyException nx) {
                NioReceiver.log.warn((Object)NioReceiver.sm.getString("nioReceiver.clientDisconnect"));
            }
            catch (final Throwable t) {
                ExceptionUtils.handleThrowable(t);
                NioReceiver.log.error((Object)NioReceiver.sm.getString("nioReceiver.requestError"), t);
            }
        }
        this.serverChannel.close();
        if (this.datagramChannel != null) {
            try {
                this.datagramChannel.close();
            }
            catch (final Exception iox) {
                if (NioReceiver.log.isDebugEnabled()) {
                    NioReceiver.log.debug((Object)"Unable to close datagram channel.", (Throwable)iox);
                }
            }
            this.datagramChannel = null;
        }
        this.closeSelector();
    }
    
    protected void stopListening() {
        this.setListen(false);
        final Selector selector = this.selector.get();
        if (selector != null) {
            try {
                selector.wakeup();
                for (int count = 0; this.running && count < 50; ++count) {
                    Thread.sleep(100L);
                }
                if (this.running) {
                    NioReceiver.log.warn((Object)NioReceiver.sm.getString("nioReceiver.stop.threadRunning"));
                }
                this.closeSelector();
            }
            catch (final Exception x) {
                NioReceiver.log.error((Object)NioReceiver.sm.getString("nioReceiver.stop.fail"), (Throwable)x);
            }
            finally {
                this.selector.set(null);
            }
        }
    }
    
    private void closeSelector() throws IOException {
        final Selector selector = this.selector.getAndSet(null);
        if (selector == null) {
            return;
        }
        try {
            for (final SelectionKey key : selector.keys()) {
                key.channel().close();
                key.attach(null);
                key.cancel();
            }
        }
        catch (final IOException ignore) {
            if (NioReceiver.log.isWarnEnabled()) {
                NioReceiver.log.warn((Object)NioReceiver.sm.getString("nioReceiver.cleanup.fail"), (Throwable)ignore);
            }
        }
        catch (final ClosedSelectorException ex) {}
        try {
            selector.selectNow();
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        selector.close();
    }
    
    protected void registerChannel(final Selector selector, final SelectableChannel channel, final int ops, final Object attach) throws Exception {
        if (channel == null) {
            return;
        }
        channel.configureBlocking(false);
        channel.register(selector, ops, attach);
    }
    
    @Override
    public void run() {
        this.running = true;
        try {
            this.listen();
        }
        catch (final Exception x) {
            NioReceiver.log.error((Object)NioReceiver.sm.getString("nioReceiver.run.fail"), (Throwable)x);
        }
        finally {
            this.running = false;
        }
    }
    
    protected void readDataFromSocket(final SelectionKey key) throws Exception {
        final NioReplicationTask task = (NioReplicationTask)this.getTaskPool().getRxTask();
        if (task == null) {
            if (NioReceiver.log.isDebugEnabled()) {
                NioReceiver.log.debug((Object)"No TcpReplicationThread available");
            }
        }
        else {
            task.serviceChannel(key);
            this.getExecutor().execute(task);
        }
    }
    
    static {
        log = LogFactory.getLog((Class)NioReceiver.class);
        sm = StringManager.getManager(NioReceiver.class);
    }
}
