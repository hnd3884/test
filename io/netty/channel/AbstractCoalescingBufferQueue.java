package io.netty.channel;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.buffer.CompositeByteBuf;
import java.util.Collection;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.buffer.ByteBuf;
import java.util.ArrayDeque;
import io.netty.util.internal.logging.InternalLogger;

public abstract class AbstractCoalescingBufferQueue
{
    private static final InternalLogger logger;
    private final ArrayDeque<Object> bufAndListenerPairs;
    private final PendingBytesTracker tracker;
    private int readableBytes;
    
    protected AbstractCoalescingBufferQueue(final Channel channel, final int initSize) {
        this.bufAndListenerPairs = new ArrayDeque<Object>(initSize);
        this.tracker = ((channel == null) ? null : PendingBytesTracker.newTracker(channel));
    }
    
    public final void addFirst(final ByteBuf buf, final ChannelPromise promise) {
        this.addFirst(buf, toChannelFutureListener(promise));
    }
    
    private void addFirst(final ByteBuf buf, final ChannelFutureListener listener) {
        if (listener != null) {
            this.bufAndListenerPairs.addFirst(listener);
        }
        this.bufAndListenerPairs.addFirst(buf);
        this.incrementReadableBytes(buf.readableBytes());
    }
    
    public final void add(final ByteBuf buf) {
        this.add(buf, (ChannelFutureListener)null);
    }
    
    public final void add(final ByteBuf buf, final ChannelPromise promise) {
        this.add(buf, toChannelFutureListener(promise));
    }
    
    public final void add(final ByteBuf buf, final ChannelFutureListener listener) {
        this.bufAndListenerPairs.add(buf);
        if (listener != null) {
            this.bufAndListenerPairs.add(listener);
        }
        this.incrementReadableBytes(buf.readableBytes());
    }
    
    public final ByteBuf removeFirst(final ChannelPromise aggregatePromise) {
        Object entry = this.bufAndListenerPairs.poll();
        if (entry == null) {
            return null;
        }
        assert entry instanceof ByteBuf;
        final ByteBuf result = (ByteBuf)entry;
        this.decrementReadableBytes(result.readableBytes());
        entry = this.bufAndListenerPairs.peek();
        if (entry instanceof ChannelFutureListener) {
            aggregatePromise.addListener((GenericFutureListener<? extends Future<? super Void>>)entry);
            this.bufAndListenerPairs.poll();
        }
        return result;
    }
    
    public final ByteBuf remove(final ByteBufAllocator alloc, int bytes, final ChannelPromise aggregatePromise) {
        ObjectUtil.checkPositiveOrZero(bytes, "bytes");
        ObjectUtil.checkNotNull(aggregatePromise, "aggregatePromise");
        if (!this.bufAndListenerPairs.isEmpty()) {
            bytes = Math.min(bytes, this.readableBytes);
            ByteBuf toReturn = null;
            ByteBuf entryBuffer = null;
            final int originalBytes = bytes;
            try {
                while (true) {
                    final Object entry = this.bufAndListenerPairs.poll();
                    if (entry == null) {
                        break;
                    }
                    if (entry instanceof ChannelFutureListener) {
                        aggregatePromise.addListener((GenericFutureListener<? extends Future<? super Void>>)entry);
                    }
                    else {
                        entryBuffer = (ByteBuf)entry;
                        if (entryBuffer.readableBytes() > bytes) {
                            this.bufAndListenerPairs.addFirst(entryBuffer);
                            if (bytes > 0) {
                                entryBuffer = entryBuffer.readRetainedSlice(bytes);
                                toReturn = ((toReturn == null) ? this.composeFirst(alloc, entryBuffer) : this.compose(alloc, toReturn, entryBuffer));
                                bytes = 0;
                                break;
                            }
                            break;
                        }
                        else {
                            bytes -= entryBuffer.readableBytes();
                            toReturn = ((toReturn == null) ? this.composeFirst(alloc, entryBuffer) : this.compose(alloc, toReturn, entryBuffer));
                            entryBuffer = null;
                        }
                    }
                }
            }
            catch (final Throwable cause) {
                ReferenceCountUtil.safeRelease(entryBuffer);
                ReferenceCountUtil.safeRelease(toReturn);
                aggregatePromise.setFailure(cause);
                PlatformDependent.throwException(cause);
            }
            this.decrementReadableBytes(originalBytes - bytes);
            return toReturn;
        }
        assert this.readableBytes == 0;
        return this.removeEmptyValue();
    }
    
    public final int readableBytes() {
        return this.readableBytes;
    }
    
    public final boolean isEmpty() {
        return this.bufAndListenerPairs.isEmpty();
    }
    
    public final void releaseAndFailAll(final ChannelOutboundInvoker invoker, final Throwable cause) {
        this.releaseAndCompleteAll(invoker.newFailedFuture(cause));
    }
    
    public final void copyTo(final AbstractCoalescingBufferQueue dest) {
        dest.bufAndListenerPairs.addAll(this.bufAndListenerPairs);
        dest.incrementReadableBytes(this.readableBytes);
    }
    
