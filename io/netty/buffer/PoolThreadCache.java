package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectPool;
import java.util.Queue;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.List;
import java.util.ArrayList;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.atomic.AtomicBoolean;
import java.nio.ByteBuffer;
import io.netty.util.internal.logging.InternalLogger;

final class PoolThreadCache
{
    private static final InternalLogger logger;
    private static final int INTEGER_SIZE_MINUS_ONE = 31;
    final PoolArena<byte[]> heapArena;
    final PoolArena<ByteBuffer> directArena;
    private final MemoryRegionCache<byte[]>[] smallSubPageHeapCaches;
    private final MemoryRegionCache<ByteBuffer>[] smallSubPageDirectCaches;
    private final MemoryRegionCache<byte[]>[] normalHeapCaches;
    private final MemoryRegionCache<ByteBuffer>[] normalDirectCaches;
    private final int freeSweepAllocationThreshold;
    private final AtomicBoolean freed;
    private int allocations;
    
    PoolThreadCache(final PoolArena<byte[]> heapArena, final PoolArena<ByteBuffer> directArena, final int smallCacheSize, final int normalCacheSize, final int maxCachedBufferCapacity, final int freeSweepAllocationThreshold) {
        this.freed = new AtomicBoolean();
        ObjectUtil.checkPositiveOrZero(maxCachedBufferCapacity, "maxCachedBufferCapacity");
        this.freeSweepAllocationThreshold = freeSweepAllocationThreshold;
        this.heapArena = heapArena;
        this.directArena = directArena;
        if (directArena != null) {
            this.smallSubPageDirectCaches = createSubPageCaches(smallCacheSize, directArena.numSmallSubpagePools);
            this.normalDirectCaches = createNormalCaches(normalCacheSize, maxCachedBufferCapacity, directArena);
            directArena.numThreadCaches.getAndIncrement();
        }
        else {
            this.smallSubPageDirectCaches = null;
            this.normalDirectCaches = null;
        }
        if (heapArena != null) {
            this.smallSubPageHeapCaches = createSubPageCaches(smallCacheSize, heapArena.numSmallSubpagePools);
            this.normalHeapCaches = createNormalCaches(normalCacheSize, maxCachedBufferCapacity, heapArena);
            heapArena.numThreadCaches.getAndIncrement();
        }
        else {
            this.smallSubPageHeapCaches = null;
            this.normalHeapCaches = null;
        }
        if ((this.smallSubPageDirectCaches != null || this.normalDirectCaches != null || this.smallSubPageHeapCaches != null || this.normalHeapCaches != null) && freeSweepAllocationThreshold < 1) {
            throw new IllegalArgumentException("freeSweepAllocationThreshold: " + freeSweepAllocationThreshold + " (expected: > 0)");
        }
    }
    
