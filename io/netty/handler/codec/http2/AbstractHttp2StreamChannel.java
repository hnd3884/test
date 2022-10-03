package io.netty.handler.codec.http2;

import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import java.io.IOException;
import io.netty.util.internal.StringUtil;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.RejectedExecutionException;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.VoidChannelPromise;
import io.netty.channel.MessageSizeEstimator;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.RecvByteBufAllocator;
import java.util.ArrayDeque;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.buffer.ByteBufAllocator;
import java.net.SocketAddress;
import io.netty.channel.EventLoop;
import io.netty.channel.ChannelConfig;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import java.util.Queue;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelFutureListener;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import io.netty.channel.ChannelMetadata;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.DefaultAttributeMap;

abstract class AbstractHttp2StreamChannel extends DefaultAttributeMap implements Http2StreamChannel
{
    static final Http2FrameStreamVisitor WRITABLE_VISITOR;
    private static final InternalLogger logger;
    private static final ChannelMetadata METADATA;
    private static final int MIN_HTTP2_FRAME_SIZE = 9;
    private static final AtomicLongFieldUpdater<AbstractHttp2StreamChannel> TOTAL_PENDING_SIZE_UPDATER;
    private static final AtomicIntegerFieldUpdater<AbstractHttp2StreamChannel> UNWRITABLE_UPDATER;
    private final ChannelFutureListener windowUpdateFrameWriteListener;
    private final Http2StreamChannelConfig config;
    private final Http2ChannelUnsafe unsafe;
    private final ChannelId channelId;
    private final ChannelPipeline pipeline;
    private final Http2FrameCodec.DefaultHttp2FrameStream stream;
    private final ChannelPromise closePromise;
    private volatile boolean registered;
    private volatile long totalPendingSize;
    private volatile int unwritable;
    private Runnable fireChannelWritabilityChangedTask;
    private boolean outboundClosed;
    private int flowControlledBytes;
    private ReadStatus readStatus;
    private Queue<Object> inboundBuffer;
    private boolean firstFrameWritten;
    private boolean readCompletePending;
    
    private static void windowUpdateFrameWriteComplete(final ChannelFuture future, final Channel streamChannel) {
        Throwable cause = future.cause();
        if (cause != null) {
            final Throwable unwrappedCause;
            if (cause instanceof Http2FrameStreamException && (unwrappedCause = cause.getCause()) != null) {
                cause = unwrappedCause;
            }
            streamChannel.pipeline().fireExceptionCaught(cause);
            streamChannel.unsafe().close(streamChannel.unsafe().voidPromise());
        }
    }
    
