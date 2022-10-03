package io.netty.channel.kqueue;

import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.util.concurrent.TimeUnit;
import io.netty.channel.ConnectTimeoutException;
import java.nio.channels.ConnectionPendingException;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import java.nio.channels.NotYetConnectedException;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import java.net.ConnectException;
import io.netty.channel.unix.UnixChannelUtil;
import java.nio.channels.AlreadyConnectedException;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import java.nio.ByteBuffer;
import java.nio.channels.UnresolvedAddressException;
import java.net.InetSocketAddress;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.unix.FileDescriptor;
import java.io.IOException;
import io.netty.channel.ChannelException;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.Channel;
import java.net.SocketAddress;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.unix.UnixChannel;
import io.netty.channel.AbstractChannel;

abstract class AbstractKQueueChannel extends AbstractChannel implements UnixChannel
{
    private static final ChannelMetadata METADATA;
    private ChannelPromise connectPromise;
    private Future<?> connectTimeoutFuture;
    private SocketAddress requestedRemoteAddress;
    final BsdSocket socket;
    private boolean readFilterEnabled;
    private boolean writeFilterEnabled;
    boolean readReadyRunnablePending;
    boolean inputClosedSeenErrorOnRead;
    protected volatile boolean active;
    private volatile SocketAddress local;
    private volatile SocketAddress remote;
    
    AbstractKQueueChannel(final Channel parent, final BsdSocket fd, final boolean active) {
        super(parent);
        this.socket = ObjectUtil.checkNotNull(fd, "fd");
        this.active = active;
        if (active) {
            this.local = fd.localAddress();
            this.remote = fd.remoteAddress();
        }
    }
    
    AbstractKQueueChannel(final Channel parent, final BsdSocket fd, final SocketAddress remote) {
        super(parent);
        this.socket = ObjectUtil.checkNotNull(fd, "fd");
        this.active = true;
        this.remote = remote;
        this.local = fd.localAddress();
    }
    
    static boolean isSoErrorZero(final BsdSocket fd) {
        try {
            return fd.getSoError() == 0;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    @Override
    public final FileDescriptor fd() {
        return this.socket;
    }
    
    @Override
    public boolean isActive() {
        return this.active;
    }
    
    @Override
    public ChannelMetadata metadata() {
        return AbstractKQueueChannel.METADATA;
    }
    
    @Override
    protected void doClose() throws Exception {
        this.active = false;
        this.inputClosedSeenErrorOnRead = true;
        this.socket.close();
    }
    
    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }
    
    void resetCachedAddresses() {
        this.local = this.socket.localAddress();
        this.remote = this.socket.remoteAddress();
    }
    
    @Override
    protected boolean isCompatible(final EventLoop loop) {
        return loop instanceof KQueueEventLoop;
    }
    
    @Override
    public boolean isOpen() {
        return this.socket.isOpen();
    }
    
    @Override
    protected void doDeregister() throws Exception {
        ((KQueueEventLoop)this.eventLoop()).remove(this);
        this.readFilterEnabled = false;
        this.writeFilterEnabled = false;
    }
    
    void unregisterFilters() throws Exception {
        this.readFilter(false);
        this.writeFilter(false);
        this.evSet0(Native.EVFILT_SOCK, Native.EV_DELETE, 0);
    }
    
    @Override
    protected final void doBeginRead() throws Exception {
        final AbstractKQueueUnsafe unsafe = (AbstractKQueueUnsafe)this.unsafe();
        this.readFilter(unsafe.readPending = true);
        if (unsafe.maybeMoreDataToRead) {
            unsafe.executeReadReadyRunnable(this.config());
        }
    }
    
    @Override
    protected void doRegister() throws Exception {
        this.readReadyRunnablePending = false;
        ((KQueueEventLoop)this.eventLoop()).add(this);
        if (this.writeFilterEnabled) {
            this.evSet0(Native.EVFILT_WRITE, Native.EV_ADD_CLEAR_ENABLE);
        }
        if (this.readFilterEnabled) {
            this.evSet0(Native.EVFILT_READ, Native.EV_ADD_CLEAR_ENABLE);
        }
        this.evSet0(Native.EVFILT_SOCK, Native.EV_ADD, Native.NOTE_RDHUP);
    }
    
    @Override
    protected abstract AbstractKQueueUnsafe newUnsafe();
    
    @Override
    public abstract KQueueChannelConfig config();
    
    protected final ByteBuf newDirectBuffer(final ByteBuf buf) {
        return this.newDirectBuffer(buf, buf);
    }
    
    protected final ByteBuf newDirectBuffer(final Object holder, final ByteBuf buf) {
        final int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            ReferenceCountUtil.release(holder);
            return Unpooled.EMPTY_BUFFER;
        }
        final ByteBufAllocator alloc = this.alloc();
        if (alloc.isDirectBufferPooled()) {
            return newDirectBuffer0(holder, buf, alloc, readableBytes);
        }
        final ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
        if (directBuf == null) {
            return newDirectBuffer0(holder, buf, alloc, readableBytes);
        }
        directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
        ReferenceCountUtil.safeRelease(holder);
        return directBuf;
    }
    
