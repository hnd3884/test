package io.netty.handler.stream;

import io.netty.channel.ChannelProgressivePromise;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import java.nio.channels.ClosedChannelException;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayDeque;
import io.netty.channel.ChannelHandlerContext;
import java.util.Queue;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.ChannelDuplexHandler;

public class ChunkedWriteHandler extends ChannelDuplexHandler
{
    private static final InternalLogger logger;
    private final Queue<PendingWrite> queue;
    private volatile ChannelHandlerContext ctx;
    
    public ChunkedWriteHandler() {
        this.queue = new ArrayDeque<PendingWrite>();
    }
    
    @Deprecated
    public ChunkedWriteHandler(final int maxPendingWrites) {
        this.queue = new ArrayDeque<PendingWrite>();
        ObjectUtil.checkPositive(maxPendingWrites, "maxPendingWrites");
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }
    
    public void resumeTransfer() {
        final ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            return;
        }
        if (ctx.executor().inEventLoop()) {
            this.resumeTransfer0(ctx);
        }
        else {
            ctx.executor().execute(new Runnable() {
                @Override
                public void run() {
                    ChunkedWriteHandler.this.resumeTransfer0(ctx);
                }
            });
        }
    }
    
    private void resumeTransfer0(final ChannelHandlerContext ctx) {
        try {
            this.doFlush(ctx);
        }
        catch (final Exception e) {
            ChunkedWriteHandler.logger.warn("Unexpected exception while sending chunks.", e);
        }
    }
    
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        this.queue.add(new PendingWrite(msg, promise));
    }
    
    @Override
    public void flush(final ChannelHandlerContext ctx) throws Exception {
        this.doFlush(ctx);
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        this.doFlush(ctx);
        ctx.fireChannelInactive();
    }
    
    @Override
    public void channelWritabilityChanged(final ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isWritable()) {
            this.doFlush(ctx);
        }
        ctx.fireChannelWritabilityChanged();
    }
    
    private void discard(Throwable cause) {
        while (true) {
            final PendingWrite currentWrite = this.queue.poll();
            if (currentWrite == null) {
                break;
            }
            final Object message = currentWrite.msg;
            if (message instanceof ChunkedInput) {
                final ChunkedInput<?> in = (ChunkedInput<?>)message;
                boolean endOfInput;
                long inputLength;
                try {
                    endOfInput = in.isEndOfInput();
                    inputLength = in.length();
                    closeInput(in);
                }
                catch (final Exception e) {
                    closeInput(in);
                    currentWrite.fail(e);
                    if (!ChunkedWriteHandler.logger.isWarnEnabled()) {
                        continue;
                    }
                    ChunkedWriteHandler.logger.warn(ChunkedInput.class.getSimpleName() + " failed", e);
                    continue;
                }
                if (!endOfInput) {
                    if (cause == null) {
                        cause = new ClosedChannelException();
                    }
                    currentWrite.fail(cause);
                }
                else {
                    currentWrite.success(inputLength);
                }
            }
            else {
                if (cause == null) {
                    cause = new ClosedChannelException();
                }
                currentWrite.fail(cause);
            }
        }
    }
    
    private void doFlush(final ChannelHandlerContext ctx) {
        final Channel channel = ctx.channel();
        if (!channel.isActive()) {
            this.discard(null);
            return;
        }
        boolean requiresFlush = true;
        final ByteBufAllocator allocator = ctx.alloc();
        while (channel.isWritable()) {
            final PendingWrite currentWrite = this.queue.peek();
            if (currentWrite == null) {
                break;
            }
            if (currentWrite.promise.isDone()) {
                this.queue.remove();
            }
            else {
                final Object pendingMessage = currentWrite.msg;
                if (pendingMessage instanceof ChunkedInput) {
                    final ChunkedInput<?> chunks = (ChunkedInput<?>)pendingMessage;
                    Object message = null;
                    boolean endOfInput;
                    boolean suspend;
                    try {
                        message = chunks.readChunk(allocator);
                        endOfInput = chunks.isEndOfInput();
                        suspend = (message == null && !endOfInput);
                    }
                    catch (final Throwable t) {
                        this.queue.remove();
                        if (message != null) {
                            ReferenceCountUtil.release(message);
                        }
                        closeInput(chunks);
                        currentWrite.fail(t);
                        break;
                    }
                    if (suspend) {
                        break;
                    }
                    if (message == null) {
                        message = Unpooled.EMPTY_BUFFER;
                    }
                    if (endOfInput) {
                        this.queue.remove();
                    }
                    final ChannelFuture f = ctx.writeAndFlush(message);
                    if (endOfInput) {
                        if (f.isDone()) {
                            handleEndOfInputFuture(f, currentWrite);
                        }
                        else {
                            f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                                @Override
                                public void operationComplete(final ChannelFuture future) {
                                    handleEndOfInputFuture(future, currentWrite);
                                }
                            });
                        }
                    }
                    else {
                        final boolean resume = !channel.isWritable();
                        if (f.isDone()) {
                            this.handleFuture(f, currentWrite, resume);
                        }
                        else {
                            f.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                                @Override
                                public void operationComplete(final ChannelFuture future) {
                                    ChunkedWriteHandler.this.handleFuture(future, currentWrite, resume);
                                }
                            });
                        }
                    }
                    requiresFlush = false;
                }
                else {
                    this.queue.remove();
                    ctx.write(pendingMessage, currentWrite.promise);
                    requiresFlush = true;
                }
                if (!channel.isActive()) {
                    this.discard(new ClosedChannelException());
                    break;
                }
                continue;
            }
        }
        if (requiresFlush) {
            ctx.flush();
        }
    }
    
    private static void handleEndOfInputFuture(final ChannelFuture future, final PendingWrite currentWrite) {
        final ChunkedInput<?> input = (ChunkedInput<?>)currentWrite.msg;
        if (!future.isSuccess()) {
            closeInput(input);
            currentWrite.fail(future.cause());
        }
        else {
            final long inputProgress = input.progress();
            final long inputLength = input.length();
            closeInput(input);
            currentWrite.progress(inputProgress, inputLength);
            currentWrite.success(inputLength);
        }
    }
    
    private void handleFuture(final ChannelFuture future, final PendingWrite currentWrite, final boolean resume) {
        final ChunkedInput<?> input = (ChunkedInput<?>)currentWrite.msg;
        if (!future.isSuccess()) {
            closeInput(input);
            currentWrite.fail(future.cause());
        }
        else {
            currentWrite.progress(input.progress(), input.length());
            if (resume && future.channel().isWritable()) {
                this.resumeTransfer();
            }
        }
    }
    
    private static void closeInput(final ChunkedInput<?> chunks) {
        try {
            chunks.close();
        }
        catch (final Throwable t) {
            if (ChunkedWriteHandler.logger.isWarnEnabled()) {
                ChunkedWriteHandler.logger.warn("Failed to close a chunked input.", t);
            }
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(ChunkedWriteHandler.class);
    }
    
    private static final class PendingWrite
    {
        final Object msg;
        final ChannelPromise promise;
        
        PendingWrite(final Object msg, final ChannelPromise promise) {
            this.msg = msg;
            this.promise = promise;
        }
        
        void fail(final Throwable cause) {
            ReferenceCountUtil.release(this.msg);
            this.promise.tryFailure(cause);
        }
        
        void success(final long total) {
            if (this.promise.isDone()) {
                return;
            }
            this.progress(total, total);
            this.promise.trySuccess();
        }
        
        void progress(final long progress, final long total) {
            if (this.promise instanceof ChannelProgressivePromise) {
                ((ChannelProgressivePromise)this.promise).tryProgress(progress, total);
            }
        }
    }
}