    public final void writeAndRemoveAll(final ChannelHandlerContext ctx) {
        Throwable pending = null;
        ByteBuf previousBuf = null;
        while (true) {
            final Object entry = this.bufAndListenerPairs.poll();
            try {
                if (entry == null) {
                    if (previousBuf != null) {
                        this.decrementReadableBytes(previousBuf.readableBytes());
                        ctx.write(previousBuf, ctx.voidPromise());
                    }
                    break;
                }
                if (entry instanceof ByteBuf) {
                    if (previousBuf != null) {
                        this.decrementReadableBytes(previousBuf.readableBytes());
                        ctx.write(previousBuf, ctx.voidPromise());
                    }
                    previousBuf = (ByteBuf)entry;
                }
                else if (entry instanceof ChannelPromise) {
                    this.decrementReadableBytes(previousBuf.readableBytes());
                    ctx.write(previousBuf, (ChannelPromise)entry);
                    previousBuf = null;
                }
                else {
                    this.decrementReadableBytes(previousBuf.readableBytes());
                    ctx.write(previousBuf).addListener((GenericFutureListener<? extends Future<? super Void>>)entry);
                    previousBuf = null;
                }
            }
            catch (final Throwable t) {
                if (pending == null) {
                    pending = t;
                }
                else {
                    AbstractCoalescingBufferQueue.logger.info("Throwable being suppressed because Throwable {} is already pending", pending, t);
                }
            }
        }
        if (pending != null) {
            throw new IllegalStateException(pending);
        }
    }
    
    @Override
    public String toString() {
        return "bytes: " + this.readableBytes + " buffers: " + (this.size() >> 1);
    }
    
    protected abstract ByteBuf compose(final ByteBufAllocator p0, final ByteBuf p1, final ByteBuf p2);
    
    protected final ByteBuf composeIntoComposite(final ByteBufAllocator alloc, final ByteBuf cumulation, final ByteBuf next) {
        final CompositeByteBuf composite = alloc.compositeBuffer(this.size() + 2);
        try {
            composite.addComponent(true, cumulation);
            composite.addComponent(true, next);
        }
        catch (final Throwable cause) {
            composite.release();
            ReferenceCountUtil.safeRelease(next);
            PlatformDependent.throwException(cause);
        }
        return composite;
    }
    
    protected final ByteBuf copyAndCompose(final ByteBufAllocator alloc, final ByteBuf cumulation, final ByteBuf next) {
        final ByteBuf newCumulation = alloc.ioBuffer(cumulation.readableBytes() + next.readableBytes());
        try {
            newCumulation.writeBytes(cumulation).writeBytes(next);
        }
        catch (final Throwable cause) {
            newCumulation.release();
            ReferenceCountUtil.safeRelease(next);
            PlatformDependent.throwException(cause);
        }
        cumulation.release();
        next.release();
        return newCumulation;
    }
    
    protected ByteBuf composeFirst(final ByteBufAllocator allocator, final ByteBuf first) {
        return first;
    }
    
    protected abstract ByteBuf removeEmptyValue();
    
    protected final int size() {
        return this.bufAndListenerPairs.size();
    }
    
    private void releaseAndCompleteAll(final ChannelFuture future) {
        Throwable pending = null;
        while (true) {
            final Object entry = this.bufAndListenerPairs.poll();
            if (entry == null) {
                break;
            }
            try {
                if (entry instanceof ByteBuf) {
                    final ByteBuf buffer = (ByteBuf)entry;
                    this.decrementReadableBytes(buffer.readableBytes());
                    ReferenceCountUtil.safeRelease(buffer);
                }
                else {
                    ((ChannelFutureListener)entry).operationComplete(future);
                }
            }
            catch (final Throwable t) {
                if (pending == null) {
                    pending = t;
                }
                else {
                    AbstractCoalescingBufferQueue.logger.info("Throwable being suppressed because Throwable {} is already pending", pending, t);
                }
            }
        }
        if (pending != null) {
            throw new IllegalStateException(pending);
        }
    }
    
    private void incrementReadableBytes(final int increment) {
        final int nextReadableBytes = this.readableBytes + increment;
        if (nextReadableBytes < this.readableBytes) {
            throw new IllegalStateException("buffer queue length overflow: " + this.readableBytes + " + " + increment);
        }
        this.readableBytes = nextReadableBytes;
        if (this.tracker != null) {
            this.tracker.incrementPendingOutboundBytes(increment);
        }
    }
    
    private void decrementReadableBytes(final int decrement) {
        this.readableBytes -= decrement;
        assert this.readableBytes >= 0;
        if (this.tracker != null) {
            this.tracker.decrementPendingOutboundBytes(decrement);
        }
    }
    
    private static ChannelFutureListener toChannelFutureListener(final ChannelPromise promise) {
        return promise.isVoid() ? null : new DelegatingChannelPromiseNotifier(promise);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(AbstractCoalescingBufferQueue.class);
    }
}