    private static ByteBuf newDirectBuffer0(final Object holder, final ByteBuf buf, final ByteBufAllocator alloc, final int capacity) {
        final ByteBuf directBuf = alloc.directBuffer(capacity);
        directBuf.writeBytes(buf, buf.readerIndex(), capacity);
        ReferenceCountUtil.safeRelease(holder);
        return directBuf;
    }
    
    protected static void checkResolvable(final InetSocketAddress addr) {
        if (addr.isUnresolved()) {
            throw new UnresolvedAddressException();
        }
    }
    
    protected final int doReadBytes(final ByteBuf byteBuf) throws Exception {
        final int writerIndex = byteBuf.writerIndex();
        this.unsafe().recvBufAllocHandle().attemptedBytesRead(byteBuf.writableBytes());
        int localReadAmount;
        if (byteBuf.hasMemoryAddress()) {
            localReadAmount = this.socket.readAddress(byteBuf.memoryAddress(), writerIndex, byteBuf.capacity());
        }
        else {
            final ByteBuffer buf = byteBuf.internalNioBuffer(writerIndex, byteBuf.writableBytes());
            localReadAmount = this.socket.read(buf, buf.position(), buf.limit());
        }
        if (localReadAmount > 0) {
            byteBuf.writerIndex(writerIndex + localReadAmount);
        }
        return localReadAmount;
    }
    
    protected final int doWriteBytes(final ChannelOutboundBuffer in, final ByteBuf buf) throws Exception {
        if (buf.hasMemoryAddress()) {
            final int localFlushedAmount = this.socket.writeAddress(buf.memoryAddress(), buf.readerIndex(), buf.writerIndex());
            if (localFlushedAmount > 0) {
                in.removeBytes(localFlushedAmount);
                return 1;
            }
        }
        else {
            final ByteBuffer nioBuf = (buf.nioBufferCount() == 1) ? buf.internalNioBuffer(buf.readerIndex(), buf.readableBytes()) : buf.nioBuffer();
            final int localFlushedAmount2 = this.socket.write(nioBuf, nioBuf.position(), nioBuf.limit());
            if (localFlushedAmount2 > 0) {
                nioBuf.position(nioBuf.position() + localFlushedAmount2);
                in.removeBytes(localFlushedAmount2);
                return 1;
            }
        }
        return Integer.MAX_VALUE;
    }
    
    final boolean shouldBreakReadReady(final ChannelConfig config) {
        return this.socket.isInputShutdown() && (this.inputClosedSeenErrorOnRead || !isAllowHalfClosure(config));
    }
    
    private static boolean isAllowHalfClosure(final ChannelConfig config) {
        if (config instanceof KQueueDomainSocketChannelConfig) {
            return ((KQueueDomainSocketChannelConfig)config).isAllowHalfClosure();
        }
        return config instanceof SocketChannelConfig && ((SocketChannelConfig)config).isAllowHalfClosure();
    }
    
