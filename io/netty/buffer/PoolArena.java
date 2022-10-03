package io.netty.buffer;

import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.ArrayList;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.atomic.AtomicInteger;
import io.netty.util.internal.LongCounter;
import java.util.List;

abstract class PoolArena<T> extends SizeClasses implements PoolArenaMetric
{
    static final boolean HAS_UNSAFE;
    final PooledByteBufAllocator parent;
    final int numSmallSubpagePools;
    final int directMemoryCacheAlignment;
    private final PoolSubpage<T>[] smallSubpagePools;
    private final PoolChunkList<T> q050;
    private final PoolChunkList<T> q025;
    private final PoolChunkList<T> q000;
    private final PoolChunkList<T> qInit;
    private final PoolChunkList<T> q075;
    private final PoolChunkList<T> q100;
    private final List<PoolChunkListMetric> chunkListMetrics;
    private long allocationsNormal;
    private final LongCounter allocationsSmall;
    private final LongCounter allocationsHuge;
    private final LongCounter activeBytesHuge;
    private long deallocationsSmall;
    private long deallocationsNormal;
    private final LongCounter deallocationsHuge;
    final AtomicInteger numThreadCaches;
    
    protected PoolArena(final PooledByteBufAllocator parent, final int pageSize, final int pageShifts, final int chunkSize, final int cacheAlignment) {
        super(pageSize, pageShifts, chunkSize, cacheAlignment);
        this.allocationsSmall = PlatformDependent.newLongCounter();
        this.allocationsHuge = PlatformDependent.newLongCounter();
        this.activeBytesHuge = PlatformDependent.newLongCounter();
        this.deallocationsHuge = PlatformDependent.newLongCounter();
        this.numThreadCaches = new AtomicInteger();
        this.parent = parent;
        this.directMemoryCacheAlignment = cacheAlignment;
        this.numSmallSubpagePools = this.nSubpages;
        this.smallSubpagePools = this.newSubpagePoolArray(this.numSmallSubpagePools);
        for (int i = 0; i < this.smallSubpagePools.length; ++i) {
            this.smallSubpagePools[i] = this.newSubpagePoolHead();
        }
        this.q100 = new PoolChunkList<T>(this, null, 100, Integer.MAX_VALUE, chunkSize);
        this.q075 = new PoolChunkList<T>(this, this.q100, 75, 100, chunkSize);
        this.q050 = new PoolChunkList<T>(this, this.q075, 50, 100, chunkSize);
        this.q025 = new PoolChunkList<T>(this, this.q050, 25, 75, chunkSize);
        this.q000 = new PoolChunkList<T>(this, this.q025, 1, 50, chunkSize);
        this.qInit = new PoolChunkList<T>(this, this.q000, Integer.MIN_VALUE, 25, chunkSize);
        this.q100.prevList(this.q075);
        this.q075.prevList(this.q050);
        this.q050.prevList(this.q025);
        this.q025.prevList(this.q000);
        this.q000.prevList(null);
        this.qInit.prevList(this.qInit);
        final List<PoolChunkListMetric> metrics = new ArrayList<PoolChunkListMetric>(6);
        metrics.add(this.qInit);
        metrics.add(this.q000);
        metrics.add(this.q025);
        metrics.add(this.q050);
        metrics.add(this.q075);
        metrics.add(this.q100);
        this.chunkListMetrics = Collections.unmodifiableList((List<? extends PoolChunkListMetric>)metrics);
    }
    
    private PoolSubpage<T> newSubpagePoolHead() {
        final PoolSubpage<T> head = new PoolSubpage<T>();
        head.prev = head;
        return head.next = head;
    }
    
    private PoolSubpage<T>[] newSubpagePoolArray(final int size) {
        return new PoolSubpage[size];
    }
    
    abstract boolean isDirect();
    
    PooledByteBuf<T> allocate(final PoolThreadCache cache, final int reqCapacity, final int maxCapacity) {
        final PooledByteBuf<T> buf = this.newByteBuf(maxCapacity);
        this.allocate(cache, buf, reqCapacity);
        return buf;
    }
    
    private void allocate(final PoolThreadCache cache, final PooledByteBuf<T> buf, final int reqCapacity) {
        final int sizeIdx = this.size2SizeIdx(reqCapacity);
        if (sizeIdx <= this.smallMaxSizeIdx) {
            this.tcacheAllocateSmall(cache, buf, reqCapacity, sizeIdx);
        }
        else if (sizeIdx < this.nSizes) {
            this.tcacheAllocateNormal(cache, buf, reqCapacity, sizeIdx);
        }
        else {
            final int normCapacity = (this.directMemoryCacheAlignment > 0) ? this.normalizeSize(reqCapacity) : reqCapacity;
            this.allocateHuge(buf, normCapacity);
        }
    }
    
