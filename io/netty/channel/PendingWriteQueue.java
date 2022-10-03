package io.netty.channel;

import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.internal.logging.InternalLogger;

public final class PendingWriteQueue
{
    private static final InternalLogger logger;
    private static final int PENDING_WRITE_OVERHEAD;
    private final ChannelOutboundInvoker invoker;
    private final EventExecutor executor;
    private final PendingBytesTracker tracker;
    private PendingWrite head;
    private PendingWrite tail;
    private int size;
    private long bytes;
    
    public PendingWriteQueue(final ChannelHandlerContext ctx) {
        this.tracker = PendingBytesTracker.newTracker(ctx.channel());
        this.invoker = ctx;
        this.executor = ctx.executor();
    }
    
    public PendingWriteQueue(final Channel channel) {
        this.tracker = PendingBytesTracker.newTracker(channel);
        this.invoker = channel;
        this.executor = channel.eventLoop();
    }
    
    public boolean isEmpty() {
        assert this.executor.inEventLoop();
        return this.head == null;
    }
    
    public int size() {
        assert this.executor.inEventLoop();
        return this.size;
    }
    
    public long bytes() {
        assert this.executor.inEventLoop();
        return this.bytes;
    }
    
    private int size(final Object msg) {
        int messageSize = this.tracker.size(msg);
        if (messageSize < 0) {
            messageSize = 0;
        }
        return messageSize + PendingWriteQueue.PENDING_WRITE_OVERHEAD;
    }
    
    public void add(final Object msg, final ChannelPromise promise) {
        assert this.executor.inEventLoop();
        ObjectUtil.checkNotNull(msg, "msg");
        ObjectUtil.checkNotNull(promise, "promise");
        final int messageSize = this.size(msg);
        final PendingWrite write = PendingWrite.newInstance(msg, messageSize, promise);
        final PendingWrite currentTail = this.tail;
        if (currentTail == null) {
            final PendingWrite pendingWrite = write;
            this.head = pendingWrite;
            this.tail = pendingWrite;
        }
        else {
            currentTail.next = write;
            this.tail = write;
        }
        ++this.size;
        this.bytes += messageSize;
        this.tracker.incrementPendingOutboundBytes(write.size);
    }
    
    public ChannelFuture removeAndWriteAll() {
        assert this.executor.inEventLoop();
        if (this.isEmpty()) {
            return null;
        }
        final ChannelPromise p = this.invoker.newPromise();
        final PromiseCombiner combiner = new PromiseCombiner(this.executor);
        try {
            for (PendingWrite write = this.head; write != null; write = this.head) {
                final PendingWrite pendingWrite = null;
                this.tail = pendingWrite;
                this.head = pendingWrite;
                this.size = 0;
                this.bytes = 0L;
                while (write != null) {
                    final PendingWrite next = write.next;
                    final Object msg = write.msg;
                    final ChannelPromise promise = write.promise;
                    this.recycle(write, false);
                    if (!(promise instanceof VoidChannelPromise)) {
                        combiner.add(promise);
                    }
                    this.invoker.write(msg, promise);
                    write = next;
                }
            }
            combiner.finish(p);
        }
        catch (final Throwable cause) {
            p.setFailure(cause);
        }
        this.assertEmpty();
        return p;
    }
    
    public void removeAndFailAll(final Throwable cause) {
        assert this.executor.inEventLoop();
        ObjectUtil.checkNotNull(cause, "cause");
        for (PendingWrite write = this.head; write != null; write = this.head) {
            final PendingWrite pendingWrite = null;
            this.tail = pendingWrite;
            this.head = pendingWrite;
            this.size = 0;
            this.bytes = 0L;
            while (write != null) {
                final PendingWrite next = write.next;
                ReferenceCountUtil.safeRelease(write.msg);
                final ChannelPromise promise = write.promise;
                this.recycle(write, false);
                safeFail(promise, cause);
                write = next;
            }
        }
        this.assertEmpty();
    }
    
    public void removeAndFail(final Throwable cause) {
        assert this.executor.inEventLoop();
        ObjectUtil.checkNotNull(cause, "cause");
        final PendingWrite write = this.head;
        if (write == null) {
            return;
        }
        ReferenceCountUtil.safeRelease(write.msg);
        final ChannelPromise promise = write.promise;
        safeFail(promise, cause);
        this.recycle(write, true);
    }
    
    private void assertEmpty() {
        assert this.tail == null && this.head == null && this.size == 0;
    }
    
    public ChannelFuture removeAndWrite() {
        assert this.executor.inEventLoop();
        final PendingWrite write = this.head;
        if (write == null) {
            return null;
        }
        final Object msg = write.msg;
        final ChannelPromise promise = write.promise;
        this.recycle(write, true);
        return this.invoker.write(msg, promise);
    }
    
    public ChannelPromise remove() {
        assert this.executor.inEventLoop();
        final PendingWrite write = this.head;
        if (write == null) {
            return null;
        }
        final ChannelPromise promise = write.promise;
        ReferenceCountUtil.safeRelease(write.msg);
        this.recycle(write, true);
        return promise;
    }
    
    public Object current() {
        assert this.executor.inEventLoop();
        final PendingWrite write = this.head;
        if (write == null) {
            return null;
        }
        return write.msg;
    }
    
    private void recycle(final PendingWrite write, final boolean update) {
        final PendingWrite next = write.next;
        final long writeSize = write.size;
        if (update) {
            if (next == null) {
                final PendingWrite pendingWrite = null;
                this.tail = pendingWrite;
                this.head = pendingWrite;
                this.size = 0;
                this.bytes = 0L;
            }
            else {
                this.head = next;
                --this.size;
                this.bytes -= writeSize;
                assert this.size > 0 && this.bytes >= 0L;
            }
        }
        write.recycle();
        this.tracker.decrementPendingOutboundBytes(writeSize);
    }
    
    private static void safeFail(final ChannelPromise promise, final Throwable cause) {
        if (!(promise instanceof VoidChannelPromise) && !promise.tryFailure(cause)) {
            PendingWriteQueue.logger.warn("Failed to mark a promise as failure because it's done already: {}", promise, cause);
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(PendingWriteQueue.class);
        PENDING_WRITE_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.pendingWriteSizeOverhead", 64);
    }
    
    static final class PendingWrite
    {
        private static final ObjectPool<PendingWrite> RECYCLER;
        private final ObjectPool.Handle<PendingWrite> handle;
        private PendingWrite next;
        private long size;
        private ChannelPromise promise;
        private Object msg;
        
        private PendingWrite(final ObjectPool.Handle<PendingWrite> handle) {
            this.handle = handle;
        }
        
        static PendingWrite newInstance(final Object msg, final int size, final ChannelPromise promise) {
            final PendingWrite write = PendingWrite.RECYCLER.get();
            write.size = size;
            write.msg = msg;
            write.promise = promise;
            return write;
        }
        
        private void recycle() {
            this.size = 0L;
            this.next = null;
            this.msg = null;
            this.promise = null;
            this.handle.recycle(this);
        }
        
        static {
            RECYCLER = ObjectPool.newPool((ObjectPool.ObjectCreator<PendingWrite>)new ObjectPool.ObjectCreator<PendingWrite>() {
                @Override
                public PendingWrite newObject(final ObjectPool.Handle<PendingWrite> handle) {
                    return new PendingWrite((ObjectPool.Handle)handle);
                }
            });
        }
    }
}
