package io.netty.buffer;

import java.util.ArrayDeque;
import java.nio.ByteBuffer;
import java.util.Deque;

final class PoolChunk<T> implements PoolChunkMetric
{
    private static final int SIZE_BIT_LENGTH = 15;
    private static final int INUSED_BIT_LENGTH = 1;
    private static final int SUBPAGE_BIT_LENGTH = 1;
    private static final int BITMAP_IDX_BIT_LENGTH = 32;
    static final int IS_SUBPAGE_SHIFT = 32;
    static final int IS_USED_SHIFT = 33;
    static final int SIZE_SHIFT = 34;
    static final int RUN_OFFSET_SHIFT = 49;
    final PoolArena<T> arena;
    final Object base;
    final T memory;
    final boolean unpooled;
    private final LongLongHashMap runsAvailMap;
    private final LongPriorityQueue[] runsAvail;
    private final PoolSubpage<T>[] subpages;
    private final int pageSize;
    private final int pageShifts;
    private final int chunkSize;
    private final Deque<ByteBuffer> cachedNioBuffers;
    int freeBytes;
    PoolChunkList<T> parent;
    PoolChunk<T> prev;
    PoolChunk<T> next;
    
    PoolChunk(final PoolArena<T> arena, final Object base, final T memory, final int pageSize, final int pageShifts, final int chunkSize, final int maxPageIdx) {
        this.unpooled = false;
        this.arena = arena;
        this.base = base;
        this.memory = memory;
        this.pageSize = pageSize;
        this.pageShifts = pageShifts;
        this.chunkSize = chunkSize;
        this.freeBytes = chunkSize;
        this.runsAvail = newRunsAvailqueueArray(maxPageIdx);
        this.runsAvailMap = new LongLongHashMap(-1L);
        this.subpages = new PoolSubpage[chunkSize >> pageShifts];
        final int pages = chunkSize >> pageShifts;
        final long initHandle = (long)pages << 34;
        this.insertAvailRun(0, pages, initHandle);
        this.cachedNioBuffers = new ArrayDeque<ByteBuffer>(8);
    }
    
    PoolChunk(final PoolArena<T> arena, final Object base, final T memory, final int size) {
        this.unpooled = true;
        this.arena = arena;
        this.base = base;
        this.memory = memory;
        this.pageSize = 0;
        this.pageShifts = 0;
        this.runsAvailMap = null;
        this.runsAvail = null;
        this.subpages = null;
        this.chunkSize = size;
        this.cachedNioBuffers = null;
    }
    
    private static LongPriorityQueue[] newRunsAvailqueueArray(final int size) {
        final LongPriorityQueue[] queueArray = new LongPriorityQueue[size];
        for (int i = 0; i < queueArray.length; ++i) {
            queueArray[i] = new LongPriorityQueue();
        }
        return queueArray;
    }
    
    private void insertAvailRun(final int runOffset, final int pages, final long handle) {
        final int pageIdxFloor = this.arena.pages2pageIdxFloor(pages);
        final LongPriorityQueue queue = this.runsAvail[pageIdxFloor];
        queue.offer(handle);
        this.insertAvailRun0(runOffset, handle);
        if (pages > 1) {
            this.insertAvailRun0(lastPage(runOffset, pages), handle);
        }
    }
    
    private void insertAvailRun0(final int runOffset, final long handle) {
        final long pre = this.runsAvailMap.put(runOffset, handle);
        assert pre == -1L;
    }
    
    private void removeAvailRun(final long handle) {
        final int pageIdxFloor = this.arena.pages2pageIdxFloor(runPages(handle));
        final LongPriorityQueue queue = this.runsAvail[pageIdxFloor];
        this.removeAvailRun(queue, handle);
    }
    
    private void removeAvailRun(final LongPriorityQueue queue, final long handle) {
        queue.remove(handle);
        final int runOffset = runOffset(handle);
        final int pages = runPages(handle);
        this.runsAvailMap.remove(runOffset);
        if (pages > 1) {
            this.runsAvailMap.remove(lastPage(runOffset, pages));
        }
    }
    
    private static int lastPage(final int runOffset, final int pages) {
        return runOffset + pages - 1;
    }
    
    private long getAvailRunByOffset(final int runOffset) {
        return this.runsAvailMap.get(runOffset);
    }
    
    @Override
    public int usage() {
        final int freeBytes;
        synchronized (this.arena) {
            freeBytes = this.freeBytes;
        }
        return this.usage(freeBytes);
    }
    
    private int usage(final int freeBytes) {
        if (freeBytes == 0) {
            return 100;
        }
        final int freePercentage = (int)(freeBytes * 100L / this.chunkSize);
        if (freePercentage == 0) {
            return 99;
        }
        return 100 - freePercentage;
    }
    
