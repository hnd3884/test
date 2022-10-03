package io.netty.channel.epoll;

import io.netty.channel.unix.SocketWritableByteChannel;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.ChannelPipeline;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelFutureListener;
import java.util.concurrent.Executor;
import io.netty.channel.EventLoop;
import io.netty.channel.AbstractChannel;
import io.netty.util.internal.StringUtil;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.channel.FileRegion;
import io.netty.channel.DefaultFileRegion;
import java.io.IOException;
import io.netty.channel.unix.IovArray;
import java.nio.ByteBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import java.nio.channels.ClosedChannelException;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelFuture;
import java.net.SocketAddress;
import io.netty.channel.unix.Socket;
import io.netty.channel.Channel;
import java.nio.channels.WritableByteChannel;
import io.netty.channel.unix.FileDescriptor;
import java.util.Queue;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.socket.DuplexChannel;

public abstract class AbstractEpollStreamChannel extends AbstractEpollChannel implements DuplexChannel
{
    private static final ChannelMetadata METADATA;
    private static final String EXPECTED_TYPES;
    private static final InternalLogger logger;
    private final Runnable flushTask;
    private volatile Queue<SpliceInTask> spliceQueue;
    private FileDescriptor pipeIn;
    private FileDescriptor pipeOut;
    private WritableByteChannel byteChannel;
    
    protected AbstractEpollStreamChannel(final Channel parent, final int fd) {
        this(parent, new LinuxSocket(fd));
    }
    
    protected AbstractEpollStreamChannel(final int fd) {
        this(new LinuxSocket(fd));
    }
    
    AbstractEpollStreamChannel(final LinuxSocket fd) {
        this(fd, AbstractEpollChannel.isSoErrorZero(fd));
    }
    
    AbstractEpollStreamChannel(final Channel parent, final LinuxSocket fd) {
        super(parent, fd, true);
        this.flushTask = new Runnable() {
            @Override
            public void run() {
                ((AbstractEpollUnsafe)AbstractEpollStreamChannel.this.unsafe()).flush0();
            }
        };
        this.flags |= Native.EPOLLRDHUP;
    }
    
    AbstractEpollStreamChannel(final Channel parent, final LinuxSocket fd, final SocketAddress remote) {
        super(parent, fd, remote);
        this.flushTask = new Runnable() {
            @Override
            public void run() {
                ((AbstractEpollUnsafe)AbstractEpollStreamChannel.this.unsafe()).flush0();
            }
        };
        this.flags |= Native.EPOLLRDHUP;
    }
    
    protected AbstractEpollStreamChannel(final LinuxSocket fd, final boolean active) {
        super(null, fd, active);
        this.flushTask = new Runnable() {
            @Override
            public void run() {
                ((AbstractEpollUnsafe)AbstractEpollStreamChannel.this.unsafe()).flush0();
            }
        };
        this.flags |= Native.EPOLLRDHUP;
    }
    
    @Override
    protected AbstractEpollUnsafe newUnsafe() {
        return new EpollStreamUnsafe();
    }
    
    @Override
    public ChannelMetadata metadata() {
        return AbstractEpollStreamChannel.METADATA;
    }
    
    public final ChannelFuture spliceTo(final AbstractEpollStreamChannel ch, final int len) {
        return this.spliceTo(ch, len, this.newPromise());
    }
    
    public final ChannelFuture spliceTo(final AbstractEpollStreamChannel ch, final int len, final ChannelPromise promise) {
        if (ch.eventLoop() != this.eventLoop()) {
            throw new IllegalArgumentException("EventLoops are not the same.");
        }
        ObjectUtil.checkPositiveOrZero(len, "len");
        if (ch.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED || this.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) {
            throw new IllegalStateException("spliceTo() supported only when using " + EpollMode.LEVEL_TRIGGERED);
        }
        ObjectUtil.checkNotNull(promise, "promise");
        if (!this.isOpen()) {
            promise.tryFailure(new ClosedChannelException());
        }
        else {
            this.addToSpliceQueue(new SpliceInChannelTask(ch, len, promise));
            this.failSpliceIfClosed(promise);
        }
        return promise;
    }
    
    public final ChannelFuture spliceTo(final FileDescriptor ch, final int offset, final int len) {
        return this.spliceTo(ch, offset, len, this.newPromise());
    }
    