    private void tcacheAllocateSmall(final PoolThreadCache cache, final PooledByteBuf<T> buf, final int reqCapacity, final int sizeIdx) {
        if (cache.allocateSmall(this, buf, reqCapacity, sizeIdx)) {
            return;
        }
        final PoolSubpage<T> head = this.smallSubpagePools[sizeIdx];
        final boolean needsNormalAllocation;
        synchronized (head) {
            final PoolSubpage<T> s = head.next;
            needsNormalAllocation = (s == head);
            if (!needsNormalAllocation) {
                assert s.doNotDestroy && s.elemSize == this.sizeIdx2size(sizeIdx);
                final long handle = s.allocate();
                assert handle >= 0L;
                s.chunk.initBufWithSubpage(buf, null, handle, reqCapacity, cache);
            }
        }
        if (needsNormalAllocation) {
            synchronized (this) {
                this.allocateNormal(buf, reqCapacity, sizeIdx, cache);
            }
        }
        this.incSmallAllocation();
    }
    
    private void tcacheAllocateNormal(final PoolThreadCache cache, final PooledByteBuf<T> buf, final int reqCapacity, final int sizeIdx) {
        if (cache.allocateNormal(this, buf, reqCapacity, sizeIdx)) {
            return;
        }
        synchronized (this) {
            this.allocateNormal(buf, reqCapacity, sizeIdx, cache);
            ++this.allocationsNormal;
        }
    }
    
    private void allocateNormal(final PooledByteBuf<T> buf, final int reqCapacity, final int sizeIdx, final PoolThreadCache threadCache) {
        if (this.q050.allocate(buf, reqCapacity, sizeIdx, threadCache) || this.q025.allocate(buf, reqCapacity, sizeIdx, threadCache) || this.q000.allocate(buf, reqCapacity, sizeIdx, threadCache) || this.qInit.allocate(buf, reqCapacity, sizeIdx, threadCache) || this.q075.allocate(buf, reqCapacity, sizeIdx, threadCache)) {
            return;
        }
        final PoolChunk<T> c = this.newChunk(this.pageSize, this.nPSizes, this.pageShifts, this.chunkSize);
        final boolean success = c.allocate(buf, reqCapacity, sizeIdx, threadCache);
        assert success;
        this.qInit.add(c);
    }
    
    private void incSmallAllocation() {
        this.allocationsSmall.increment();
    }
    
    private void allocateHuge(final PooledByteBuf<T> buf, final int reqCapacity) {
        final PoolChunk<T> chunk = this.newUnpooledChunk(reqCapacity);
        this.activeBytesHuge.add(chunk.chunkSize());
        buf.initUnpooled(chunk, reqCapacity);
        this.allocationsHuge.increment();
    }
    
    void free(final PoolChunk<T> chunk, final ByteBuffer nioBuffer, final long handle, final int normCapacity, final PoolThreadCache cache) {
        if (chunk.unpooled) {
            final int size = chunk.chunkSize();
            this.destroyChunk(chunk);
            this.activeBytesHuge.add(-size);
            this.deallocationsHuge.increment();
        }
        else {
            final SizeClass sizeClass = sizeClass(handle);
            if (cache != null && cache.add(this, chunk, nioBuffer, handle, normCapacity, sizeClass)) {
                return;
            }
            this.freeChunk(chunk, handle, normCapacity, sizeClass, nioBuffer, false);
        }
    }
    
    private static SizeClass sizeClass(final long handle) {
        return PoolChunk.isSubpage(handle) ? SizeClass.Small : SizeClass.Normal;
    }
    
    void freeChunk(final PoolChunk<T> chunk, final long handle, final int normCapacity, final SizeClass sizeClass, final ByteBuffer nioBuffer, final boolean finalizer) {
        final boolean destroyChunk;
        synchronized (this) {
            if (!finalizer) {
                switch (sizeClass) {
                    case Normal: {
                        ++this.deallocationsNormal;
                        break;
                    }
                    case Small: {
                        ++this.deallocationsSmall;
                        break;
                    }
                    default: {
                        throw new Error();
                    }
                }
            }
            destroyChunk = !chunk.parent.free(chunk, handle, normCapacity, nioBuffer);
        }
        if (destroyChunk) {
            this.destroyChunk(chunk);
        }
    }
    