    boolean allocate(final PooledByteBuf<T> buf, final int reqCapacity, final int sizeIdx, final PoolThreadCache cache) {
        long handle;
        if (sizeIdx <= this.arena.smallMaxSizeIdx) {
            handle = this.allocateSubpage(sizeIdx);
            if (handle < 0L) {
                return false;
            }
            assert isSubpage(handle);
        }
        else {
            final int runSize = this.arena.sizeIdx2size(sizeIdx);
            handle = this.allocateRun(runSize);
            if (handle < 0L) {
                return false;
            }
        }
        final ByteBuffer nioBuffer = (this.cachedNioBuffers != null) ? this.cachedNioBuffers.pollLast() : null;
        this.initBuf(buf, nioBuffer, handle, reqCapacity, cache);
        return true;
    }
    
    private long allocateRun(final int runSize) {
        final int pages = runSize >> this.pageShifts;
        final int pageIdx = this.arena.pages2pageIdx(pages);
        synchronized (this.runsAvail) {
            final int queueIdx = this.runFirstBestFit(pageIdx);
            if (queueIdx == -1) {
                return -1L;
            }
            final LongPriorityQueue queue = this.runsAvail[queueIdx];
            long handle = queue.poll();
            assert handle != -1L && !isUsed(handle) : "invalid handle: " + handle;
            this.removeAvailRun(queue, handle);
            if (handle != -1L) {
                handle = this.splitLargeRun(handle, pages);
            }
            this.freeBytes -= runSize(this.pageShifts, handle);
            return handle;
        }
    }
    
    private int calculateRunSize(final int sizeIdx) {
        final int maxElements = 1 << this.pageShifts - 4;
        int runSize = 0;
        final int elemSize = this.arena.sizeIdx2size(sizeIdx);
        int nElements;
        do {
            runSize += this.pageSize;
            nElements = runSize / elemSize;
        } while (nElements < maxElements && runSize != nElements * elemSize);
        while (nElements > maxElements) {
            runSize -= this.pageSize;
            nElements = runSize / elemSize;
        }
        assert nElements > 0;
        assert runSize <= this.chunkSize;
        assert runSize >= elemSize;
        return runSize;
    }
    
    private int runFirstBestFit(final int pageIdx) {
        if (this.freeBytes == this.chunkSize) {
            return this.arena.nPSizes - 1;
        }
        for (int i = pageIdx; i < this.arena.nPSizes; ++i) {
            final LongPriorityQueue queue = this.runsAvail[i];
            if (queue != null && !queue.isEmpty()) {
                return i;
            }
        }
        return -1;
    }
    
    private long splitLargeRun(long handle, final int needPages) {
        assert needPages > 0;
        final int totalPages = runPages(handle);
        assert needPages <= totalPages;
        final int remPages = totalPages - needPages;
        if (remPages > 0) {
            final int runOffset = runOffset(handle);
            final int availOffset = runOffset + needPages;
            final long availRun = toRunHandle(availOffset, remPages, 0);
            this.insertAvailRun(availOffset, remPages, availRun);
            return toRunHandle(runOffset, needPages, 1);
        }
        handle |= 0x200000000L;
        return handle;
    }
    
    private long allocateSubpage(final int sizeIdx) {
        final PoolSubpage<T> head = this.arena.findSubpagePoolHead(sizeIdx);
        synchronized (head) {
            final int runSize = this.calculateRunSize(sizeIdx);
            final long runHandle = this.allocateRun(runSize);
            if (runHandle < 0L) {
                return -1L;
            }
            final int runOffset = runOffset(runHandle);
            assert this.subpages[runOffset] == null;
            final int elemSize = this.arena.sizeIdx2size(sizeIdx);
            final PoolSubpage<T> subpage = new PoolSubpage<T>(head, this, this.pageShifts, runOffset, runSize(this.pageShifts, runHandle), elemSize);
            this.subpages[runOffset] = subpage;
            return subpage.allocate();
        }
    }
    