    public final ChannelFuture spliceTo(final FileDescriptor ch, final int offset, final int len, final ChannelPromise promise) {
        ObjectUtil.checkPositiveOrZero(len, "len");
        ObjectUtil.checkPositiveOrZero(offset, "offset");
        if (this.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) {
            throw new IllegalStateException("spliceTo() supported only when using " + EpollMode.LEVEL_TRIGGERED);
        }
        ObjectUtil.checkNotNull(promise, "promise");
        if (!this.isOpen()) {
            promise.tryFailure(new ClosedChannelException());
        }
        else {
            this.addToSpliceQueue(new SpliceFdTask(ch, offset, len, promise));
            this.failSpliceIfClosed(promise);
        }
        return promise;
    }
    
    private void failSpliceIfClosed(final ChannelPromise promise) {
        if (!this.isOpen() && promise.tryFailure(new ClosedChannelException())) {
            this.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    AbstractEpollStreamChannel.this.clearSpliceQueue();
                }
            });
        }
    }
    
    private int writeBytes(final ChannelOutboundBuffer in, final ByteBuf buf) throws Exception {
        final int readableBytes = buf.readableBytes();
        if (readableBytes == 0) {
            in.remove();
            return 0;
        }
        if (buf.hasMemoryAddress() || buf.nioBufferCount() == 1) {
            return this.doWriteBytes(in, buf);
        }
        final ByteBuffer[] nioBuffers = buf.nioBuffers();
        return this.writeBytesMultiple(in, nioBuffers, nioBuffers.length, readableBytes, this.config().getMaxBytesPerGatheringWrite());
    }
    
    private void adjustMaxBytesPerGatheringWrite(final long attempted, final long written, final long oldMaxBytesPerGatheringWrite) {
        if (attempted == written) {
            if (attempted << 1 > oldMaxBytesPerGatheringWrite) {
                this.config().setMaxBytesPerGatheringWrite(attempted << 1);
            }
        }
        else if (attempted > 4096L && written < attempted >>> 1) {
            this.config().setMaxBytesPerGatheringWrite(attempted >>> 1);
        }
    }
    
    private int writeBytesMultiple(final ChannelOutboundBuffer in, final IovArray array) throws IOException {
        final long expectedWrittenBytes = array.size();
        assert expectedWrittenBytes != 0L;
        final int cnt = array.count();
        assert cnt != 0;
        final long localWrittenBytes = this.socket.writevAddresses(array.memoryAddress(0), cnt);
        if (localWrittenBytes > 0L) {
            this.adjustMaxBytesPerGatheringWrite(expectedWrittenBytes, localWrittenBytes, array.maxBytes());
            in.removeBytes(localWrittenBytes);
            return 1;
        }
        return Integer.MAX_VALUE;
    }
    
    private int writeBytesMultiple(final ChannelOutboundBuffer in, final ByteBuffer[] nioBuffers, final int nioBufferCnt, long expectedWrittenBytes, final long maxBytesPerGatheringWrite) throws IOException {
        assert expectedWrittenBytes != 0L;
        if (expectedWrittenBytes > maxBytesPerGatheringWrite) {
            expectedWrittenBytes = maxBytesPerGatheringWrite;
        }
        final long localWrittenBytes = this.socket.writev(nioBuffers, 0, nioBufferCnt, expectedWrittenBytes);
        if (localWrittenBytes > 0L) {
            this.adjustMaxBytesPerGatheringWrite(expectedWrittenBytes, localWrittenBytes, maxBytesPerGatheringWrite);
            in.removeBytes(localWrittenBytes);
            return 1;
        }
        return Integer.MAX_VALUE;
    }
    
    private int writeDefaultFileRegion(final ChannelOutboundBuffer in, final DefaultFileRegion region) throws Exception {
        final long offset = region.transferred();
        final long regionCount = region.count();
        if (offset >= regionCount) {
            in.remove();
            return 0;
        }
        final long flushedAmount = this.socket.sendFile(region, region.position(), offset, regionCount - offset);
        if (flushedAmount > 0L) {
            in.progress(flushedAmount);
            if (region.transferred() >= regionCount) {
                in.remove();
            }
            return 1;
        }
        if (flushedAmount == 0L) {
            this.validateFileRegion(region, offset);
        }
        return Integer.MAX_VALUE;
    }
    
    private int writeFileRegion(final ChannelOutboundBuffer in, final FileRegion region) throws Exception {
        if (region.transferred() >= region.count()) {
            in.remove();
            return 0;
        }
        if (this.byteChannel == null) {
            this.byteChannel = new EpollSocketWritableByteChannel();
        }
        final long flushedAmount = region.transferTo(this.byteChannel, region.transferred());
        if (flushedAmount > 0L) {
            in.progress(flushedAmount);
            if (region.transferred() >= region.count()) {
                in.remove();
            }
            return 1;
        }
        return Integer.MAX_VALUE;
    }
    
    @Override
    protected void doWrite(final ChannelOutboundBuffer in) throws Exception {
        int writeSpinCount = this.config().getWriteSpinCount();
        do {
            final int msgCount = in.size();
            if (msgCount > 1 && in.current() instanceof ByteBuf) {
                writeSpinCount -= this.doWriteMultiple(in);
            }
            else {
                if (msgCount == 0) {
                    this.clearFlag(Native.EPOLLOUT);
                    return;
                }
                writeSpinCount -= this.doWriteSingle(in);
            }
        } while (writeSpinCount > 0);
        if (writeSpinCount == 0) {
            this.clearFlag(Native.EPOLLOUT);
            this.eventLoop().execute(this.flushTask);
        }
        else {
            this.setFlag(Native.EPOLLOUT);
        }
    }
    
    protected int doWriteSingle(final ChannelOutboundBuffer in) throws Exception {
        final Object msg = in.current();
        if (msg instanceof ByteBuf) {
            return this.writeBytes(in, (ByteBuf)msg);
        }
        if (msg instanceof DefaultFileRegion) {
            return this.writeDefaultFileRegion(in, (DefaultFileRegion)msg);
        }
        if (msg instanceof FileRegion) {
            return this.writeFileRegion(in, (FileRegion)msg);
        }
        if (!(msg instanceof SpliceOutTask)) {
            throw new Error();
        }
        if (!((SpliceOutTask)msg).spliceOut()) {
            return Integer.MAX_VALUE;
        }
        in.remove();
        return 1;
    }
    
    private int doWriteMultiple(final ChannelOutboundBuffer in) throws Exception {
        final long maxBytesPerGatheringWrite = this.config().getMaxBytesPerGatheringWrite();
        final IovArray array = ((EpollEventLoop)this.eventLoop()).cleanIovArray();
        array.maxBytes(maxBytesPerGatheringWrite);
        in.forEachFlushedMessage(array);
        if (array.count() >= 1) {
            return this.writeBytesMultiple(in, array);
        }
        in.removeBytes(0L);
        return 0;
    }
    
    @Override
    protected Object filterOutboundMessage(final Object msg) {
        if (msg instanceof ByteBuf) {
            final ByteBuf buf = (ByteBuf)msg;
            return UnixChannelUtil.isBufferCopyNeededForWrite(buf) ? this.newDirectBuffer(buf) : buf;
        }
        if (msg instanceof FileRegion || msg instanceof SpliceOutTask) {
            return msg;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + AbstractEpollStreamChannel.EXPECTED_TYPES);
    }
    
    @Override
    protected final void doShutdownOutput() throws Exception {
        this.socket.shutdown(false, true);
    }
    
    private void shutdownInput0(final ChannelPromise promise) {
        try {
            this.socket.shutdown(true, false);
            promise.setSuccess();
        }
        catch (final Throwable cause) {
            promise.setFailure(cause);
        }
    }
    
    @Override
    public boolean isOutputShutdown() {
        return this.socket.isOutputShutdown();
    }
    
    @Override
    public boolean isInputShutdown() {
        return this.socket.isInputShutdown();
    }
    
    @Override
    public boolean isShutdown() {
        return this.socket.isShutdown();
    }
    
    @Override
    public ChannelFuture shutdownOutput() {
        return this.shutdownOutput(this.newPromise());
    }
    
    @Override
    public ChannelFuture shutdownOutput(final ChannelPromise promise) {
        final EventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            ((AbstractUnsafe)this.unsafe()).shutdownOutput(promise);
        }
        else {
            loop.execute(new Runnable() {
                @Override
                public void run() {
                    ((AbstractUnsafe)AbstractEpollStreamChannel.this.unsafe()).shutdownOutput(promise);
                }
            });
        }
        return promise;
    }
    
    @Override
    public ChannelFuture shutdownInput() {
        return this.shutdownInput(this.newPromise());
    }
    
    @Override
    public ChannelFuture shutdownInput(final ChannelPromise promise) {
        final Executor closeExecutor = ((EpollStreamUnsafe)this.unsafe()).prepareToClose();
        if (closeExecutor != null) {
            closeExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    AbstractEpollStreamChannel.this.shutdownInput0(promise);
                }
            });
        }
        else {
            final EventLoop loop = this.eventLoop();
            if (loop.inEventLoop()) {
                this.shutdownInput0(promise);
            }
            else {
                loop.execute(new Runnable() {
                    @Override
                    public void run() {
                        AbstractEpollStreamChannel.this.shutdownInput0(promise);
                    }
                });
            }
        }
        return promise;
    }
    
    @Override
    public ChannelFuture shutdown() {
        return this.shutdown(this.newPromise());
    }
    
    @Override
    public ChannelFuture shutdown(final ChannelPromise promise) {
        final ChannelFuture shutdownOutputFuture = this.shutdownOutput();
        if (shutdownOutputFuture.isDone()) {
            this.shutdownOutputDone(shutdownOutputFuture, promise);
        }
        else {
            shutdownOutputFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture shutdownOutputFuture) throws Exception {
                    AbstractEpollStreamChannel.this.shutdownOutputDone(shutdownOutputFuture, promise);
                }
            });
        }
        return promise;
    }
    
    private void shutdownOutputDone(final ChannelFuture shutdownOutputFuture, final ChannelPromise promise) {
        final ChannelFuture shutdownInputFuture = this.shutdownInput();
        if (shutdownInputFuture.isDone()) {
            shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
        }
        else {
            shutdownInputFuture.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture shutdownInputFuture) throws Exception {
                    shutdownDone(shutdownOutputFuture, shutdownInputFuture, promise);
                }
            });
        }
    }
    
    private static void shutdownDone(final ChannelFuture shutdownOutputFuture, final ChannelFuture shutdownInputFuture, final ChannelPromise promise) {
        final Throwable shutdownOutputCause = shutdownOutputFuture.cause();
        final Throwable shutdownInputCause = shutdownInputFuture.cause();
        if (shutdownOutputCause != null) {
            if (shutdownInputCause != null) {
                AbstractEpollStreamChannel.logger.debug("Exception suppressed because a previous exception occurred.", shutdownInputCause);
            }
            promise.setFailure(shutdownOutputCause);
        }
        else if (shutdownInputCause != null) {
            promise.setFailure(shutdownInputCause);
        }
        else {
            promise.setSuccess();
        }
    }
    
    @Override
    protected void doClose() throws Exception {
        try {
            super.doClose();
        }
        finally {
            safeClosePipe(this.pipeIn);
            safeClosePipe(this.pipeOut);
            this.clearSpliceQueue();
        }
    }
    
    private void clearSpliceQueue() {
        final Queue<SpliceInTask> sQueue = this.spliceQueue;
        if (sQueue == null) {
            return;
        }
        ClosedChannelException exception = null;
        while (true) {
            final SpliceInTask task = sQueue.poll();
            if (task == null) {
                break;
            }
            if (exception == null) {
                exception = new ClosedChannelException();
            }
            task.promise.tryFailure(exception);
        }
    }
    
    private static void safeClosePipe(final FileDescriptor fd) {
        if (fd != null) {
            try {
                fd.close();
            }
            catch (final IOException e) {
                AbstractEpollStreamChannel.logger.warn("Error while closing a pipe", e);
            }
        }
    }
    
    private void addToSpliceQueue(final SpliceInTask task) {
        Queue<SpliceInTask> sQueue = this.spliceQueue;
        if (sQueue == null) {
            synchronized (this) {
                sQueue = this.spliceQueue;
                if (sQueue == null) {
                    sQueue = (this.spliceQueue = PlatformDependent.newMpscQueue());
                }
            }
        }
        sQueue.add(task);
    }
    
    static {
        METADATA = new ChannelMetadata(false, 16);
        EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DefaultFileRegion.class) + ')';
        logger = InternalLoggerFactory.getInstance(AbstractEpollStreamChannel.class);
    }
    
    class EpollStreamUnsafe extends AbstractEpollUnsafe
    {
        @Override
        protected Executor prepareToClose() {
            return super.prepareToClose();
        }
        
        private void handleReadException(final ChannelPipeline pipeline, final ByteBuf byteBuf, final Throwable cause, final boolean close, final EpollRecvByteAllocatorHandle allocHandle) {
            if (byteBuf != null) {
                if (byteBuf.isReadable()) {
                    this.readPending = false;
                    pipeline.fireChannelRead((Object)byteBuf);
                }
                else {
                    byteBuf.release();
                }
            }
            allocHandle.readComplete();
            pipeline.fireChannelReadComplete();
            pipeline.fireExceptionCaught(cause);
            if (close || cause instanceof OutOfMemoryError || cause instanceof IOException) {
                this.shutdownInput(false);
            }
        }
        
        @Override
        EpollRecvByteAllocatorHandle newEpollHandle(final RecvByteBufAllocator.ExtendedHandle handle) {
            return new EpollRecvByteAllocatorStreamingHandle(handle);
        }
        
        @Override
        void epollInReady() {
            final ChannelConfig config = AbstractEpollStreamChannel.this.config();
            if (AbstractEpollStreamChannel.this.shouldBreakEpollInReady(config)) {
                this.clearEpollIn0();
                return;
            }
            final EpollRecvByteAllocatorHandle allocHandle = this.recvBufAllocHandle();
            allocHandle.edgeTriggered(AbstractEpollStreamChannel.this.isFlagSet(Native.EPOLLET));
            final ChannelPipeline pipeline = AbstractEpollStreamChannel.this.pipeline();
            final ByteBufAllocator allocator = config.getAllocator();
            allocHandle.reset(config);
            this.epollInBefore();
            ByteBuf byteBuf = null;
            boolean close = false;
            try {
                Queue<SpliceInTask> sQueue = null;
                do {
                    if (sQueue != null || (sQueue = AbstractEpollStreamChannel.this.spliceQueue) != null) {
                        final SpliceInTask spliceTask = sQueue.peek();
                        if (spliceTask != null) {
                            if (!spliceTask.spliceIn(allocHandle)) {
                                break;
                            }
                            if (AbstractEpollStreamChannel.this.isActive()) {
                                sQueue.remove();
                                continue;
                            }
                            continue;
                        }
                    }
                    byteBuf = allocHandle.allocate(allocator);
                    allocHandle.lastBytesRead(AbstractEpollStreamChannel.this.doReadBytes(byteBuf));
                    if (allocHandle.lastBytesRead() <= 0) {
                        byteBuf.release();
                        byteBuf = null;
                        close = (allocHandle.lastBytesRead() < 0);
                        if (close) {
                            this.readPending = false;
                            break;
                        }
                        break;
                    }
                    else {
                        allocHandle.incMessagesRead(1);
                        this.readPending = false;
                        pipeline.fireChannelRead((Object)byteBuf);
                        byteBuf = null;
                        if (AbstractEpollStreamChannel.this.shouldBreakEpollInReady(config)) {
                            break;
                        }
                        continue;
                    }
                } while (allocHandle.continueReading());
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                if (close) {
                    this.shutdownInput(false);
                }
            }
            catch (final Throwable t) {
                this.handleReadException(pipeline, byteBuf, t, close, allocHandle);
            }
            finally {
                this.epollInFinally(config);
            }
        }
    }
    
    protected abstract class SpliceInTask
    {
        final ChannelPromise promise;
        int len;
        
        protected SpliceInTask(final int len, final ChannelPromise promise) {
            this.promise = promise;
            this.len = len;
        }
        
        abstract boolean spliceIn(final RecvByteBufAllocator.Handle p0);
        
        protected final int spliceIn(final FileDescriptor pipeOut, final RecvByteBufAllocator.Handle handle) throws IOException {
            int length = Math.min(handle.guess(), this.len);
            int splicedIn = 0;
            while (true) {
                final int localSplicedIn = Native.splice(AbstractEpollStreamChannel.this.socket.intValue(), -1L, pipeOut.intValue(), -1L, length);
                if (localSplicedIn == 0) {
                    break;
                }
                splicedIn += localSplicedIn;
                length -= localSplicedIn;
            }
            return splicedIn;
        }
    }
    
    private final class SpliceInChannelTask extends SpliceInTask implements ChannelFutureListener
    {
        private final AbstractEpollStreamChannel ch;
        
        SpliceInChannelTask(final AbstractEpollStreamChannel ch, final int len, final ChannelPromise promise) {
            super(len, promise);
            this.ch = ch;
        }
        
        @Override
        public void operationComplete(final ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                this.promise.setFailure(future.cause());
            }
        }
        
        public boolean spliceIn(final RecvByteBufAllocator.Handle handle) {
            assert this.ch.eventLoop().inEventLoop();
            if (this.len == 0) {
                this.promise.setSuccess();
                return true;
            }
            try {
                FileDescriptor pipeOut = this.ch.pipeOut;
                if (pipeOut == null) {
                    final FileDescriptor[] pipe = FileDescriptor.pipe();
                    this.ch.pipeIn = pipe[0];
                    pipeOut = (this.ch.pipeOut = pipe[1]);
                }
                final int splicedIn = this.spliceIn(pipeOut, handle);
                if (splicedIn > 0) {
                    if (this.len != Integer.MAX_VALUE) {
                        this.len -= splicedIn;
                    }
                    ChannelPromise splicePromise;
                    if (this.len == 0) {
                        splicePromise = this.promise;
                    }
                    else {
                        splicePromise = this.ch.newPromise().addListener((GenericFutureListener<? extends Future<? super Void>>)this);
                    }
                    final boolean autoRead = AbstractEpollStreamChannel.this.config().isAutoRead();
                    this.ch.unsafe().write(new SpliceOutTask(this.ch, splicedIn, autoRead), splicePromise);
                    this.ch.unsafe().flush();
                    if (autoRead && !splicePromise.isDone()) {
                        AbstractEpollStreamChannel.this.config().setAutoRead(false);
                    }
                }
                return this.len == 0;
            }
            catch (final Throwable cause) {
                this.promise.setFailure(cause);
                return true;
            }
        }
    }
    
    private final class SpliceOutTask
    {
        private final AbstractEpollStreamChannel ch;
        private final boolean autoRead;
        private int len;
        
        SpliceOutTask(final AbstractEpollStreamChannel ch, final int len, final boolean autoRead) {
            this.ch = ch;
            this.len = len;
            this.autoRead = autoRead;
        }
        
        public boolean spliceOut() throws Exception {
            assert this.ch.eventLoop().inEventLoop();
            try {
                final int splicedOut = Native.splice(this.ch.pipeIn.intValue(), -1L, this.ch.socket.intValue(), -1L, this.len);
                this.len -= splicedOut;
                if (this.len == 0) {
                    if (this.autoRead) {
                        AbstractEpollStreamChannel.this.config().setAutoRead(true);
                    }
                    return true;
                }
                return false;
            }
            catch (final IOException e) {
                if (this.autoRead) {
                    AbstractEpollStreamChannel.this.config().setAutoRead(true);
                }
                throw e;
            }
        }
    }
    
    private final class SpliceFdTask extends SpliceInTask
    {
        private final FileDescriptor fd;
        private final ChannelPromise promise;
        private int offset;
        
        SpliceFdTask(final FileDescriptor fd, final int offset, final int len, final ChannelPromise promise) {
            super(len, promise);
            this.fd = fd;
            this.promise = promise;
            this.offset = offset;
        }
        
        public boolean spliceIn(final RecvByteBufAllocator.Handle handle) {
            assert AbstractEpollStreamChannel.this.eventLoop().inEventLoop();
            if (this.len == 0) {
                this.promise.setSuccess();
                return true;
            }
            try {
                final FileDescriptor[] pipe = FileDescriptor.pipe();
                final FileDescriptor pipeIn = pipe[0];
                final FileDescriptor pipeOut = pipe[1];
                try {
                    int splicedIn = this.spliceIn(pipeOut, handle);
                    if (splicedIn > 0) {
                        if (this.len != Integer.MAX_VALUE) {
                            this.len -= splicedIn;
                        }
                        do {
                            final int splicedOut = Native.splice(pipeIn.intValue(), -1L, this.fd.intValue(), this.offset, splicedIn);
                            this.offset += splicedOut;
                            splicedIn -= splicedOut;
                        } while (splicedIn > 0);
                        if (this.len == 0) {
                            this.promise.setSuccess();
                            return true;
                        }
                    }
                    return false;
                }
                finally {
                    safeClosePipe(pipeIn);
                    safeClosePipe(pipeOut);
                }
            }
            catch (final Throwable cause) {
                this.promise.setFailure(cause);
                return true;
            }
        }
    }
    
    private final class EpollSocketWritableByteChannel extends SocketWritableByteChannel
    {
        EpollSocketWritableByteChannel() {
            super(AbstractEpollStreamChannel.this.socket);
        }
        
        @Override
        protected ByteBufAllocator alloc() {
            return AbstractEpollStreamChannel.this.alloc();
        }
    }
}
