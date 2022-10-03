package io.netty.channel.kqueue;

import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.SocketWritableByteChannel;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelConfig;
import java.util.concurrent.Executor;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelFuture;
import io.netty.util.internal.StringUtil;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.channel.FileRegion;
import io.netty.channel.DefaultFileRegion;
import java.io.IOException;
import io.netty.channel.unix.IovArray;
import java.nio.ByteBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelOutboundBuffer;
import java.net.SocketAddress;
import io.netty.channel.Channel;
import java.nio.channels.WritableByteChannel;
import io.netty.channel.ChannelMetadata;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.socket.DuplexChannel;

public abstract class AbstractKQueueStreamChannel extends AbstractKQueueChannel implements DuplexChannel
{
    private static final InternalLogger logger;
    private static final ChannelMetadata METADATA;
    private static final String EXPECTED_TYPES;
    private WritableByteChannel byteChannel;
    private final Runnable flushTask;
    
    AbstractKQueueStreamChannel(final Channel parent, final BsdSocket fd, final boolean active) {
        super(parent, fd, active);
        this.flushTask = new Runnable() {
            @Override
            public void run() {
                ((AbstractKQueueUnsafe)AbstractKQueueStreamChannel.this.unsafe()).flush0();
            }
        };
    }
    
    AbstractKQueueStreamChannel(final Channel parent, final BsdSocket fd, final SocketAddress remote) {
        super(parent, fd, remote);
        this.flushTask = new Runnable() {
            @Override
            public void run() {
                ((AbstractKQueueUnsafe)AbstractKQueueStreamChannel.this.unsafe()).flush0();
            }
        };
    }
    
    AbstractKQueueStreamChannel(final BsdSocket fd) {
        this(null, fd, AbstractKQueueChannel.isSoErrorZero(fd));
    }
    
    @Override
    protected AbstractKQueueUnsafe newUnsafe() {
        return new KQueueStreamUnsafe();
    }
    
    @Override
    public ChannelMetadata metadata() {
        return AbstractKQueueStreamChannel.METADATA;
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
        final long regionCount = region.count();
        final long offset = region.transferred();
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
            this.byteChannel = new KQueueSocketWritableByteChannel();
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
                    this.writeFilter(false);
                    return;
                }
                writeSpinCount -= this.doWriteSingle(in);
            }
        } while (writeSpinCount > 0);
        if (writeSpinCount == 0) {
            this.writeFilter(false);
            this.eventLoop().execute(this.flushTask);
        }
        else {
            this.writeFilter(true);
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
        throw new Error();
    }
    
    private int doWriteMultiple(final ChannelOutboundBuffer in) throws Exception {
        final long maxBytesPerGatheringWrite = this.config().getMaxBytesPerGatheringWrite();
        final IovArray array = ((KQueueEventLoop)this.eventLoop()).cleanArray();
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
        if (msg instanceof FileRegion) {
            return msg;
        }
        throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + AbstractKQueueStreamChannel.EXPECTED_TYPES);
    }
    
    @Override
    protected final void doShutdownOutput() throws Exception {
        this.socket.shutdown(false, true);
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
                    ((AbstractUnsafe)AbstractKQueueStreamChannel.this.unsafe()).shutdownOutput(promise);
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
        final EventLoop loop = this.eventLoop();
        if (loop.inEventLoop()) {
            this.shutdownInput0(promise);
        }
        else {
            loop.execute(new Runnable() {
                @Override
                public void run() {
                    AbstractKQueueStreamChannel.this.shutdownInput0(promise);
                }
            });
        }
        return promise;
    }
    
    private void shutdownInput0(final ChannelPromise promise) {
        try {
            this.socket.shutdown(true, false);
        }
        catch (final Throwable cause) {
            promise.setFailure(cause);
            return;
        }
        promise.setSuccess();
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
                    AbstractKQueueStreamChannel.this.shutdownOutputDone(shutdownOutputFuture, promise);
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
                AbstractKQueueStreamChannel.logger.debug("Exception suppressed because a previous exception occurred.", shutdownInputCause);
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
    
    static {
        logger = InternalLoggerFactory.getInstance(AbstractKQueueStreamChannel.class);
        METADATA = new ChannelMetadata(false, 16);
        EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DefaultFileRegion.class) + ')';
    }
    
    class KQueueStreamUnsafe extends AbstractKQueueUnsafe
    {
        @Override
        protected Executor prepareToClose() {
            return super.prepareToClose();
        }
        
        @Override
        void readReady(final KQueueRecvByteAllocatorHandle allocHandle) {
            final ChannelConfig config = AbstractKQueueStreamChannel.this.config();
            if (AbstractKQueueStreamChannel.this.shouldBreakReadReady(config)) {
                this.clearReadFilter0();
                return;
            }
            final ChannelPipeline pipeline = AbstractKQueueStreamChannel.this.pipeline();
            final ByteBufAllocator allocator = config.getAllocator();
            allocHandle.reset(config);
            this.readReadyBefore();
            ByteBuf byteBuf = null;
            boolean close = false;
            try {
                do {
                    byteBuf = allocHandle.allocate(allocator);
                    allocHandle.lastBytesRead(AbstractKQueueStreamChannel.this.doReadBytes(byteBuf));
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
                        if (AbstractKQueueStreamChannel.this.shouldBreakReadReady(config)) {
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
                this.readReadyFinally(config);
            }
        }
        
        private void handleReadException(final ChannelPipeline pipeline, final ByteBuf byteBuf, final Throwable cause, final boolean close, final KQueueRecvByteAllocatorHandle allocHandle) {
            if (byteBuf != null) {
                if (byteBuf.isReadable()) {
                    this.readPending = false;
                    pipeline.fireChannelRead((Object)byteBuf);
                }
                else {
                    byteBuf.release();
                }
            }
            if (!this.failConnectPromise(cause)) {
                allocHandle.readComplete();
                pipeline.fireChannelReadComplete();
                pipeline.fireExceptionCaught(cause);
                if (close || cause instanceof OutOfMemoryError || cause instanceof IOException) {
                    this.shutdownInput(false);
                }
            }
        }
    }
    
    private final class KQueueSocketWritableByteChannel extends SocketWritableByteChannel
    {
        KQueueSocketWritableByteChannel() {
            super(AbstractKQueueStreamChannel.this.socket);
        }
        
        @Override
        protected ByteBufAllocator alloc() {
            return AbstractKQueueStreamChannel.this.alloc();
        }
    }
}