    void free(final long handle, final int normCapacity, final ByteBuffer nioBuffer) {
        if (isSubpage(handle)) {
            final int sizeIdx = this.arena.size2SizeIdx(normCapacity);
            final PoolSubpage<T> head = this.arena.findSubpagePoolHead(sizeIdx);
            final int sIdx = runOffset(handle);
            final PoolSubpage<T> subpage = this.subpages[sIdx];
            assert subpage != null && subpage.doNotDestroy;
            synchronized (head) {
                if (subpage.free(head, bitmapIdx(handle))) {
                    return;
                }
                assert !subpage.doNotDestroy;
                this.subpages[sIdx] = null;
            }
        }
        final int pages = runPages(handle);
        synchronized (this.runsAvail) {
            long finalRun = this.collapseRuns(handle);
            finalRun &= 0xFFFFFFFDFFFFFFFFL;
            finalRun &= 0xFFFFFFFEFFFFFFFFL;
            this.insertAvailRun(runOffset(finalRun), runPages(finalRun), finalRun);
            this.freeBytes += pages << this.pageShifts;
        }
        if (nioBuffer != null && this.cachedNioBuffers != null && this.cachedNioBuffers.size() < PooledByteBufAllocator.DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK) {
            this.cachedNioBuffers.offer(nioBuffer);
        }
    }
    
    private long collapseRuns(final long handle) {
        return this.collapseNext(this.collapsePast(handle));
    }
    
    private long collapsePast(long handle) {
        while (true) {
            final int runOffset = runOffset(handle);
            final int runPages = runPages(handle);
            final long pastRun = this.getAvailRunByOffset(runOffset - 1);
            if (pastRun == -1L) {
                return handle;
            }
            final int pastOffset = runOffset(pastRun);
            final int pastPages = runPages(pastRun);
            if (pastRun == handle || pastOffset + pastPages != runOffset) {
                return handle;
            }
            this.removeAvailRun(pastRun);
            handle = toRunHandle(pastOffset, pastPages + runPages, 0);
        }
    }
    
    private long collapseNext(long handle) {
        while (true) {
            final int runOffset = runOffset(handle);
            final int runPages = runPages(handle);
            final long nextRun = this.getAvailRunByOffset(runOffset + runPages);
            if (nextRun == -1L) {
                return handle;
            }
            final int nextOffset = runOffset(nextRun);
            final int nextPages = runPages(nextRun);
            if (nextRun == handle || runOffset + runPages != nextOffset) {
                return handle;
            }
            this.removeAvailRun(nextRun);
            handle = toRunHandle(runOffset, runPages + nextPages, 0);
        }
    }
    
    private static long toRunHandle(final int runOffset, final int runPages, final int inUsed) {
        return (long)runOffset << 49 | (long)runPages << 34 | (long)inUsed << 33;
    }
    
    void initBuf(final PooledByteBuf<T> buf, final ByteBuffer nioBuffer, final long handle, final int reqCapacity, final PoolThreadCache threadCache) {
        if (isRun(handle)) {
            buf.init(this, nioBuffer, handle, runOffset(handle) << this.pageShifts, reqCapacity, runSize(this.pageShifts, handle), this.arena.parent.threadCache());
        }
        else {
            this.initBufWithSubpage(buf, nioBuffer, handle, reqCapacity, threadCache);
        }
    }
    
    void initBufWithSubpage(final PooledByteBuf<T> buf, final ByteBuffer nioBuffer, final long handle, final int reqCapacity, final PoolThreadCache threadCache) {
        final int runOffset = runOffset(handle);
        final int bitmapIdx = bitmapIdx(handle);
        final PoolSubpage<T> s = this.subpages[runOffset];
        assert s.doNotDestroy;
        assert reqCapacity <= s.elemSize;
        final int offset = (runOffset << this.pageShifts) + bitmapIdx * s.elemSize;
        buf.init(this, nioBuffer, handle, offset, reqCapacity, s.elemSize, threadCache);
    }
    
    @Override
    public int chunkSize() {
        return this.chunkSize;
    }
    
    @Override
    public int freeBytes() {
        synchronized (this.arena) {
            return this.freeBytes;
        }
    }
    
    @Override
    public String toString() {
        final int freeBytes;
        synchronized (this.arena) {
            freeBytes = this.freeBytes;
        }
        return "Chunk(" + Integer.toHexString(System.identityHashCode(this)) + ": " + this.usage(freeBytes) + "%, " + (this.chunkSize - freeBytes) + '/' + this.chunkSize + ')';
    }
    
    void destroy() {
        this.arena.destroyChunk(this);
    }
    
    static int runOffset(final long handle) {
        return (int)(handle >> 49);
    }
    
    static int runSize(final int pageShifts, final long handle) {
        return runPages(handle) << pageShifts;
    }
    
    static int runPages(final long handle) {
        return (int)(handle >> 34 & 0x7FFFL);
    }
    
    static boolean isUsed(final long handle) {
        return (handle >> 33 & 0x1L) == 0x1L;
    }
    
    static boolean isRun(final long handle) {
        return !isSubpage(handle);
    }
    
    static boolean isSubpage(final long handle) {
        return (handle >> 32 & 0x1L) == 0x1L;
    }
    
    static int bitmapIdx(final long handle) {
        return (int)handle;
    }
}