    private static <T> MemoryRegionCache<T>[] createSubPageCaches(final int cacheSize, final int numCaches) {
        if (cacheSize > 0 && numCaches > 0) {
            final MemoryRegionCache<T>[] cache = new MemoryRegionCache[numCaches];
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new SubPageMemoryRegionCache<T>(cacheSize);
            }
            return cache;
        }
        return null;
    }
    
    private static <T> MemoryRegionCache<T>[] createNormalCaches(final int cacheSize, final int maxCachedBufferCapacity, final PoolArena<T> area) {
        if (cacheSize > 0 && maxCachedBufferCapacity > 0) {
            final int max = Math.min(area.chunkSize, maxCachedBufferCapacity);
            final List<MemoryRegionCache<T>> cache = new ArrayList<MemoryRegionCache<T>>();
            for (int idx = area.numSmallSubpagePools; idx < area.nSizes && area.sizeIdx2size(idx) <= max; ++idx) {
                cache.add(new NormalMemoryRegionCache<T>(cacheSize));
            }
            return cache.toArray(new MemoryRegionCache[0]);
        }
        return null;
    }
    
    static int log2(final int val) {
        return 31 - Integer.numberOfLeadingZeros(val);
    }
    
    boolean allocateSmall(final PoolArena<?> area, final PooledByteBuf<?> buf, final int reqCapacity, final int sizeIdx) {
        return this.allocate(this.cacheForSmall(area, sizeIdx), buf, reqCapacity);
    }
    
    boolean allocateNormal(final PoolArena<?> area, final PooledByteBuf<?> buf, final int reqCapacity, final int sizeIdx) {
        return this.allocate(this.cacheForNormal(area, sizeIdx), buf, reqCapacity);
    }
    
    private boolean allocate(final MemoryRegionCache<?> cache, final PooledByteBuf buf, final int reqCapacity) {
        if (cache == null) {
            return false;
        }
        final boolean allocated = cache.allocate(buf, reqCapacity, this);
        if (++this.allocations >= this.freeSweepAllocationThreshold) {
            this.allocations = 0;
            this.trim();
        }
        return allocated;
    }
    
    boolean add(final PoolArena<?> area, final PoolChunk chunk, final ByteBuffer nioBuffer, final long handle, final int normCapacity, final PoolArena.SizeClass sizeClass) {
        final int sizeIdx = area.size2SizeIdx(normCapacity);
        final MemoryRegionCache<?> cache = this.cache(area, sizeIdx, sizeClass);
        return cache != null && cache.add(chunk, nioBuffer, handle, normCapacity);
    }
    
    private MemoryRegionCache<?> cache(final PoolArena<?> area, final int sizeIdx, final PoolArena.SizeClass sizeClass) {
        switch (sizeClass) {
            case Normal: {
                return this.cacheForNormal(area, sizeIdx);
            }
            case Small: {
                return this.cacheForSmall(area, sizeIdx);
            }
            default: {
                throw new Error();
            }
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        }
        finally {
            this.free(true);
        }
    }
    
    void free(final boolean finalizer) {
        if (this.freed.compareAndSet(false, true)) {
            final int numFreed = free(this.smallSubPageDirectCaches, finalizer) + free(this.normalDirectCaches, finalizer) + free(this.smallSubPageHeapCaches, finalizer) + free(this.normalHeapCaches, finalizer);
            if (numFreed > 0 && PoolThreadCache.logger.isDebugEnabled()) {
                PoolThreadCache.logger.debug("Freed {} thread-local buffer(s) from thread: {}", (Object)numFreed, Thread.currentThread().getName());
            }
            if (this.directArena != null) {
                this.directArena.numThreadCaches.getAndDecrement();
            }
            if (this.heapArena != null) {
                this.heapArena.numThreadCaches.getAndDecrement();
            }
        }
    }
    
    private static int free(final MemoryRegionCache<?>[] caches, final boolean finalizer) {
        if (caches == null) {
            return 0;
        }
        int numFreed = 0;
        for (final MemoryRegionCache<?> c : caches) {
            numFreed += free(c, finalizer);
        }
        return numFreed;
    }
    
    private static int free(final MemoryRegionCache<?> cache, final boolean finalizer) {
        if (cache == null) {
            return 0;
        }
        return cache.free(finalizer);
    }
    
    void trim() {
        trim(this.smallSubPageDirectCaches);
        trim(this.normalDirectCaches);
        trim(this.smallSubPageHeapCaches);
        trim(this.normalHeapCaches);
    }
    
    private static void trim(final MemoryRegionCache<?>[] caches) {
        if (caches == null) {
            return;
        }
        for (final MemoryRegionCache<?> c : caches) {
            trim(c);
        }
    }
    
    private static void trim(final MemoryRegionCache<?> cache) {
        if (cache == null) {
            return;
        }
        cache.trim();
    }
    
    private MemoryRegionCache<?> cacheForSmall(final PoolArena<?> area, final int sizeIdx) {
        if (area.isDirect()) {
            return cache((MemoryRegionCache<?>[])this.smallSubPageDirectCaches, sizeIdx);
        }
        return cache((MemoryRegionCache<?>[])this.smallSubPageHeapCaches, sizeIdx);
    }
    
    private MemoryRegionCache<?> cacheForNormal(final PoolArena<?> area, final int sizeIdx) {
        final int idx = sizeIdx - area.numSmallSubpagePools;
        if (area.isDirect()) {
            return cache((MemoryRegionCache<?>[])this.normalDirectCaches, idx);
        }
        return cache((MemoryRegionCache<?>[])this.normalHeapCaches, idx);
    }
    
    private static <T> MemoryRegionCache<T> cache(final MemoryRegionCache<T>[] cache, final int sizeIdx) {
        if (cache == null || sizeIdx > cache.length - 1) {
            return null;
        }
        return cache[sizeIdx];
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(PoolThreadCache.class);
    }
    
    private static final class SubPageMemoryRegionCache<T> extends MemoryRegionCache<T>
    {
        SubPageMemoryRegionCache(final int size) {
            super(size, PoolArena.SizeClass.Small);
        }
        
        @Override
        protected void initBuf(final PoolChunk<T> chunk, final ByteBuffer nioBuffer, final long handle, final PooledByteBuf<T> buf, final int reqCapacity, final PoolThreadCache threadCache) {
            chunk.initBufWithSubpage(buf, nioBuffer, handle, reqCapacity, threadCache);
        }
    }
    
    private static final class NormalMemoryRegionCache<T> extends MemoryRegionCache<T>
    {
        NormalMemoryRegionCache(final int size) {
            super(size, PoolArena.SizeClass.Normal);
        }
        
        @Override
        protected void initBuf(final PoolChunk<T> chunk, final ByteBuffer nioBuffer, final long handle, final PooledByteBuf<T> buf, final int reqCapacity, final PoolThreadCache threadCache) {
            chunk.initBuf(buf, nioBuffer, handle, reqCapacity, threadCache);
        }
    }
    
    private abstract static class MemoryRegionCache<T>
    {
        private final int size;
        private final Queue<Entry<T>> queue;
        private final PoolArena.SizeClass sizeClass;
        private int allocations;
        private static final ObjectPool<Entry> RECYCLER;
        
        MemoryRegionCache(final int size, final PoolArena.SizeClass sizeClass) {
            this.size = MathUtil.safeFindNextPositivePowerOfTwo(size);
            this.queue = PlatformDependent.newFixedMpscQueue(this.size);
            this.sizeClass = sizeClass;
        }
        
        protected abstract void initBuf(final PoolChunk<T> p0, final ByteBuffer p1, final long p2, final PooledByteBuf<T> p3, final int p4, final PoolThreadCache p5);
        
        public final boolean add(final PoolChunk<T> chunk, final ByteBuffer nioBuffer, final long handle, final int normCapacity) {
            final Entry<T> entry = newEntry(chunk, nioBuffer, handle, normCapacity);
            final boolean queued = this.queue.offer(entry);
            if (!queued) {
                entry.recycle();
            }
            return queued;
        }
        
        public final boolean allocate(final PooledByteBuf<T> buf, final int reqCapacity, final PoolThreadCache threadCache) {
            final Entry<T> entry = this.queue.poll();
            if (entry == null) {
                return false;
            }
            this.initBuf(entry.chunk, entry.nioBuffer, entry.handle, buf, reqCapacity, threadCache);
            entry.recycle();
            ++this.allocations;
            return true;
        }
        
        public final int free(final boolean finalizer) {
            return this.free(Integer.MAX_VALUE, finalizer);
        }
        
        private int free(final int max, final boolean finalizer) {
            int numFreed;
            for (numFreed = 0; numFreed < max; ++numFreed) {
                final Entry<T> entry = this.queue.poll();
                if (entry == null) {
                    return numFreed;
                }
                this.freeEntry(entry, finalizer);
            }
            return numFreed;
        }
        
        public final void trim() {
            final int free = this.size - this.allocations;
            this.allocations = 0;
            if (free > 0) {
                this.free(free, false);
            }
        }
        
        private void freeEntry(final Entry entry, final boolean finalizer) {
            final PoolChunk chunk = entry.chunk;
            final long handle = entry.handle;
            final ByteBuffer nioBuffer = entry.nioBuffer;
            if (!finalizer) {
                entry.recycle();
            }
            chunk.arena.freeChunk(chunk, handle, entry.normCapacity, this.sizeClass, nioBuffer, finalizer);
        }
        
        private static Entry newEntry(final PoolChunk<?> chunk, final ByteBuffer nioBuffer, final long handle, final int normCapacity) {
            final Entry entry = MemoryRegionCache.RECYCLER.get();
            entry.chunk = (PoolChunk<T>)chunk;
            entry.nioBuffer = nioBuffer;
            entry.handle = handle;
            entry.normCapacity = normCapacity;
            return entry;
        }
        
        static {
            RECYCLER = ObjectPool.newPool((ObjectPool.ObjectCreator<Entry>)new ObjectPool.ObjectCreator<Entry>() {
                @Override
                public Entry newObject(final ObjectPool.Handle<Entry> handle) {
                    return new Entry((ObjectPool.Handle<Entry<?>>)handle);
                }
            });
        }
        
        static final class Entry<T>
        {
            final ObjectPool.Handle<Entry<?>> recyclerHandle;
            PoolChunk<T> chunk;
            ByteBuffer nioBuffer;
            long handle;
            int normCapacity;
            
            Entry(final ObjectPool.Handle<Entry<?>> recyclerHandle) {
                this.handle = -1L;
                this.recyclerHandle = recyclerHandle;
            }
            
            void recycle() {
                this.chunk = null;
                this.nioBuffer = null;
                this.handle = -1L;
                this.recyclerHandle.recycle(this);
            }
        }
    }
}
