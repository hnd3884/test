package io.netty.channel;

import io.netty.buffer.Unpooled;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PromiseNotificationUtil;
import java.nio.channels.ClosedChannelException;
import io.netty.util.internal.InternalThreadLocalMap;
import java.util.Arrays;
import io.netty.util.ReferenceCountUtil;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBuf;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.nio.ByteBuffer;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.logging.InternalLogger;

public final class ChannelOutboundBuffer
{
    static final int CHANNEL_OUTBOUND_BUFFER_ENTRY_OVERHEAD;
    private static final InternalLogger logger;
    private static final FastThreadLocal<ByteBuffer[]> NIO_BUFFERS;
    private final Channel channel;
    private Entry flushedEntry;
    private Entry unflushedEntry;
    private Entry tailEntry;
    private int flushed;
    private int nioBufferCount;
    private long nioBufferSize;
    private boolean inFail;
    private static final AtomicLongFieldUpdater<ChannelOutboundBuffer> TOTAL_PENDING_SIZE_UPDATER;
    private volatile long totalPendingSize;
    private static final AtomicIntegerFieldUpdater<ChannelOutboundBuffer> UNWRITABLE_UPDATER;
    private volatile int unwritable;
    private volatile Runnable fireChannelWritabilityChangedTask;
    
    ChannelOutboundBuffer(final AbstractChannel channel) {
        this.channel = channel;
    }
    
    public void addMessage(final Object msg, final int size, final ChannelPromise promise) {
        final Entry entry = Entry.newInstance(msg, size, total(msg), promise);
        if (this.tailEntry == null) {
            this.flushedEntry = null;
        }
        else {
            final Entry tail = this.tailEntry;
            tail.next = entry;
        }
        this.tailEntry = entry;
        if (this.unflushedEntry == null) {
            this.unflushedEntry = entry;
        }
        this.incrementPendingOutboundBytes(entry.pendingSize, false);
    }
    
    public void addFlush() {
        Entry entry = this.unflushedEntry;
        if (entry != null) {
            if (this.flushedEntry == null) {
                this.flushedEntry = entry;
            }
            do {
                ++this.flushed;
                if (!entry.promise.setUncancellable()) {
                    final int pending = entry.cancel();
                    this.decrementPendingOutboundBytes(pending, false, true);
                }
                entry = entry.next;
            } while (entry != null);
            this.unflushedEntry = null;
        }
    }
    
    void incrementPendingOutboundBytes(final long size) {
        this.incrementPendingOutboundBytes(size, true);
    }
    