    final void clearReadFilter() {
        if (this.isRegistered()) {
            final EventLoop loop = this.eventLoop();
            final AbstractKQueueUnsafe unsafe = (AbstractKQueueUnsafe)this.unsafe();
            if (loop.inEventLoop()) {
                unsafe.clearReadFilter0();
            }
            else {
                loop.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!unsafe.readPending && !AbstractKQueueChannel.this.config().isAutoRead()) {
                            unsafe.clearReadFilter0();
                        }
                    }
                });
            }
        }
        else {
            this.readFilterEnabled = false;
        }
    }
    
    void readFilter(final boolean readFilterEnabled) throws IOException {
        if (this.readFilterEnabled != readFilterEnabled) {
            this.readFilterEnabled = readFilterEnabled;
            this.evSet(Native.EVFILT_READ, readFilterEnabled ? Native.EV_ADD_CLEAR_ENABLE : Native.EV_DELETE_DISABLE);
        }
    }
    
    void writeFilter(final boolean writeFilterEnabled) throws IOException {
        if (this.writeFilterEnabled != writeFilterEnabled) {
            this.writeFilterEnabled = writeFilterEnabled;
            this.evSet(Native.EVFILT_WRITE, writeFilterEnabled ? Native.EV_ADD_CLEAR_ENABLE : Native.EV_DELETE_DISABLE);
        }
    }
    
    private void evSet(final short filter, final short flags) {
        if (this.isRegistered()) {
            this.evSet0(filter, flags);
        }
    }
    
    private void evSet0(final short filter, final short flags) {
        this.evSet0(filter, flags, 0);
    }
    
    private void evSet0(final short filter, final short flags, final int fflags) {
        if (this.isOpen()) {
            ((KQueueEventLoop)this.eventLoop()).evSet(this, filter, flags, fflags);
        }
    }
    
    @Override
    protected void doBind(final SocketAddress local) throws Exception {
        if (local instanceof InetSocketAddress) {
            checkResolvable((InetSocketAddress)local);
        }
        this.socket.bind(local);
        this.local = this.socket.localAddress();
    }
    
    protected boolean doConnect(final SocketAddress remoteAddress, final SocketAddress localAddress) throws Exception {
        if (localAddress instanceof InetSocketAddress) {
            checkResolvable((InetSocketAddress)localAddress);
        }
        final InetSocketAddress remoteSocketAddr = (remoteAddress instanceof InetSocketAddress) ? ((InetSocketAddress)remoteAddress) : null;
        if (remoteSocketAddr != null) {
            checkResolvable(remoteSocketAddr);
        }
        if (this.remote != null) {
            throw new AlreadyConnectedException();
        }
        if (localAddress != null) {
            this.socket.bind(localAddress);
        }
        final boolean connected = this.doConnect0(remoteAddress, localAddress);
        if (connected) {
            this.remote = ((remoteSocketAddr == null) ? remoteAddress : UnixChannelUtil.computeRemoteAddr(remoteSocketAddr, this.socket.remoteAddress()));
        }
        this.local = this.socket.localAddress();
        return connected;
    }
    
    protected boolean doConnect0(final SocketAddress remoteAddress, final SocketAddress localAddress) throws Exception {
        boolean success = false;
        try {
            final boolean connected = this.socket.connect(remoteAddress);
            if (!connected) {
                this.writeFilter(true);
            }
            success = true;
            return connected;
        }
        finally {
            if (!success) {
                this.doClose();
            }
        }
    }
    
    @Override
    protected SocketAddress localAddress0() {
        return this.local;
    }
    
    @Override
    protected SocketAddress remoteAddress0() {
        return this.remote;
    }
    
    static {
        METADATA = new ChannelMetadata(false);
    }
    
    abstract class AbstractKQueueUnsafe extends AbstractUnsafe
    {
        boolean readPending;
        boolean maybeMoreDataToRead;
        private KQueueRecvByteAllocatorHandle allocHandle;
        private final Runnable readReadyRunnable;
        
        AbstractKQueueUnsafe() {
            this.readReadyRunnable = new Runnable() {
                @Override
                public void run() {
                    AbstractKQueueChannel.this.readReadyRunnablePending = false;
                    AbstractKQueueUnsafe.this.readReady(AbstractKQueueUnsafe.this.recvBufAllocHandle());
                }
            };
        }
        
        final void readReady(final long numberBytesPending) {
            final KQueueRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            allocHandle.numberBytesPending(numberBytesPending);
            this.readReady(allocHandle);
        }
        
        abstract void readReady(final KQueueRecvByteAllocatorHandle p0);
        
        final void readReadyBefore() {
            this.maybeMoreDataToRead = false;
        }
        
        final void readReadyFinally(final ChannelConfig config) {
            this.maybeMoreDataToRead = this.allocHandle.maybeMoreDataToRead();
            if (this.allocHandle.isReadEOF() || (this.readPending && this.maybeMoreDataToRead)) {
                this.executeReadReadyRunnable(config);
            }
            else if (!this.readPending && !config.isAutoRead()) {
                this.clearReadFilter0();
            }
        }
        
        final boolean failConnectPromise(final Throwable cause) {
            if (AbstractKQueueChannel.this.connectPromise != null) {
                final ChannelPromise connectPromise = AbstractKQueueChannel.this.connectPromise;
                AbstractKQueueChannel.this.connectPromise = null;
                if (connectPromise.tryFailure((cause instanceof ConnectException) ? cause : new ConnectException("failed to connect").initCause(cause))) {
                    this.closeIfClosed();
                    return true;
                }
            }
            return false;
        }
        
        final void writeReady() {
            if (AbstractKQueueChannel.this.connectPromise != null) {
                this.finishConnect();
            }
            else if (!AbstractKQueueChannel.this.socket.isOutputShutdown()) {
                super.flush0();
            }
        }
        
        void shutdownInput(final boolean readEOF) {
            if (readEOF && AbstractKQueueChannel.this.connectPromise != null) {
                this.finishConnect();
            }
            if (!AbstractKQueueChannel.this.socket.isInputShutdown()) {
                if (isAllowHalfClosure(AbstractKQueueChannel.this.config())) {
                    try {
                        AbstractKQueueChannel.this.socket.shutdown(true, false);
                    }
                    catch (final IOException ignored) {
                        this.fireEventAndClose(ChannelInputShutdownEvent.INSTANCE);
                        return;
                    }
                    catch (final NotYetConnectedException ex) {}
                    AbstractKQueueChannel.this.pipeline().fireUserEventTriggered((Object)ChannelInputShutdownEvent.INSTANCE);
                }
                else {
                    this.close(this.voidPromise());
                }
            }
            else if (!readEOF) {
                AbstractKQueueChannel.this.inputClosedSeenErrorOnRead = true;
                AbstractKQueueChannel.this.pipeline().fireUserEventTriggered((Object)ChannelInputShutdownReadComplete.INSTANCE);
            }
        }
        
        final void readEOF() {
            final KQueueRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            allocHandle.readEOF();
            if (AbstractKQueueChannel.this.isActive()) {
                this.readReady(allocHandle);
            }
            else {
                this.shutdownInput(true);
            }
        }
        
        @Override
        public KQueueRecvByteAllocatorHandle recvBufAllocHandle() {
            if (this.allocHandle == null) {
                this.allocHandle = new KQueueRecvByteAllocatorHandle((RecvByteBufAllocator.ExtendedHandle)super.recvBufAllocHandle());
            }
            return this.allocHandle;
        }
        
        @Override
        protected final void flush0() {
            if (!AbstractKQueueChannel.this.writeFilterEnabled) {
                super.flush0();
            }
        }
        
        final void executeReadReadyRunnable(final ChannelConfig config) {
            if (AbstractKQueueChannel.this.readReadyRunnablePending || !AbstractKQueueChannel.this.isActive() || AbstractKQueueChannel.this.shouldBreakReadReady(config)) {
                return;
            }
            AbstractKQueueChannel.this.readReadyRunnablePending = true;
            AbstractKQueueChannel.this.eventLoop().execute(this.readReadyRunnable);
        }
        
        protected final void clearReadFilter0() {
            assert AbstractKQueueChannel.this.eventLoop().inEventLoop();
            try {
                this.readPending = false;
                AbstractKQueueChannel.this.readFilter(false);
            }
            catch (final IOException e) {
                AbstractKQueueChannel.this.pipeline().fireExceptionCaught((Throwable)e);
                AbstractKQueueChannel.this.unsafe().close(AbstractKQueueChannel.this.unsafe().voidPromise());
            }
        }
        
        private void fireEventAndClose(final Object evt) {
            AbstractKQueueChannel.this.pipeline().fireUserEventTriggered(evt);
            this.close(this.voidPromise());
        }
        
        @Override
        public void connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
            if (!promise.setUncancellable() || !this.ensureOpen(promise)) {
                return;
            }
            try {
                if (AbstractKQueueChannel.this.connectPromise != null) {
                    throw new ConnectionPendingException();
                }
                final boolean wasActive = AbstractKQueueChannel.this.isActive();
                if (AbstractKQueueChannel.this.doConnect(remoteAddress, localAddress)) {
                    this.fulfillConnectPromise(promise, wasActive);
                }
                else {
                    AbstractKQueueChannel.this.connectPromise = promise;
                    AbstractKQueueChannel.this.requestedRemoteAddress = remoteAddress;
                    final int connectTimeoutMillis = AbstractKQueueChannel.this.config().getConnectTimeoutMillis();
                    if (connectTimeoutMillis > 0) {
                        AbstractKQueueChannel.this.connectTimeoutFuture = AbstractKQueueChannel.this.eventLoop().schedule((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                final ChannelPromise connectPromise = AbstractKQueueChannel.this.connectPromise;
                                if (connectPromise != null && !connectPromise.isDone() && connectPromise.tryFailure(new ConnectTimeoutException("connection timed out: " + remoteAddress))) {
                                    AbstractKQueueUnsafe.this.close(AbstractKQueueUnsafe.this.voidPromise());
                                }
                            }
                        }, (long)connectTimeoutMillis, TimeUnit.MILLISECONDS);
                    }
                    promise.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                        @Override
                        public void operationComplete(final ChannelFuture future) throws Exception {
                            if (future.isCancelled()) {
                                if (AbstractKQueueChannel.this.connectTimeoutFuture != null) {
                                    AbstractKQueueChannel.this.connectTimeoutFuture.cancel(false);
                                }
                                AbstractKQueueChannel.this.connectPromise = null;
                                AbstractKQueueUnsafe.this.close(AbstractKQueueUnsafe.this.voidPromise());
                            }
                        }
                    });
                }
            }
            catch (final Throwable t) {
                this.closeIfClosed();
                promise.tryFailure(this.annotateConnectException(t, remoteAddress));
            }
        }
        
        private void fulfillConnectPromise(final ChannelPromise promise, final boolean wasActive) {
            if (promise == null) {
                return;
            }
            AbstractKQueueChannel.this.active = true;
            final boolean active = AbstractKQueueChannel.this.isActive();
            final boolean promiseSet = promise.trySuccess();
            if (!wasActive && active) {
                AbstractKQueueChannel.this.pipeline().fireChannelActive();
            }
            if (!promiseSet) {
                this.close(this.voidPromise());
            }
        }
        
        private void fulfillConnectPromise(final ChannelPromise promise, final Throwable cause) {
            if (promise == null) {
                return;
            }
            promise.tryFailure(cause);
            this.closeIfClosed();
        }
        
        private void finishConnect() {
            assert AbstractKQueueChannel.this.eventLoop().inEventLoop();
            boolean connectStillInProgress = false;
            try {
                final boolean wasActive = AbstractKQueueChannel.this.isActive();
                if (!this.doFinishConnect()) {
                    connectStillInProgress = true;
                    return;
                }
                this.fulfillConnectPromise(AbstractKQueueChannel.this.connectPromise, wasActive);
            }
            catch (final Throwable t) {
                this.fulfillConnectPromise(AbstractKQueueChannel.this.connectPromise, this.annotateConnectException(t, AbstractKQueueChannel.this.requestedRemoteAddress));
            }
            finally {
                if (!connectStillInProgress) {
                    if (AbstractKQueueChannel.this.connectTimeoutFuture != null) {
                        AbstractKQueueChannel.this.connectTimeoutFuture.cancel(false);
                    }
                    AbstractKQueueChannel.this.connectPromise = null;
                }
            }
        }
        
        private boolean doFinishConnect() throws Exception {
            if (AbstractKQueueChannel.this.socket.finishConnect()) {
                AbstractKQueueChannel.this.writeFilter(false);
                if (AbstractKQueueChannel.this.requestedRemoteAddress instanceof InetSocketAddress) {
                    AbstractKQueueChannel.this.remote = UnixChannelUtil.computeRemoteAddr((InetSocketAddress)AbstractKQueueChannel.this.requestedRemoteAddress, AbstractKQueueChannel.this.socket.remoteAddress());
                }
                AbstractKQueueChannel.this.requestedRemoteAddress = null;
                return true;
            }
            AbstractKQueueChannel.this.writeFilter(true);
            return false;
        }
    }
}