    PoolSubpage<T> findSubpagePoolHead(final int sizeIdx) {
        return this.smallSubpagePools[sizeIdx];
    }
    
    void reallocate(final PooledByteBuf<T> buf, final int newCapacity, final boolean freeOldMemory) {
        assert newCapacity >= 0 && newCapacity <= buf.maxCapacity();
        final int oldCapacity = buf.length;
        if (oldCapacity == newCapacity) {
            return;
        }
        final PoolChunk<T> oldChunk = buf.chunk;
        final ByteBuffer oldNioBuffer = buf.tmpNioBuf;
        final long oldHandle = buf.handle;
        final T oldMemory = buf.memory;
        final int oldOffset = buf.offset;
        final int oldMaxLength = buf.maxLength;
        this.allocate(this.parent.threadCache(), buf, newCapacity);
        int bytesToCopy;
        if (newCapacity > oldCapacity) {
            bytesToCopy = oldCapacity;
        }
        else {
            buf.trimIndicesToCapacity(newCapacity);
            bytesToCopy = newCapacity;
        }
        this.memoryCopy(oldMemory, oldOffset, buf, bytesToCopy);
        if (freeOldMemory) {
            this.free(oldChunk, oldNioBuffer, oldHandle, oldMaxLength, buf.cache);
        }
    }
    
    @Override
    public int numThreadCaches() {
        return this.numThreadCaches.get();
    }
    
    @Override
    public int numTinySubpages() {
        return 0;
    }
    
    @Override
    public int numSmallSubpages() {
        return this.smallSubpagePools.length;
    }
    
    @Override
    public int numChunkLists() {
        return this.chunkListMetrics.size();
    }
    
    @Override
    public List<PoolSubpageMetric> tinySubpages() {
        return Collections.emptyList();
    }
    
    @Override
    public List<PoolSubpageMetric> smallSubpages() {
        return subPageMetricList(this.smallSubpagePools);
    }
    
    @Override
    public List<PoolChunkListMetric> chunkLists() {
        return this.chunkListMetrics;
    }
    
    private static List<PoolSubpageMetric> subPageMetricList(final PoolSubpage<?>[] pages) {
        final List<PoolSubpageMetric> metrics = new ArrayList<PoolSubpageMetric>();
        for (final PoolSubpage<?> head : pages) {
            if (head.next != head) {
                PoolSubpage<?> s = head.next;
                do {
                    metrics.add(s);
                    s = s.next;
                } while (s != head);
            }
        }
        return metrics;
    }
    
    @Override
    public long numAllocations() {
        final long allocsNormal;
        synchronized (this) {
            allocsNormal = this.allocationsNormal;
        }
        return this.allocationsSmall.value() + allocsNormal + this.allocationsHuge.value();
    }
    
    @Override
    public long numTinyAllocations() {
        return 0L;
    }
    
    @Override
    public long numSmallAllocations() {
        return this.allocationsSmall.value();
    }
    
    @Override
    public synchronized long numNormalAllocations() {
        return this.allocationsNormal;
    }
    
    @Override
    public long numDeallocations() {
        final long deallocs;
        synchronized (this) {
            deallocs = this.deallocationsSmall + this.deallocationsNormal;
        }
        return deallocs + this.deallocationsHuge.value();
    }
    
    @Override
    public long numTinyDeallocations() {
        return 0L;
    }
    
    @Override
    public synchronized long numSmallDeallocations() {
        return this.deallocationsSmall;
    }
    
    @Override
    public synchronized long numNormalDeallocations() {
        return this.deallocationsNormal;
    }
    
    @Override
    public long numHugeAllocations() {
        return this.allocationsHuge.value();
    }
    
    @Override
    public long numHugeDeallocations() {
        return this.deallocationsHuge.value();
    }
    
    @Override
    public long numActiveAllocations() {
        long val = this.allocationsSmall.value() + this.allocationsHuge.value() - this.deallocationsHuge.value();
        synchronized (this) {
            val += this.allocationsNormal - (this.deallocationsSmall + this.deallocationsNormal);
        }
        return Math.max(val, 0L);
    }
    
    @Override
    public long numActiveTinyAllocations() {
        return 0L;
    }
    
    @Override
    public long numActiveSmallAllocations() {
        return Math.max(this.numSmallAllocations() - this.numSmallDeallocations(), 0L);
    }
    
