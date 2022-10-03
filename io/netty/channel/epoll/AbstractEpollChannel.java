package io.netty.channel.epoll;

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
import io.netty.channel.unix.UnixChannelUtil;
import java.nio.channels.AlreadyConnectedException;
import io.netty.channel.unix.IovArray;
import io.netty.channel.ChannelOutboundBuffer;
import java.nio.ByteBuffer;
import java.nio.channels.UnresolvedAddressException;
import java.net.InetSocketAddress;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.ChannelConfig;
import io.netty.channel.EventLoop;
import java.nio.channels.ClosedChannelException;
import io.netty.channel.unix.FileDescriptor;
import java.io.IOException;
import io.netty.channel.ChannelException;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.Channel;
import java.net.SocketAddress;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.unix.UnixChannel;
import io.netty.channel.AbstractChannel;

abstract class AbstractEpollChannel extends AbstractChannel implements UnixChannel
{
    private static final ChannelMetadata METADATA;
    final LinuxSocket socket;
    private ChannelPromise connectPromise;
    private Future<?> connectTimeoutFuture;
    private SocketAddress requestedRemoteAddress;
    private volatile SocketAddress local;
    private volatile SocketAddress remote;
    protected int flags;
    boolean inputClosedSeenErrorOnRead;
    boolean epollInReadyRunnablePending;
    protected volatile boolean active;
    
    AbstractEpollChannel(final LinuxSocket fd) {
        this(null, fd, false);
    }
    
    AbstractEpollChannel(final Channel parent, final LinuxSocket fd, final boolean active) {
        super(parent);
        this.flags = Native.EPOLLET;
        this.socket = ObjectUtil.checkNotNull(fd, "fd");
        this.active = active;
        if (active) {
            this.local = fd.localAddress();
            this.remote = fd.remoteAddress();
        }
    }
    
    AbstractEpollChannel(final Channel parent, final LinuxSocket fd, final SocketAddress remote) {
        super(parent);
        this.flags = Native.EPOLLET;
        this.socket = ObjectUtil.checkNotNull(fd, "fd");
        this.active = true;
        this.remote = remote;
        this.local = fd.localAddress();
    }
    
    static boolean isSoErrorZero(final Socket fd) {
        try {
            return fd.getSoError() == 0;
        }
        catch (final IOException e) {
            throw new ChannelException(e);
        }
    }
    
    void setFlag(final int flag) throws IOException {
        if (!this.isFlagSet(flag)) {
            this.flags |= flag;
            this.modifyEvents();
        }
    }
    
    void clearFlag(final int flag) throws IOException {
        if (this.isFlagSet(flag)) {
            this.flags &= ~flag;
            this.modifyEvents();
        }
    }
    
    boolean isFlagSet(final int flag) {
        return (this.flags & flag) != 0x0;
    }
    
    @Override
    public final FileDescriptor fd() {
        return this.socket;
    }
    
    @Override
    public abstract EpollChannelConfig config();
    
    @Override
    public boolean isActive() {
        return this.active;
    }
    
    @Override
    public ChannelMetadata metadata() {
        return AbstractEpollChannel.METADATA;
    }
    