    private void incrementPendingOutboundBytes(final long size, final boolean invokeLater) {
        if (size == 0L) {
            return;
        }
        final long newWriteBufferSize = ChannelOutboundBuffer.TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, size);
        if (newWriteBufferSize > this.channel.config().getWriteBufferHighWaterMark()) {
            this.setUnwritable(invokeLater);
        }
    }
    
    void decrementPendingOutboundBytes(final long size) {
        this.decrementPendingOutboundBytes(size, true, true);
    }
    
    private void decrementPendingOutboundBytes(final long size, final boolean invokeLater, final boolean notifyWritability) {
        if (size == 0L) {
            return;
        }
        final long newWriteBufferSize = ChannelOutboundBuffer.TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
        if (notifyWritability && newWriteBufferSize < this.channel.config().getWriteBufferLowWaterMark()) {
            this.setWritable(invokeLater);
        }
    }
    
    private static long total(final Object msg) {
        if (msg instanceof ByteBuf) {
            return ((ByteBuf)msg).readableBytes();
        }
        if (msg instanceof FileRegion) {
            return ((FileRegion)msg).count();
        }
        if (msg instanceof ByteBufHolder) {
            return ((ByteBufHolder)msg).content().readableBytes();
        }
        return -1L;
    }
    
    public Object current() {
        final Entry entry = this.flushedEntry;
        if (entry == null) {
            return null;
        }
        return entry.msg;
    }
    
    public long currentProgress() {
        final Entry entry = this.flushedEntry;
        if (entry == null) {
            return 0L;
        }
        return entry.progress;
    }
    
    public void progress(final long amount) {
        final Entry e = this.flushedEntry;
        assert e != null;
        final ChannelPromise p = e.promise;
        final long progress = e.progress + amount;
        e.progress = progress;
        if (p instanceof ChannelProgressivePromise) {
            ((ChannelProgressivePromise)p).tryProgress(progress, e.total);
        }
    }
    
    public boolean remove() {
        final Entry e = this.flushedEntry;
        if (e == null) {
            this.clearNioBuffers();
            return false;
        }
        final Object msg = e.msg;
        final ChannelPromise promise = e.promise;
        final int size = e.pendingSize;
        this.removeEntry(e);
        if (!e.cancelled) {
            ReferenceCountUtil.safeRelease(msg);
            safeSuccess(promise);
            this.decrementPendingOutboundBytes(size, false, true);
        }
        e.recycle();
        return true;
    }
    
    public boolean remove(final Throwable cause) {
        return this.remove0(cause, true);
    }
    
    private boolean remove0(final Throwable cause, final boolean notifyWritability) {
        final Entry e = this.flushedEntry;
        if (e == null) {
            this.clearNioBuffers();
            return false;
        }
        final Object msg = e.msg;
        final ChannelPromise promise = e.promise;
        final int size = e.pendingSize;
        this.removeEntry(e);
        if (!e.cancelled) {
            ReferenceCountUtil.safeRelease(msg);
            safeFail(promise, cause);
            this.decrementPendingOutboundBytes(size, false, notifyWritability);
        }
        e.recycle();
        return true;
    }
    
    private void removeEntry(final Entry e) {
        final int flushed = this.flushed - 1;
        this.flushed = flushed;
        if (flushed == 0) {
            this.flushedEntry = null;
            if (e == this.tailEntry) {
                this.tailEntry = null;
                this.unflushedEntry = null;
            }
        }
        else {
            this.flushedEntry = e.next;
        }
    }
    
    public void removeBytes(long writtenBytes) {
        while (true) {
            final Object msg = this.current();
            if (!(msg instanceof ByteBuf)) {
                assert writtenBytes == 0L;
                break;
            }
            else {
                final ByteBuf buf = (ByteBuf)msg;
                final int readerIndex = buf.readerIndex();
                final int readableBytes = buf.writerIndex() - readerIndex;
                if (readableBytes <= writtenBytes) {
                    if (writtenBytes != 0L) {
                        this.progress(readableBytes);
                        writtenBytes -= readableBytes;
                    }
                    this.remove();
                }
                else {
                    if (writtenBytes != 0L) {
                        buf.readerIndex(readerIndex + (int)writtenBytes);
                        this.progress(writtenBytes);
                        break;
                    }
                    break;
                }
            }
        }
        this.clearNioBuffers();
    }
    
    private void clearNioBuffers() {
        final int count = this.nioBufferCount;
        if (count > 0) {
            this.nioBufferCount = 0;
            Arrays.fill(ChannelOutboundBuffer.NIO_BUFFERS.get(), 0, count, null);
        }
    }
    
    public ByteBuffer[] nioBuffers() {
        return this.nioBuffers(Integer.MAX_VALUE, 2147483647L);
    }
    
    public ByteBuffer[] nioBuffers(final int maxCount, final long maxBytes) {
        assert maxCount > 0;
        assert maxBytes > 0L;
        long nioBufferSize = 0L;
        int nioBufferCount = 0;
        final InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
        ByteBuffer[] nioBuffers = ChannelOutboundBuffer.NIO_BUFFERS.get(threadLocalMap);
        for (Entry entry = this.flushedEntry; this.isFlushedEntry(entry) && entry.msg instanceof ByteBuf; entry = entry.next) {
            if (!entry.cancelled) {
                final ByteBuf buf = (ByteBuf)entry.msg;
                final int readerIndex = buf.readerIndex();
                final int readableBytes = buf.writerIndex() - readerIndex;
                if (readableBytes > 0) {
                    if (maxBytes - readableBytes < nioBufferSize && nioBufferCount != 0) {
                        break;
                    }
                    nioBufferSize += readableBytes;
                    int count = entry.count;
                    if (count == -1) {
                        count = (entry.count = buf.nioBufferCount());
                    }
                    final int neededSpace = Math.min(maxCount, nioBufferCount + count);
                    if (neededSpace > nioBuffers.length) {
                        nioBuffers = expandNioBufferArray(nioBuffers, neededSpace, nioBufferCount);
                        ChannelOutboundBuffer.NIO_BUFFERS.set(threadLocalMap, nioBuffers);
                    }
                    if (count == 1) {
                        ByteBuffer nioBuf = entry.buf;
                        if (nioBuf == null) {
                            nioBuf = (entry.buf = buf.internalNioBuffer(readerIndex, readableBytes));
                        }
                        nioBuffers[nioBufferCount++] = nioBuf;
                    }
                    else {
                        nioBufferCount = nioBuffers(entry, buf, nioBuffers, nioBufferCount, maxCount);
                    }
                    if (nioBufferCount >= maxCount) {
                        break;
                    }
                }
            }
        }
        this.nioBufferCount = nioBufferCount;
        this.nioBufferSize = nioBufferSize;
        return nioBuffers;
    }
    
    private static int nioBuffers(final Entry entry, final ByteBuf buf, final ByteBuffer[] nioBuffers, int nioBufferCount, final int maxCount) {
        ByteBuffer[] nioBufs = entry.bufs;
        if (nioBufs == null) {
            nioBufs = (entry.bufs = buf.nioBuffers());
        }
        for (int i = 0; i < nioBufs.length && nioBufferCount < maxCount; ++i) {
            final ByteBuffer nioBuf = nioBufs[i];
            if (nioBuf == null) {
                break;
            }
            if (nioBuf.hasRemaining()) {
                nioBuffers[nioBufferCount++] = nioBuf;
            }
        }
        return nioBufferCount;
    }
    
    private static ByteBuffer[] expandNioBufferArray(final ByteBuffer[] array, final int neededSpace, final int size) {
        int newCapacity = array.length;
        do {
            newCapacity <<= 1;
            if (newCapacity < 0) {
                throw new IllegalStateException();
            }
        } while (neededSpace > newCapacity);
        final ByteBuffer[] newArray = new ByteBuffer[newCapacity];
        System.arraycopy(array, 0, newArray, 0, size);
        return newArray;
    }
    
    public int nioBufferCount() {
        return this.nioBufferCount;
    }
    
    public long nioBufferSize() {
        return this.nioBufferSize;
    }
    
    public boolean isWritable() {
        return this.unwritable == 0;
    }
    
    public boolean getUserDefinedWritability(final int index) {
        return (this.unwritable & writabilityMask(index)) == 0x0;
    }
    
    public void setUserDefinedWritability(final int index, final boolean writable) {
        if (writable) {
            this.setUserDefinedWritability(index);
        }
        else {
            this.clearUserDefinedWritability(index);
        }
    }
    
    private void setUserDefinedWritability(final int index) {
        final int mask = ~writabilityMask(index);
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = (oldValue & mask);
        } while (!ChannelOutboundBuffer.UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        if (oldValue != 0 && newValue == 0) {
            this.fireChannelWritabilityChanged(true);
        }
    }
    
    private void clearUserDefinedWritability(final int index) {
        final int mask = writabilityMask(index);
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = (oldValue | mask);
        } while (!ChannelOutboundBuffer.UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        if (oldValue == 0 && newValue != 0) {
            this.fireChannelWritabilityChanged(true);
        }
    }
    
    private static int writabilityMask(final int index) {
        if (index < 1 || index > 31) {
            throw new IllegalArgumentException("index: " + index + " (expected: 1~31)");
        }
        return 1 << index;
    }
    
    private void setWritable(final boolean invokeLater) {
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = (oldValue & 0xFFFFFFFE);
        } while (!ChannelOutboundBuffer.UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
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
        } while (!ChannelOutboundBuffer.UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        if (oldValue == 0) {
            this.fireChannelWritabilityChanged(invokeLater);
        }
    }
    
    private void fireChannelWritabilityChanged(final boolean invokeLater) {
        final ChannelPipeline pipeline = this.channel.pipeline();
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
            this.channel.eventLoop().execute(task);
        }
        else {
            pipeline.fireChannelWritabilityChanged();
        }
    }
    
    public int size() {
        return this.flushed;
    }
    
    public boolean isEmpty() {
        return this.flushed == 0;
    }
    
    void failFlushed(final Throwable cause, final boolean notify) {
        if (this.inFail) {
            return;
        }
        try {
            this.inFail = true;
            while (this.remove0(cause, notify)) {}
        }
        finally {
            this.inFail = false;
        }
    }
    
    void close(final Throwable cause, final boolean allowChannelOpen) {
        if (this.inFail) {
            this.channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    ChannelOutboundBuffer.this.close(cause, allowChannelOpen);
                }
            });
            return;
        }
        this.inFail = true;
        if (!allowChannelOpen && this.channel.isOpen()) {
            throw new IllegalStateException("close() must be invoked after the channel is closed.");
        }
        if (!this.isEmpty()) {
            throw new IllegalStateException("close() must be invoked after all flushed writes are handled.");
        }
        try {
            for (Entry e = this.unflushedEntry; e != null; e = e.recycleAndGetNext()) {
                final int size = e.pendingSize;
                ChannelOutboundBuffer.TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
                if (!e.cancelled) {
                    ReferenceCountUtil.safeRelease(e.msg);
                    safeFail(e.promise, cause);
                }
            }
        }
        finally {
            this.inFail = false;
        }
        this.clearNioBuffers();
    }
    
    void close(final ClosedChannelException cause) {
        this.close(cause, false);
    }
    
    private static void safeSuccess(final ChannelPromise promise) {
        PromiseNotificationUtil.trySuccess((Promise<? super Object>)promise, (Object)null, (promise instanceof VoidChannelPromise) ? null : ChannelOutboundBuffer.logger);
    }
    
    private static void safeFail(final ChannelPromise promise, final Throwable cause) {
        PromiseNotificationUtil.tryFailure(promise, cause, (promise instanceof VoidChannelPromise) ? null : ChannelOutboundBuffer.logger);
    }
    
    @Deprecated
    public void recycle() {
    }
    
    public long totalPendingWriteBytes() {
        return this.totalPendingSize;
    }
    
    public long bytesBeforeUnwritable() {
        final long bytes = this.channel.config().getWriteBufferHighWaterMark() - this.totalPendingSize;
        if (bytes > 0L) {
            return this.isWritable() ? bytes : 0L;
        }
        return 0L;
    }
    
    public long bytesBeforeWritable() {
        final long bytes = this.totalPendingSize - this.channel.config().getWriteBufferLowWaterMark();
        if (bytes > 0L) {
            return this.isWritable() ? 0L : bytes;
        }
        return 0L;
    }
    
    public void forEachFlushedMessage(final MessageProcessor processor) throws Exception {
        ObjectUtil.checkNotNull(processor, "processor");
        Entry entry = this.flushedEntry;
        if (entry == null) {
            return;
        }
        while (entry.cancelled || processor.processMessage(entry.msg)) {
            entry = entry.next;
            if (!this.isFlushedEntry(entry)) {
                return;
            }
        }
    }
    
    private boolean isFlushedEntry(final Entry e) {
        return e != null && e != this.unflushedEntry;
    }
    
    static {
        CHANNEL_OUTBOUND_BUFFER_ENTRY_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.outboundBufferEntrySizeOverhead", 96);
        logger = InternalLoggerFactory.getInstance(ChannelOutboundBuffer.class);
        NIO_BUFFERS = new FastThreadLocal<ByteBuffer[]>() {
            @Override
            protected ByteBuffer[] initialValue() throws Exception {
                return new ByteBuffer[1024];
            }
        };
        TOTAL_PENDING_SIZE_UPDATER = AtomicLongFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "totalPendingSize");
        UNWRITABLE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "unwritable");
    }
    
    static final class Entry
    {
        private static final ObjectPool<Entry> RECYCLER;
        private final ObjectPool.Handle<Entry> handle;
        Entry next;
        Object msg;
        ByteBuffer[] bufs;
        ByteBuffer buf;
        ChannelPromise promise;
        long progress;
        long total;
        int pendingSize;
        int count;
        boolean cancelled;
        
        private Entry(final ObjectPool.Handle<Entry> handle) {
            this.count = -1;
            this.handle = handle;
        }
        
        static Entry newInstance(final Object msg, final int size, final long total, final ChannelPromise promise) {
            final Entry entry = Entry.RECYCLER.get();
            entry.msg = msg;
            entry.pendingSize = size + ChannelOutboundBuffer.CHANNEL_OUTBOUND_BUFFER_ENTRY_OVERHEAD;
            entry.total = total;
            entry.promise = promise;
            return entry;
        }
        
        int cancel() {
            if (!this.cancelled) {
                this.cancelled = true;
                final int pSize = this.pendingSize;
                ReferenceCountUtil.safeRelease(this.msg);
                this.msg = Unpooled.EMPTY_BUFFER;
                this.pendingSize = 0;
                this.total = 0L;
                this.progress = 0L;
                this.bufs = null;
                this.buf = null;
                return pSize;
            }
            return 0;
        }
        
        void recycle() {
            this.next = null;
            this.bufs = null;
            this.buf = null;
            this.msg = null;
            this.promise = null;
            this.progress = 0L;
            this.total = 0L;
            this.pendingSize = 0;
            this.count = -1;
            this.cancelled = false;
            this.handle.recycle(this);
        }
        
        Entry recycleAndGetNext() {
            final Entry next = this.next;
            this.recycle();
            return next;
        }
        
        static {
            RECYCLER = ObjectPool.newPool((ObjectPool.ObjectCreator<Entry>)new ObjectPool.ObjectCreator<Entry>() {
                @Override
                public Entry newObject(final ObjectPool.Handle<Entry> handle) {
                    return new Entry((ObjectPool.Handle)handle);
                }
            });
        }
    }
    
    public interface MessageProcessor
    {
        boolean processMessage(final Object p0) throws Exception;
    }
}