    AbstractHttp2StreamChannel(final Http2FrameCodec.DefaultHttp2FrameStream stream, final int id, final ChannelHandler inboundHandler) {
        this.windowUpdateFrameWriteListener = new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) {
                windowUpdateFrameWriteComplete(future, AbstractHttp2StreamChannel.this);
            }
        };
        this.config = new Http2StreamChannelConfig(this);
        this.unsafe = new Http2ChannelUnsafe();
        this.readStatus = ReadStatus.IDLE;
        this.stream = stream;
        stream.attachment = this;
        this.pipeline = new DefaultChannelPipeline(this) {
            @Override
            protected void incrementPendingOutboundBytes(final long size) {
                AbstractHttp2StreamChannel.this.incrementPendingOutboundBytes(size, true);
            }
            
            @Override
            protected void decrementPendingOutboundBytes(final long size) {
                AbstractHttp2StreamChannel.this.decrementPendingOutboundBytes(size, true);
            }
        };
        this.closePromise = this.pipeline.newPromise();
        this.channelId = new Http2StreamChannelId(this.parent().id(), id);
        if (inboundHandler != null) {
            this.pipeline.addLast(inboundHandler);
        }
    }
    
    private void incrementPendingOutboundBytes(final long size, final boolean invokeLater) {
        if (size == 0L) {
            return;
        }
        final long newWriteBufferSize = AbstractHttp2StreamChannel.TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, size);
        if (newWriteBufferSize > this.config().getWriteBufferHighWaterMark()) {
            this.setUnwritable(invokeLater);
        }
    }
    
    private void decrementPendingOutboundBytes(final long size, final boolean invokeLater) {
        if (size == 0L) {
            return;
        }
        final long newWriteBufferSize = AbstractHttp2StreamChannel.TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
        if (newWriteBufferSize < this.config().getWriteBufferLowWaterMark() && this.parent().isWritable()) {
            this.setWritable(invokeLater);
        }
    }
    
    final void trySetWritable() {
        if (this.totalPendingSize < this.config().getWriteBufferLowWaterMark()) {
            this.setWritable(false);
        }
    }
    
    private void setWritable(final boolean invokeLater) {
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = (oldValue & 0xFFFFFFFE);
        } while (!AbstractHttp2StreamChannel.UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        if (oldValue != 0 && newValue == 0) {
            this.fireChannelWritabilityChanged(invokeLater);
        }
    }
    
    private void setUnwritable(final boolean invokeLater) {
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = (oldValue | 0x1);
        } while (!AbstractHttp2StreamChannel.UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        if (oldValue == 0) {
            this.fireChannelWritabilityChanged(invokeLater);
        }
    }
    
    private void fireChannelWritabilityChanged(final boolean invokeLater) {
        final ChannelPipeline pipeline = this.pipeline();
        if (invokeLater) {
            Runnable task = this.fireChannelWritabilityChangedTask;
            if (task == null) {
                task = (this.fireChannelWritabilityChangedTask = new Runnable() {
                    @Override
                    public void run() {
                        pipeline.fireChannelWritabilityChanged();
                    }
                });
            }
            this.eventLoop().execute(task);
        }
        else {
            pipeline.fireChannelWritabilityChanged();
        }
    }
    
    @Override
    public Http2FrameStream stream() {
        return this.stream;
    }
    
    void closeOutbound() {
        this.outboundClosed = true;
    }
    
    void streamClosed() {
        this.unsafe.readEOS();
        this.unsafe.doBeginRead();
    }
    
    @Override
    public ChannelMetadata metadata() {
        return AbstractHttp2StreamChannel.METADATA;
    }
    
    @Override
    public ChannelConfig config() {
        return this.config;
    }
    
    @Override
    public boolean isOpen() {
        return !this.closePromise.isDone();
    }
    
    @Override
    public boolean isActive() {
        return this.isOpen();
    }
    
    @Override
    public boolean isWritable() {
        return this.unwritable == 0;
    }
    
    @Override
    public ChannelId id() {
        return this.channelId;
    }
    
    @Override
    public EventLoop eventLoop() {
        return this.parent().eventLoop();
    }
    
    @Override
    public Channel parent() {
        return this.parentContext().channel();
    }
    
    @Override
    public boolean isRegistered() {
        return this.registered;
    }
    
    @Override
    public SocketAddress localAddress() {
        return this.parent().localAddress();
    }
    
    @Override
    public SocketAddress remoteAddress() {
        return this.parent().remoteAddress();
    }
    
    @Override
    public ChannelFuture closeFuture() {
        return this.closePromise;
    }
    
    @Override
    public long bytesBeforeUnwritable() {
        final long bytes = this.config().getWriteBufferHighWaterMark() - this.totalPendingSize;
        if (bytes > 0L) {
            return this.isWritable() ? bytes : 0L;
        }
        return 0L;
    }
    
    @Override
    public long bytesBeforeWritable() {
        final long bytes = this.totalPendingSize - this.config().getWriteBufferLowWaterMark();
        if (bytes > 0L) {
            return this.isWritable() ? 0L : bytes;
        }
        return 0L;
    }
    
    @Override
    public Channel.Unsafe unsafe() {
        return this.unsafe;
    }
    
    @Override
    public ChannelPipeline pipeline() {
        return this.pipeline;
    }
    
    @Override
    public ByteBufAllocator alloc() {
        return this.config().getAllocator();
    }
    
    @Override
    public Channel read() {
        this.pipeline().read();
        return this;
    }
    
    @Override
    public Channel flush() {
        this.pipeline().flush();
        return this;
    }
    
    @Override
    public ChannelFuture bind(final SocketAddress localAddress) {
        return this.pipeline().bind(localAddress);
    }
    
    @Override
    public ChannelFuture connect(final SocketAddress remoteAddress) {
        return this.pipeline().connect(remoteAddress);
    }
    
    @Override
    public ChannelFuture connect(final SocketAddress remoteAddress, final SocketAddress localAddress) {
        return this.pipeline().connect(remoteAddress, localAddress);
    }
    
    @Override
    public ChannelFuture disconnect() {
        return this.pipeline().disconnect();
    }
    
    @Override
    public ChannelFuture close() {
        return this.pipeline().close();
    }
    
    @Override
    public ChannelFuture deregister() {
        return this.pipeline().deregister();
    }
    
    @Override
    public ChannelFuture bind(final SocketAddress localAddress, final ChannelPromise promise) {
        return this.pipeline().bind(localAddress, promise);
    }
    
    @Override
    public ChannelFuture connect(final SocketAddress remoteAddress, final ChannelPromise promise) {
        return this.pipeline().connect(remoteAddress, promise);
    }
    
    @Override
    public ChannelFuture connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
        return this.pipeline().connect(remoteAddress, localAddress, promise);
    }
    
    @Override
    public ChannelFuture disconnect(final ChannelPromise promise) {
        return this.pipeline().disconnect(promise);
    }
    
    @Override
    public ChannelFuture close(final ChannelPromise promise) {
        return this.pipeline().close(promise);
    }
    
    @Override
    public ChannelFuture deregister(final ChannelPromise promise) {
        return this.pipeline().deregister(promise);
    }
    
    @Override
    public ChannelFuture write(final Object msg) {
        return this.pipeline().write(msg);
    }
    
    @Override
    public ChannelFuture write(final Object msg, final ChannelPromise promise) {
        return this.pipeline().write(msg, promise);
    }
    
    @Override
    public ChannelFuture writeAndFlush(final Object msg, final ChannelPromise promise) {
        return this.pipeline().writeAndFlush(msg, promise);
    }
    
    @Override
    public ChannelFuture writeAndFlush(final Object msg) {
        return this.pipeline().writeAndFlush(msg);
    }
    
    @Override
    public ChannelPromise newPromise() {
        return this.pipeline().newPromise();
    }
    
    @Override
    public ChannelProgressivePromise newProgressivePromise() {
        return this.pipeline().newProgressivePromise();
    }
    
    @Override
    public ChannelFuture newSucceededFuture() {
        return this.pipeline().newSucceededFuture();
    }
    
    @Override
    public ChannelFuture newFailedFuture(final Throwable cause) {
        return this.pipeline().newFailedFuture(cause);
    }
    
    @Override
    public ChannelPromise voidPromise() {
        return this.pipeline().voidPromise();
    }
    
    @Override
    public int hashCode() {
        return this.id().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o;
    }
    
    @Override
    public int compareTo(final Channel o) {
        if (this == o) {
            return 0;
        }
        return this.id().compareTo(o.id());
    }
    
    @Override
    public String toString() {
        return this.parent().toString() + "(H2 - " + this.stream + ')';
    }
    
    void fireChildRead(final Http2Frame frame) {
        assert this.eventLoop().inEventLoop();
        if (!this.isActive()) {
            ReferenceCountUtil.release(frame);
        }
        else if (this.readStatus != ReadStatus.IDLE) {
            assert !(!this.inboundBuffer.isEmpty());
            final RecvByteBufAllocator.Handle allocHandle = this.unsafe.recvBufAllocHandle();
            this.unsafe.doRead0(frame, allocHandle);
            if (allocHandle.continueReading()) {
                this.maybeAddChannelToReadCompletePendingQueue();
            }
            else {
                this.unsafe.notifyReadComplete(allocHandle, true);
            }
        }
        else {
            if (this.inboundBuffer == null) {
                this.inboundBuffer = new ArrayDeque<Object>(4);
            }
            this.inboundBuffer.add(frame);
        }
    }
    
    void fireChildReadComplete() {
        assert this.eventLoop().inEventLoop();
        assert !this.readCompletePending;
        this.unsafe.notifyReadComplete(this.unsafe.recvBufAllocHandle(), false);
    }
    
    private void maybeAddChannelToReadCompletePendingQueue() {
        if (!this.readCompletePending) {
            this.readCompletePending = true;
            this.addChannelToReadCompletePendingQueue();
        }
    }
    
    protected void flush0(final ChannelHandlerContext ctx) {
        ctx.flush();
    }
    
    protected ChannelFuture write0(final ChannelHandlerContext ctx, final Object msg) {
        final ChannelPromise promise = ctx.newPromise();
        ctx.write(msg, promise);
        return promise;
    }
    
    protected abstract boolean isParentReadInProgress();
    
    protected abstract void addChannelToReadCompletePendingQueue();
    
    protected abstract ChannelHandlerContext parentContext();
    
    static {
        WRITABLE_VISITOR = new Http2FrameStreamVisitor() {
            @Override
            public boolean visit(final Http2FrameStream stream) {
                final AbstractHttp2StreamChannel childChannel = (AbstractHttp2StreamChannel)((Http2FrameCodec.DefaultHttp2FrameStream)stream).attachment;
                childChannel.trySetWritable();
                return true;
            }
        };
        logger = InternalLoggerFactory.getInstance(AbstractHttp2StreamChannel.class);
        METADATA = new ChannelMetadata(false, 16);
        TOTAL_PENDING_SIZE_UPDATER = AtomicLongFieldUpdater.newUpdater(AbstractHttp2StreamChannel.class, "totalPendingSize");
        UNWRITABLE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractHttp2StreamChannel.class, "unwritable");
    }
    
    private static final class FlowControlledFrameSizeEstimator implements MessageSizeEstimator
    {
        static final FlowControlledFrameSizeEstimator INSTANCE;
        private static final Handle HANDLE_INSTANCE;
        
        @Override
        public Handle newHandle() {
            return FlowControlledFrameSizeEstimator.HANDLE_INSTANCE;
        }
        
        static {
            INSTANCE = new FlowControlledFrameSizeEstimator();
            HANDLE_INSTANCE = new Handle() {
                @Override
                public int size(final Object msg) {
                    return (msg instanceof Http2DataFrame) ? ((int)Math.min(2147483647L, ((Http2DataFrame)msg).initialFlowControlledBytes() + 9L)) : 9;
                }
            };
        }
    }
    
    private enum ReadStatus
    {
        IDLE, 
        IN_PROGRESS, 
        REQUESTED;
    }
    
    private final class Http2ChannelUnsafe implements Channel.Unsafe
    {
        private final VoidChannelPromise unsafeVoidPromise;
        private RecvByteBufAllocator.Handle recvHandle;
        private boolean writeDoneAndNoFlush;
        private boolean closeInitiated;
        private boolean readEOS;
        
        private Http2ChannelUnsafe() {
            this.unsafeVoidPromise = new VoidChannelPromise(AbstractHttp2StreamChannel.this, false);
        }
        
        @Override
        public void connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
            if (!promise.setUncancellable()) {
                return;
            }
            promise.setFailure((Throwable)new UnsupportedOperationException());
        }
        
        @Override
        public RecvByteBufAllocator.Handle recvBufAllocHandle() {
            if (this.recvHandle == null) {
                (this.recvHandle = AbstractHttp2StreamChannel.this.config().getRecvByteBufAllocator().newHandle()).reset(AbstractHttp2StreamChannel.this.config());
            }
            return this.recvHandle;
        }
        
        @Override
        public SocketAddress localAddress() {
            return AbstractHttp2StreamChannel.this.parent().unsafe().localAddress();
        }
        
        @Override
        public SocketAddress remoteAddress() {
            return AbstractHttp2StreamChannel.this.parent().unsafe().remoteAddress();
        }
        
        @Override
        public void register(final EventLoop eventLoop, final ChannelPromise promise) {
            if (!promise.setUncancellable()) {
                return;
            }
            if (AbstractHttp2StreamChannel.this.registered) {
                promise.setFailure((Throwable)new UnsupportedOperationException("Re-register is not supported"));
                return;
            }
            AbstractHttp2StreamChannel.this.registered = true;
            promise.setSuccess();
            AbstractHttp2StreamChannel.this.pipeline().fireChannelRegistered();
            if (AbstractHttp2StreamChannel.this.isActive()) {
                AbstractHttp2StreamChannel.this.pipeline().fireChannelActive();
            }
        }
        
        @Override
        public void bind(final SocketAddress localAddress, final ChannelPromise promise) {
            if (!promise.setUncancellable()) {
                return;
            }
            promise.setFailure((Throwable)new UnsupportedOperationException());
        }
        
        @Override
        public void disconnect(final ChannelPromise promise) {
            this.close(promise);
        }
        
        @Override
        public void close(final ChannelPromise promise) {
            if (!promise.setUncancellable()) {
                return;
            }
            if (this.closeInitiated) {
                if (AbstractHttp2StreamChannel.this.closePromise.isDone()) {
                    promise.setSuccess();
                }
                else if (!(promise instanceof VoidChannelPromise)) {
                    AbstractHttp2StreamChannel.this.closePromise.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                        @Override
                        public void operationComplete(final ChannelFuture future) {
                            promise.setSuccess();
                        }
                    });
                }
                return;
            }
            this.closeInitiated = true;
            AbstractHttp2StreamChannel.this.readCompletePending = false;
            final boolean wasActive = AbstractHttp2StreamChannel.this.isActive();
            if (AbstractHttp2StreamChannel.this.parent().isActive() && !this.readEOS && Http2CodecUtil.isStreamIdValid(AbstractHttp2StreamChannel.this.stream.id())) {
                final Http2StreamFrame resetFrame = new DefaultHttp2ResetFrame(Http2Error.CANCEL).stream(AbstractHttp2StreamChannel.this.stream());
                this.write(resetFrame, AbstractHttp2StreamChannel.this.unsafe().voidPromise());
                this.flush();
            }
            if (AbstractHttp2StreamChannel.this.inboundBuffer != null) {
                while (true) {
                    final Object msg = AbstractHttp2StreamChannel.this.inboundBuffer.poll();
                    if (msg == null) {
                        break;
                    }
                    ReferenceCountUtil.release(msg);
                }
                AbstractHttp2StreamChannel.this.inboundBuffer = null;
            }
            AbstractHttp2StreamChannel.this.outboundClosed = true;
            AbstractHttp2StreamChannel.this.closePromise.setSuccess();
            promise.setSuccess();
            this.fireChannelInactiveAndDeregister(this.voidPromise(), wasActive);
        }
        
        @Override
        public void closeForcibly() {
            this.close(AbstractHttp2StreamChannel.this.unsafe().voidPromise());
        }
        
        @Override
        public void deregister(final ChannelPromise promise) {
            this.fireChannelInactiveAndDeregister(promise, false);
        }
        
        private void fireChannelInactiveAndDeregister(final ChannelPromise promise, final boolean fireChannelInactive) {
            if (!promise.setUncancellable()) {
                return;
            }
            if (!AbstractHttp2StreamChannel.this.registered) {
                promise.setSuccess();
                return;
            }
            this.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (fireChannelInactive) {
                        AbstractHttp2StreamChannel.this.pipeline.fireChannelInactive();
                    }
                    if (AbstractHttp2StreamChannel.this.registered) {
                        AbstractHttp2StreamChannel.this.registered = false;
                        AbstractHttp2StreamChannel.this.pipeline.fireChannelUnregistered();
                    }
                    Http2ChannelUnsafe.this.safeSetSuccess(promise);
                }
            });
        }
        
        private void safeSetSuccess(final ChannelPromise promise) {
            if (!(promise instanceof VoidChannelPromise) && !promise.trySuccess()) {
                AbstractHttp2StreamChannel.logger.warn("Failed to mark a promise as success because it is done already: {}", promise);
            }
        }
        
        private void invokeLater(final Runnable task) {
            try {
                AbstractHttp2StreamChannel.this.eventLoop().execute(task);
            }
            catch (final RejectedExecutionException e) {
                AbstractHttp2StreamChannel.logger.warn("Can't invoke task later as EventLoop rejected it", e);
            }
        }
        
        @Override
        public void beginRead() {
            if (!AbstractHttp2StreamChannel.this.isActive()) {
                return;
            }
            this.updateLocalWindowIfNeeded();
            switch (AbstractHttp2StreamChannel.this.readStatus) {
                case IDLE: {
                    AbstractHttp2StreamChannel.this.readStatus = ReadStatus.IN_PROGRESS;
                    this.doBeginRead();
                    break;
                }
                case IN_PROGRESS: {
                    AbstractHttp2StreamChannel.this.readStatus = ReadStatus.REQUESTED;
                    break;
                }
            }
        }
        
        private Object pollQueuedMessage() {
            return (AbstractHttp2StreamChannel.this.inboundBuffer == null) ? null : AbstractHttp2StreamChannel.this.inboundBuffer.poll();
        }
        
        void doBeginRead() {
            while (AbstractHttp2StreamChannel.this.readStatus != ReadStatus.IDLE) {
                Object message = this.pollQueuedMessage();
                if (message == null) {
                    if (this.readEOS) {
                        AbstractHttp2StreamChannel.this.unsafe.closeForcibly();
                    }
                    this.flush();
                    break;
                }
                final RecvByteBufAllocator.Handle allocHandle = this.recvBufAllocHandle();
                allocHandle.reset(AbstractHttp2StreamChannel.this.config());
                boolean continueReading = false;
                do {
                    this.doRead0((Http2Frame)message, allocHandle);
                } while ((this.readEOS || (continueReading = allocHandle.continueReading())) && (message = this.pollQueuedMessage()) != null);
                if (continueReading && AbstractHttp2StreamChannel.this.isParentReadInProgress() && !this.readEOS) {
                    AbstractHttp2StreamChannel.this.maybeAddChannelToReadCompletePendingQueue();
                }
                else {
                    this.notifyReadComplete(allocHandle, true);
                }
            }
        }
        
        void readEOS() {
            this.readEOS = true;
        }
        
        private void updateLocalWindowIfNeeded() {
            if (AbstractHttp2StreamChannel.this.flowControlledBytes != 0) {
                final int bytes = AbstractHttp2StreamChannel.this.flowControlledBytes;
                AbstractHttp2StreamChannel.this.flowControlledBytes = 0;
                final ChannelFuture future = AbstractHttp2StreamChannel.this.write0(AbstractHttp2StreamChannel.this.parentContext(), new DefaultHttp2WindowUpdateFrame(bytes).stream(AbstractHttp2StreamChannel.this.stream));
                this.writeDoneAndNoFlush = true;
                if (future.isDone()) {
                    windowUpdateFrameWriteComplete(future, AbstractHttp2StreamChannel.this);
                }
                else {
                    future.addListener((GenericFutureListener<? extends Future<? super Void>>)AbstractHttp2StreamChannel.this.windowUpdateFrameWriteListener);
                }
            }
        }
        
        void notifyReadComplete(final RecvByteBufAllocator.Handle allocHandle, final boolean forceReadComplete) {
            if (!AbstractHttp2StreamChannel.this.readCompletePending && !forceReadComplete) {
                return;
            }
            AbstractHttp2StreamChannel.this.readCompletePending = false;
            if (AbstractHttp2StreamChannel.this.readStatus == ReadStatus.REQUESTED) {
                AbstractHttp2StreamChannel.this.readStatus = ReadStatus.IN_PROGRESS;
            }
            else {
                AbstractHttp2StreamChannel.this.readStatus = ReadStatus.IDLE;
            }
            allocHandle.readComplete();
            AbstractHttp2StreamChannel.this.pipeline().fireChannelReadComplete();
            this.flush();
            if (this.readEOS) {
                AbstractHttp2StreamChannel.this.unsafe.closeForcibly();
            }
        }
        
        void doRead0(final Http2Frame frame, final RecvByteBufAllocator.Handle allocHandle) {
            int bytes;
            if (frame instanceof Http2DataFrame) {
                bytes = ((Http2DataFrame)frame).initialFlowControlledBytes();
                AbstractHttp2StreamChannel.this.flowControlledBytes += bytes;
            }
            else {
                bytes = 9;
            }
            allocHandle.attemptedBytesRead(bytes);
            allocHandle.lastBytesRead(bytes);
            allocHandle.incMessagesRead(1);
            AbstractHttp2StreamChannel.this.pipeline().fireChannelRead((Object)frame);
        }
        
        @Override
        public void write(final Object msg, final ChannelPromise promise) {
            if (!promise.setUncancellable()) {
                ReferenceCountUtil.release(msg);
                return;
            }
            if (!AbstractHttp2StreamChannel.this.isActive() || (AbstractHttp2StreamChannel.this.outboundClosed && (msg instanceof Http2HeadersFrame || msg instanceof Http2DataFrame))) {
                ReferenceCountUtil.release(msg);
                promise.setFailure((Throwable)new ClosedChannelException());
                return;
            }
            try {
                if (msg instanceof Http2StreamFrame) {
                    final Http2StreamFrame frame = this.validateStreamFrame((Http2StreamFrame)msg).stream(AbstractHttp2StreamChannel.this.stream());
                    this.writeHttp2StreamFrame(frame, promise);
                }
                else {
                    final String msgStr = msg.toString();
                    ReferenceCountUtil.release(msg);
                    promise.setFailure((Throwable)new IllegalArgumentException("Message must be an " + StringUtil.simpleClassName(Http2StreamFrame.class) + ": " + msgStr));
                }
            }
            catch (final Throwable t) {
                promise.tryFailure(t);
            }
        }
        
        private void writeHttp2StreamFrame(final Http2StreamFrame frame, final ChannelPromise promise) {
            if (!AbstractHttp2StreamChannel.this.firstFrameWritten && !Http2CodecUtil.isStreamIdValid(AbstractHttp2StreamChannel.this.stream().id()) && !(frame instanceof Http2HeadersFrame)) {
                ReferenceCountUtil.release(frame);
                promise.setFailure((Throwable)new IllegalArgumentException("The first frame must be a headers frame. Was: " + frame.name()));
                return;
            }
            final boolean firstWrite = !AbstractHttp2StreamChannel.this.firstFrameWritten && (AbstractHttp2StreamChannel.this.firstFrameWritten = true);
            final ChannelFuture f = AbstractHttp2StreamChannel.this.write0(AbstractHttp2StreamChannel.this.parentContext(), frame);
            if (f.isDone()) {
                if (firstWrite) {
                    this.firstWriteComplete(f, promise);
                }
                else {
                    this.writeComplete(f, promise);
                }
            }
            else {
                final long bytes = FlowControlledFrameSizeEstimator.HANDLE_INSTANCE.size(frame);
                AbstractHttp2StreamChannel.this.incrementPendingOutboundBytes(bytes, false);
                f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture future) {
                        if (firstWrite) {
                            Http2ChannelUnsafe.this.firstWriteComplete(future, promise);
                        }
                        else {
                            Http2ChannelUnsafe.this.writeComplete(future, promise);
                        }
                        AbstractHttp2StreamChannel.this.decrementPendingOutboundBytes(bytes, false);
                    }
                });
                this.writeDoneAndNoFlush = true;
            }
        }
        
        private void firstWriteComplete(final ChannelFuture future, final ChannelPromise promise) {
            final Throwable cause = future.cause();
            if (cause == null) {
                promise.setSuccess();
            }
            else {
                this.closeForcibly();
                promise.setFailure(this.wrapStreamClosedError(cause));
            }
        }
        
        private void writeComplete(final ChannelFuture future, final ChannelPromise promise) {
            final Throwable cause = future.cause();
            if (cause == null) {
                promise.setSuccess();
            }
            else {
                final Throwable error = this.wrapStreamClosedError(cause);
                if (error instanceof IOException) {
                    if (AbstractHttp2StreamChannel.this.config.isAutoClose()) {
                        this.closeForcibly();
                    }
                    else {
                        AbstractHttp2StreamChannel.this.outboundClosed = true;
                    }
                }
                promise.setFailure(error);
            }
        }
        
        private Throwable wrapStreamClosedError(final Throwable cause) {
            if (cause instanceof Http2Exception && ((Http2Exception)cause).error() == Http2Error.STREAM_CLOSED) {
                return new ClosedChannelException().initCause(cause);
            }
            return cause;
        }
        
        private Http2StreamFrame validateStreamFrame(final Http2StreamFrame frame) {
            if (frame.stream() != null && frame.stream() != AbstractHttp2StreamChannel.this.stream) {
                final String msgString = frame.toString();
                ReferenceCountUtil.release(frame);
                throw new IllegalArgumentException("Stream " + frame.stream() + " must not be set on the frame: " + msgString);
            }
            return frame;
        }
        
        @Override
        public void flush() {
            if (!this.writeDoneAndNoFlush || AbstractHttp2StreamChannel.this.isParentReadInProgress()) {
                return;
            }
            this.writeDoneAndNoFlush = false;
            AbstractHttp2StreamChannel.this.flush0(AbstractHttp2StreamChannel.this.parentContext());
        }
        
        @Override
        public ChannelPromise voidPromise() {
            return this.unsafeVoidPromise;
        }
        
        @Override
        public ChannelOutboundBuffer outboundBuffer() {
            return null;
        }
    }
    
    private static final class Http2StreamChannelConfig extends DefaultChannelConfig
    {
        Http2StreamChannelConfig(final Channel channel) {
            super(channel);
        }
        
        @Override
        public MessageSizeEstimator getMessageSizeEstimator() {
            return FlowControlledFrameSizeEstimator.INSTANCE;
        }
        
        @Override
        public ChannelConfig setMessageSizeEstimator(final MessageSizeEstimator estimator) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator allocator) {
            if (!(allocator.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
                throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
            }
            super.setRecvByteBufAllocator(allocator);
            return this;
        }
    }
}