    @Override
    protected void doClose() throws Exception {
        this.active = false;
        this.inputClosedSeenErrorOnRead = true;
        try {
            final ChannelPromise promise = this.connectPromise;
            if (promise != null) {
                promise.tryFailure(new ClosedChannelException());
                this.connectPromise = null;
            }
            final Future<?> future = this.connectTimeoutFuture;
            if (future != null) {
                future.cancel(false);
                this.connectTimeoutFuture = null;
            }
            if (this.isRegistered()) {
                final EventLoop loop = this.eventLoop();
                if (loop.inEventLoop()) {
                    this.doDeregister();
                }
                else {
                    loop.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AbstractEpollChannel.this.doDeregister();
                            }
                            catch (final Throwable cause) {
                                AbstractEpollChannel.this.pipeline().fireExceptionCaught(cause);
                            }
                        }
                    });
                }
            }
        }
        finally {
            this.socket.close();
        }
    }
    
    void resetCachedAddresses() {
        this.local = this.socket.localAddress();
        this.remote = this.socket.remoteAddress();
    }
    
    @Override
    protected void doDisconnect() throws Exception {
        this.doClose();
    }
    
    @Override
    protected boolean isCompatible(final EventLoop loop) {
        return loop instanceof EpollEventLoop;
    }
    
    @Override
    public boolean isOpen() {
        return this.socket.isOpen();
    }
    
    @Override
    protected void doDeregister() throws Exception {
        ((EpollEventLoop)this.eventLoop()).remove(this);
    }
    
    @Override
    protected final void doBeginRead() throws Exception {
        final AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe)this.unsafe();
        unsafe.readPending = true;
        this.setFlag(Native.EPOLLIN);
        if (unsafe.maybeMoreDataToRead) {
            unsafe.executeEpollInReadyRunnable(this.config());
        }
    }
    
    final boolean shouldBreakEpollInReady(final ChannelConfig config) {
        return this.socket.isInputShutdown() && (this.inputClosedSeenErrorOnRead || !isAllowHalfClosure(config));
    }
    
    private static boolean isAllowHalfClosure(final ChannelConfig config) {
        if (config instanceof EpollDomainSocketChannelConfig) {
            return ((EpollDomainSocketChannelConfig)config).isAllowHalfClosure();
        }
        return config instanceof SocketChannelConfig && ((SocketChannelConfig)config).isAllowHalfClosure();
    }
    
    final void clearEpollIn() {
        if (this.isRegistered()) {
            final EventLoop loop = this.eventLoop();
            final AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe)this.unsafe();
            if (loop.inEventLoop()) {
                unsafe.clearEpollIn0();
            }
            else {
                loop.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (!unsafe.readPending && !AbstractEpollChannel.this.config().isAutoRead()) {
                            unsafe.clearEpollIn0();
                        }
                    }
                });
            }
        }
        else {
            this.flags &= ~Native.EPOLLIN;
        }
    }
    
    private void modifyEvents() throws IOException {
        if (this.isOpen() && this.isRegistered()) {
            ((EpollEventLoop)this.eventLoop()).modify(this);
        }
    }
    
    @Override
    protected void doRegister() throws Exception {
        this.epollInReadyRunnablePending = false;
        ((EpollEventLoop)this.eventLoop()).add(this);
    }
    
    @Override
    protected abstract AbstractEpollUnsafe newUnsafe();
    
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
    
    final long doWriteOrSendBytes(final ByteBuf data, final InetSocketAddress remoteAddress, final boolean fastOpen) throws IOException {
        assert remoteAddress != null : "fastOpen requires a remote address";
        if (data.hasMemoryAddress()) {
            final long memoryAddress = data.memoryAddress();
            if (remoteAddress == null) {
                return this.socket.writeAddress(memoryAddress, data.readerIndex(), data.writerIndex());
            }
            return this.socket.sendToAddress(memoryAddress, data.readerIndex(), data.writerIndex(), remoteAddress.getAddress(), remoteAddress.getPort(), fastOpen);
        }
        else if (data.nioBufferCount() > 1) {
            final IovArray array = ((EpollEventLoop)this.eventLoop()).cleanIovArray();
            array.add(data, data.readerIndex(), data.readableBytes());
            final int cnt = array.count();
            assert cnt != 0;
            if (remoteAddress == null) {
                return this.socket.writevAddresses(array.memoryAddress(0), cnt);
            }
            return this.socket.sendToAddresses(array.memoryAddress(0), cnt, remoteAddress.getAddress(), remoteAddress.getPort(), fastOpen);
        }
        else {
            final ByteBuffer nioData = data.internalNioBuffer(data.readerIndex(), data.readableBytes());
            if (remoteAddress == null) {
                return this.socket.write(nioData, nioData.position(), nioData.limit());
            }
            return this.socket.sendTo(nioData, nioData.position(), nioData.limit(), remoteAddress.getAddress(), remoteAddress.getPort(), fastOpen);
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
        final boolean connected = this.doConnect0(remoteAddress);
        if (connected) {
            this.remote = ((remoteSocketAddr == null) ? remoteAddress : UnixChannelUtil.computeRemoteAddr(remoteSocketAddr, this.socket.remoteAddress()));
        }
        this.local = this.socket.localAddress();
        return connected;
    }
    
    boolean doConnect0(final SocketAddress remote) throws Exception {
        boolean success = false;
        try {
            final boolean connected = this.socket.connect(remote);
            if (!connected) {
                this.setFlag(Native.EPOLLOUT);
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
    
    protected abstract class AbstractEpollUnsafe extends AbstractUnsafe
    {
        boolean readPending;
        boolean maybeMoreDataToRead;
        private EpollRecvByteAllocatorHandle allocHandle;
        private final Runnable epollInReadyRunnable;
        
        protected AbstractEpollUnsafe() {
            this.epollInReadyRunnable = new Runnable() {
                @Override
                public void run() {
                    AbstractEpollChannel.this.epollInReadyRunnablePending = false;
                    AbstractEpollUnsafe.this.epollInReady();
                }
            };
        }
        
        abstract void epollInReady();
        
        final void epollInBefore() {
            this.maybeMoreDataToRead = false;
        }
        
        final void epollInFinally(final ChannelConfig config) {
            this.maybeMoreDataToRead = this.allocHandle.maybeMoreDataToRead();
            if (this.allocHandle.isReceivedRdHup() || (this.readPending && this.maybeMoreDataToRead)) {
                this.executeEpollInReadyRunnable(config);
            }
            else if (!this.readPending && !config.isAutoRead()) {
                AbstractEpollChannel.this.clearEpollIn();
            }
        }
        
        final void executeEpollInReadyRunnable(final ChannelConfig config) {
            if (AbstractEpollChannel.this.epollInReadyRunnablePending || !AbstractEpollChannel.this.isActive() || AbstractEpollChannel.this.shouldBreakEpollInReady(config)) {
                return;
            }
            AbstractEpollChannel.this.epollInReadyRunnablePending = true;
            AbstractEpollChannel.this.eventLoop().execute(this.epollInReadyRunnable);
        }
        
        final void epollRdHupReady() {
            this.recvBufAllocHandle().receivedRdHup();
            if (AbstractEpollChannel.this.isActive()) {
                this.epollInReady();
            }
            else {
                this.shutdownInput(true);
            }
            this.clearEpollRdHup();
        }
        
        private void clearEpollRdHup() {
            try {
                AbstractEpollChannel.this.clearFlag(Native.EPOLLRDHUP);
            }
            catch (final IOException e) {
                AbstractEpollChannel.this.pipeline().fireExceptionCaught((Throwable)e);
                this.close(this.voidPromise());
            }
        }
        
        void shutdownInput(final boolean rdHup) {
            if (!AbstractEpollChannel.this.socket.isInputShutdown()) {
                if (isAllowHalfClosure(AbstractEpollChannel.this.config())) {
                    try {
                        AbstractEpollChannel.this.socket.shutdown(true, false);
                    }
                    catch (final IOException ignored) {
                        this.fireEventAndClose(ChannelInputShutdownEvent.INSTANCE);
                        return;
                    }
                    catch (final NotYetConnectedException ex) {}
                    AbstractEpollChannel.this.clearEpollIn();
                    AbstractEpollChannel.this.pipeline().fireUserEventTriggered((Object)ChannelInputShutdownEvent.INSTANCE);
                }
                else {
                    this.close(this.voidPromise());
                }
            }
            else if (!rdHup) {
                AbstractEpollChannel.this.inputClosedSeenErrorOnRead = true;
                AbstractEpollChannel.this.pipeline().fireUserEventTriggered((Object)ChannelInputShutdownReadComplete.INSTANCE);
            }
        }
        
        private void fireEventAndClose(final Object evt) {
            AbstractEpollChannel.this.pipeline().fireUserEventTriggered(evt);
            this.close(this.voidPromise());
        }
        
        @Override
        public EpollRecvByteAllocatorHandle recvBufAllocHandle() {
            if (this.allocHandle == null) {
                this.allocHandle = this.newEpollHandle((RecvByteBufAllocator.ExtendedHandle)super.recvBufAllocHandle());
            }
            return this.allocHandle;
        }
        
        EpollRecvByteAllocatorHandle newEpollHandle(final RecvByteBufAllocator.ExtendedHandle handle) {
            return new EpollRecvByteAllocatorHandle(handle);
        }
        
        @Override
        protected final void flush0() {
            if (!AbstractEpollChannel.this.isFlagSet(Native.EPOLLOUT)) {
                super.flush0();
            }
        }
        
        final void epollOutReady() {
            if (AbstractEpollChannel.this.connectPromise != null) {
                this.finishConnect();
            }
            else if (!AbstractEpollChannel.this.socket.isOutputShutdown()) {
                super.flush0();
            }
        }
        
        protected final void clearEpollIn0() {
            assert AbstractEpollChannel.this.eventLoop().inEventLoop();
            try {
                this.readPending = false;
                AbstractEpollChannel.this.clearFlag(Native.EPOLLIN);
            }
            catch (final IOException e) {
                AbstractEpollChannel.this.pipeline().fireExceptionCaught((Throwable)e);
                AbstractEpollChannel.this.unsafe().close(AbstractEpollChannel.this.unsafe().voidPromise());
            }
        }
        
        @Override
        public void connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
            if (!promise.setUncancellable() || !this.ensureOpen(promise)) {
                return;
            }
            try {
                if (AbstractEpollChannel.this.connectPromise != null) {
                    throw new ConnectionPendingException();
                }
                final boolean wasActive = AbstractEpollChannel.this.isActive();
                if (AbstractEpollChannel.this.doConnect(remoteAddress, localAddress)) {
                    this.fulfillConnectPromise(promise, wasActive);
                }
                else {
                    AbstractEpollChannel.this.connectPromise = promise;
                    AbstractEpollChannel.this.requestedRemoteAddress = remoteAddress;
                    final int connectTimeoutMillis = AbstractEpollChannel.this.config().getConnectTimeoutMillis();
                    if (connectTimeoutMillis > 0) {
                        AbstractEpollChannel.this.connectTimeoutFuture = AbstractEpollChannel.this.eventLoop().schedule((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                final ChannelPromise connectPromise = AbstractEpollChannel.this.connectPromise;
                                if (connectPromise != null && !connectPromise.isDone() && connectPromise.tryFailure(new ConnectTimeoutException("connection timed out: " + remoteAddress))) {
                                    AbstractEpollUnsafe.this.close(AbstractEpollUnsafe.this.voidPromise());
                                }
                            }
                        }, (long)connectTimeoutMillis, TimeUnit.MILLISECONDS);
                    }
                    promise.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                        @Override
                        public void operationComplete(final ChannelFuture future) throws Exception {
                            if (future.isCancelled()) {
                                if (AbstractEpollChannel.this.connectTimeoutFuture != null) {
                                    AbstractEpollChannel.this.connectTimeoutFuture.cancel(false);
                                }
                                AbstractEpollChannel.this.connectPromise = null;
                                AbstractEpollUnsafe.this.close(AbstractEpollUnsafe.this.voidPromise());
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
            AbstractEpollChannel.this.active = true;
            final boolean active = AbstractEpollChannel.this.isActive();
            final boolean promiseSet = promise.trySuccess();
            if (!wasActive && active) {
                AbstractEpollChannel.this.pipeline().fireChannelActive();
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
            assert AbstractEpollChannel.this.eventLoop().inEventLoop();
            boolean connectStillInProgress = false;
            try {
                final boolean wasActive = AbstractEpollChannel.this.isActive();
                if (!this.doFinishConnect()) {
                    connectStillInProgress = true;
                    return;
                }
                this.fulfillConnectPromise(AbstractEpollChannel.this.connectPromise, wasActive);
            }
            catch (final Throwable t) {
                this.fulfillConnectPromise(AbstractEpollChannel.this.connectPromise, this.annotateConnectException(t, AbstractEpollChannel.this.requestedRemoteAddress));
            }
            finally {
                if (!connectStillInProgress) {
                    if (AbstractEpollChannel.this.connectTimeoutFuture != null) {
                        AbstractEpollChannel.this.connectTimeoutFuture.cancel(false);
                    }
                    AbstractEpollChannel.this.connectPromise = null;
                }
            }
        }
        
        private boolean doFinishConnect() throws Exception {
            if (AbstractEpollChannel.this.socket.finishConnect()) {
                AbstractEpollChannel.this.clearFlag(Native.EPOLLOUT);
                if (AbstractEpollChannel.this.requestedRemoteAddress instanceof InetSocketAddress) {
                    AbstractEpollChannel.this.remote = UnixChannelUtil.computeRemoteAddr((InetSocketAddress)AbstractEpollChannel.this.requestedRemoteAddress, AbstractEpollChannel.this.socket.remoteAddress());
                }
                AbstractEpollChannel.this.requestedRemoteAddress = null;
                return true;
            }
            AbstractEpollChannel.this.setFlag(Native.EPOLLOUT);
            return false;
        }
    }
}