    @Override
    public long numActiveNormalAllocations() {
        final long val;
        synchronized (this) {
            val = this.allocationsNormal - this.deallocationsNormal;
        }
        return Math.max(val, 0L);
    }
    
    @Override
    public long numActiveHugeAllocations() {
        return Math.max(this.numHugeAllocations() - this.numHugeDeallocations(), 0L);
    }
    
    @Override
    public long numActiveBytes() {
        long val = this.activeBytesHuge.value();
        synchronized (this) {
            for (int i = 0; i < this.chunkListMetrics.size(); ++i) {
                for (final PoolChunkMetric m : this.chunkListMetrics.get(i)) {
                    val += m.chunkSize();
                }
            }
        }
        return Math.max(0L, val);
    }
    
    protected abstract PoolChunk<T> newChunk(final int p0, final int p1, final int p2, final int p3);
    
    protected abstract PoolChunk<T> newUnpooledChunk(final int p0);
    
    protected abstract PooledByteBuf<T> newByteBuf(final int p0);
    
    protected abstract void memoryCopy(final T p0, final int p1, final PooledByteBuf<T> p2, final int p3);
    
    protected abstract void destroyChunk(final PoolChunk<T> p0);
    
    @Override
    public synchronized String toString() {
        final StringBuilder buf = new StringBuilder().append("Chunk(s) at 0~25%:").append(StringUtil.NEWLINE).append(this.qInit).append(StringUtil.NEWLINE).append("Chunk(s) at 0~50%:").append(StringUtil.NEWLINE).append(this.q000).append(StringUtil.NEWLINE).append("Chunk(s) at 25~75%:").append(StringUtil.NEWLINE).append(this.q025).append(StringUtil.NEWLINE).append("Chunk(s) at 50~100%:").append(StringUtil.NEWLINE).append(this.q050).append(StringUtil.NEWLINE).append("Chunk(s) at 75~100%:").append(StringUtil.NEWLINE).append(this.q075).append(StringUtil.NEWLINE).append("Chunk(s) at 100%:").append(StringUtil.NEWLINE).append(this.q100).append(StringUtil.NEWLINE).append("small subpages:");
        appendPoolSubPages(buf, this.smallSubpagePools);
        buf.append(StringUtil.NEWLINE);
        return buf.toString();
    }
    
    private static void appendPoolSubPages(final StringBuilder buf, final PoolSubpage<?>[] subpages) {
        for (int i = 0; i < subpages.length; ++i) {
            final PoolSubpage<?> head = subpages[i];
            if (head.next != head) {
                buf.append(StringUtil.NEWLINE).append(i).append(": ");
                PoolSubpage<?> s = head.next;
                do {
                    buf.append(s);
                    s = s.next;
                } while (s != head);
            }
        }
    }
    
    @Override
    protected final void finalize() throws Throwable {
        try {
            super.finalize();
        }
        finally {
            destroyPoolSubPages(this.smallSubpagePools);
            this.destroyPoolChunkLists(this.qInit, this.q000, this.q025, this.q050, this.q075, this.q100);
        }
    }
    
    private static void destroyPoolSubPages(final PoolSubpage<?>[] pages) {
        for (final PoolSubpage<?> page : pages) {
            page.destroy();
        }
    }
    
    private void destroyPoolChunkLists(final PoolChunkList<T>... chunkLists) {
        for (final PoolChunkList<T> chunkList : chunkLists) {
            chunkList.destroy(this);
        }
    }
    
    static {
        HAS_UNSAFE = PlatformDependent.hasUnsafe();
    }
    
    enum SizeClass
    {
        Small, 
        Normal;
    }
    
    static final class HeapArena extends PoolArena<byte[]>
    {
        HeapArena(final PooledByteBufAllocator parent, final int pageSize, final int pageShifts, final int chunkSize, final int directMemoryCacheAlignment) {
            super(parent, pageSize, pageShifts, chunkSize, directMemoryCacheAlignment);
        }
        
        private static byte[] newByteArray(final int size) {
            return PlatformDependent.allocateUninitializedArray(size);
        }
        
        @Override
        boolean isDirect() {
            return false;
        }
        
        @Override
        protected PoolChunk<byte[]> newChunk(final int pageSize, final int maxPageIdx, final int pageShifts, final int chunkSize) {
            return new PoolChunk<byte[]>(this, null, newByteArray(chunkSize), pageSize, pageShifts, chunkSize, maxPageIdx);
        }
        
