package io.netty.buffer;

import java.util.Collections;
import io.netty.util.internal.StringUtil;
import java.util.List;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.Iterator;

final class PoolChunkList<T> implements PoolChunkListMetric
{
    private static final Iterator<PoolChunkMetric> EMPTY_METRICS;
    private final PoolArena<T> arena;
    private final PoolChunkList<T> nextList;
    private final int minUsage;
    private final int maxUsage;
    private final int maxCapacity;
    private PoolChunk<T> head;
    private final int freeMinThreshold;
    private final int freeMaxThreshold;
    private PoolChunkList<T> prevList;
    
    PoolChunkList(final PoolArena<T> arena, final PoolChunkList<T> nextList, final int minUsage, final int maxUsage, final int chunkSize) {
        assert minUsage <= maxUsage;
        this.arena = arena;
        this.nextList = nextList;
        this.minUsage = minUsage;
        this.maxUsage = maxUsage;
        this.maxCapacity = calculateMaxCapacity(minUsage, chunkSize);
        this.freeMinThreshold = ((maxUsage == 100) ? 0 : ((int)(chunkSize * (100.0 - maxUsage + 0.99999999) / 100.0)));
        this.freeMaxThreshold = ((minUsage == 100) ? 0 : ((int)(chunkSize * (100.0 - minUsage + 0.99999999) / 100.0)));
    }
    
    private static int calculateMaxCapacity(int minUsage, final int chunkSize) {
        minUsage = minUsage0(minUsage);
        if (minUsage == 100) {
            return 0;
        }
        return (int)(chunkSize * (100L - minUsage) / 100L);
    }
    
    void prevList(final PoolChunkList<T> prevList) {
        assert this.prevList == null;
        this.prevList = prevList;
    }
    
    boolean allocate(final PooledByteBuf<T> buf, final int reqCapacity, final int sizeIdx, final PoolThreadCache threadCache) {
        final int normCapacity = this.arena.sizeIdx2size(sizeIdx);
        if (normCapacity > this.maxCapacity) {
            return false;
        }
        for (PoolChunk<T> cur = this.head; cur != null; cur = cur.next) {
            if (cur.allocate(buf, reqCapacity, sizeIdx, threadCache)) {
                if (cur.freeBytes <= this.freeMinThreshold) {
                    this.remove(cur);
                    this.nextList.add(cur);
                }
                return true;
            }
        }
        return false;
    }
    
    boolean free(final PoolChunk<T> chunk, final long handle, final int normCapacity, final ByteBuffer nioBuffer) {
        chunk.free(handle, normCapacity, nioBuffer);
        if (chunk.freeBytes > this.freeMaxThreshold) {
            this.remove(chunk);
            return this.move0(chunk);
        }
        return true;
    }
    
    private boolean move(final PoolChunk<T> chunk) {
        assert chunk.usage() < this.maxUsage;
        if (chunk.freeBytes > this.freeMaxThreshold) {
            return this.move0(chunk);
        }
        this.add0(chunk);
        return true;
    }
    
    private boolean move0(final PoolChunk<T> chunk) {
        if (this.prevList != null) {
            return this.prevList.move(chunk);
        }
        assert chunk.usage() == 0;
        return false;
    }
    
    void add(final PoolChunk<T> chunk) {
        if (chunk.freeBytes <= this.freeMinThreshold) {
            this.nextList.add(chunk);
            return;
        }
        this.add0(chunk);
    }
    
    void add0(final PoolChunk<T> chunk) {
        chunk.parent = this;
        if (this.head == null) {
            this.head = chunk;
            chunk.prev = null;
            chunk.next = null;
        }
        else {
            chunk.prev = null;
            chunk.next = this.head;
            this.head.prev = chunk;
            this.head = chunk;
        }
    }
    
    private void remove(final PoolChunk<T> cur) {
        if (cur == this.head) {
            this.head = cur.next;
            if (this.head != null) {
                this.head.prev = null;
            }
        }
        else {
            final PoolChunk<T> next = cur.next;
            if ((cur.prev.next = next) != null) {
                next.prev = cur.prev;
            }
        }
    }
    
    @Override
    public int minUsage() {
        return minUsage0(this.minUsage);
    }
    
    @Override
    public int maxUsage() {
        return Math.min(this.maxUsage, 100);
    }
    
    private static int minUsage0(final int value) {
        return Math.max(1, value);
    }
    
    @Override
    public Iterator<PoolChunkMetric> iterator() {
        synchronized (this.arena) {
            if (this.head == null) {
                return PoolChunkList.EMPTY_METRICS;
            }
            final List<PoolChunkMetric> metrics = new ArrayList<PoolChunkMetric>();
            PoolChunk<T> cur = this.head;
            do {
                metrics.add(cur);
                cur = cur.next;
            } while (cur != null);
            return metrics.iterator();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        synchronized (this.arena) {
            if (this.head == null) {
                return "none";
            }
            PoolChunk<T> cur = this.head;
            while (true) {
                buf.append(cur);
                cur = cur.next;
                if (cur == null) {
                    break;
                }
                buf.append(StringUtil.NEWLINE);
            }
        }
        return buf.toString();
    }
    
    void destroy(final PoolArena<T> arena) {
        for (PoolChunk<T> chunk = this.head; chunk != null; chunk = chunk.next) {
            arena.destroyChunk(chunk);
        }
        this.head = null;
    }
    
    static {
        EMPTY_METRICS = Collections.emptyList().iterator();
    }
}