        @Override
        protected PoolChunk<byte[]> newUnpooledChunk(final int capacity) {
            return new PoolChunk<byte[]>(this, null, newByteArray(capacity), capacity);
        }
        
        @Override
        protected void destroyChunk(final PoolChunk<byte[]> chunk) {
        }
        
        @Override
        protected PooledByteBuf<byte[]> newByteBuf(final int maxCapacity) {
            return HeapArena.HAS_UNSAFE ? PooledUnsafeHeapByteBuf.newUnsafeInstance(maxCapacity) : PooledHeapByteBuf.newInstance(maxCapacity);
        }
        
        @Override
        protected void memoryCopy(final byte[] src, final int srcOffset, final PooledByteBuf<byte[]> dst, final int length) {
            if (length == 0) {
                return;
            }
            System.arraycopy(src, srcOffset, dst.memory, dst.offset, length);
        }
    }
    
    static final class DirectArena extends PoolArena<ByteBuffer>
    {
        DirectArena(final PooledByteBufAllocator parent, final int pageSize, final int pageShifts, final int chunkSize, final int directMemoryCacheAlignment) {
            super(parent, pageSize, pageShifts, chunkSize, directMemoryCacheAlignment);
        }
        
        @Override
        boolean isDirect() {
            return true;
        }
        
        @Override
        protected PoolChunk<ByteBuffer> newChunk(final int pageSize, final int maxPageIdx, final int pageShifts, final int chunkSize) {
            if (this.directMemoryCacheAlignment == 0) {
                final ByteBuffer memory = allocateDirect(chunkSize);
                return new PoolChunk<ByteBuffer>(this, memory, memory, pageSize, pageShifts, chunkSize, maxPageIdx);
            }
            final ByteBuffer base = allocateDirect(chunkSize + this.directMemoryCacheAlignment);
            final ByteBuffer memory2 = PlatformDependent.alignDirectBuffer(base, this.directMemoryCacheAlignment);
            return new PoolChunk<ByteBuffer>(this, base, memory2, pageSize, pageShifts, chunkSize, maxPageIdx);
        }
        
        @Override
        protected PoolChunk<ByteBuffer> newUnpooledChunk(final int capacity) {
            if (this.directMemoryCacheAlignment == 0) {
                final ByteBuffer memory = allocateDirect(capacity);
                return new PoolChunk<ByteBuffer>(this, memory, memory, capacity);
            }
            final ByteBuffer base = allocateDirect(capacity + this.directMemoryCacheAlignment);
            final ByteBuffer memory2 = PlatformDependent.alignDirectBuffer(base, this.directMemoryCacheAlignment);
            return new PoolChunk<ByteBuffer>(this, base, memory2, capacity);
        }
        
        private static ByteBuffer allocateDirect(final int capacity) {
            return PlatformDependent.useDirectBufferNoCleaner() ? PlatformDependent.allocateDirectNoCleaner(capacity) : ByteBuffer.allocateDirect(capacity);
        }
        
        @Override
        protected void destroyChunk(final PoolChunk<ByteBuffer> chunk) {
            if (PlatformDependent.useDirectBufferNoCleaner()) {
                PlatformDependent.freeDirectNoCleaner((ByteBuffer)chunk.base);
            }
            else {
                PlatformDependent.freeDirectBuffer((ByteBuffer)chunk.base);
            }
        }
        
        @Override
        protected PooledByteBuf<ByteBuffer> newByteBuf(final int maxCapacity) {
            if (DirectArena.HAS_UNSAFE) {
                return PooledUnsafeDirectByteBuf.newInstance(maxCapacity);
            }
            return PooledDirectByteBuf.newInstance(maxCapacity);
        }
        
        @Override
        protected void memoryCopy(ByteBuffer src, final int srcOffset, final PooledByteBuf<ByteBuffer> dstBuf, final int length) {
            if (length == 0) {
                return;
            }
            if (DirectArena.HAS_UNSAFE) {
                PlatformDependent.copyMemory(PlatformDependent.directBufferAddress(src) + srcOffset, PlatformDependent.directBufferAddress(dstBuf.memory) + dstBuf.offset, length);
            }
            else {
                src = src.duplicate();
                final ByteBuffer dst = dstBuf.internalNioBuffer();
                src.position(srcOffset).limit(srcOffset + length);
                dst.position(dstBuf.offset);
                dst.put(src);
            }
        }
    }
}
