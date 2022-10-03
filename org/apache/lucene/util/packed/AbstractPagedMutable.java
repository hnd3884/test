package org.apache.lucene.util.packed;

import java.util.Collections;
import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.util.LongValues;

abstract class AbstractPagedMutable<T extends AbstractPagedMutable<T>> extends LongValues implements Accountable
{
    static final int MIN_BLOCK_SIZE = 64;
    static final int MAX_BLOCK_SIZE = 1073741824;
    final long size;
    final int pageShift;
    final int pageMask;
    final PackedInts.Mutable[] subMutables;
    final int bitsPerValue;
    
    AbstractPagedMutable(final int bitsPerValue, final long size, final int pageSize) {
        this.bitsPerValue = bitsPerValue;
        this.size = size;
        this.pageShift = PackedInts.checkBlockSize(pageSize, 64, 1073741824);
        this.pageMask = pageSize - 1;
        final int numPages = PackedInts.numBlocks(size, pageSize);
        this.subMutables = new PackedInts.Mutable[numPages];
    }
    
    protected final void fillPages() {
        for (int numPages = PackedInts.numBlocks(this.size, this.pageSize()), i = 0; i < numPages; ++i) {
            final int valueCount = (i == numPages - 1) ? this.lastPageSize(this.size) : this.pageSize();
            this.subMutables[i] = this.newMutable(valueCount, this.bitsPerValue);
        }
    }
    
    protected abstract PackedInts.Mutable newMutable(final int p0, final int p1);
    
    final int lastPageSize(final long size) {
        final int sz = this.indexInPage(size);
        return (sz == 0) ? this.pageSize() : sz;
    }
    
    final int pageSize() {
        return this.pageMask + 1;
    }
    
    public final long size() {
        return this.size;
    }
    
    final int pageIndex(final long index) {
        return (int)(index >>> this.pageShift);
    }
    
    final int indexInPage(final long index) {
        return (int)index & this.pageMask;
    }
    
    @Override
    public final long get(final long index) {
        assert index >= 0L && index < this.size;
        final int pageIndex = this.pageIndex(index);
        final int indexInPage = this.indexInPage(index);
        return this.subMutables[pageIndex].get(indexInPage);
    }
    
    public final void set(final long index, final long value) {
        assert index >= 0L && index < this.size;
        final int pageIndex = this.pageIndex(index);
        final int indexInPage = this.indexInPage(index);
        this.subMutables[pageIndex].set(indexInPage, value);
    }
    
    protected long baseRamBytesUsed() {
        return RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + RamUsageEstimator.NUM_BYTES_OBJECT_REF + 8 + 12;
    }
    
    @Override
    public long ramBytesUsed() {
        long bytesUsed = RamUsageEstimator.alignObjectSize(this.baseRamBytesUsed());
        bytesUsed += RamUsageEstimator.alignObjectSize(RamUsageEstimator.shallowSizeOf(this.subMutables));
        for (final PackedInts.Mutable gw : this.subMutables) {
            bytesUsed += gw.ramBytesUsed();
        }
        return bytesUsed;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    protected abstract T newUnfilledCopy(final long p0);
    
    public final T resize(final long newSize) {
        final T copy = this.newUnfilledCopy(newSize);
        final int numCommonPages = Math.min(copy.subMutables.length, this.subMutables.length);
        final long[] copyBuffer = new long[1024];
        for (int i = 0; i < copy.subMutables.length; ++i) {
            final int valueCount = (i == copy.subMutables.length - 1) ? this.lastPageSize(newSize) : this.pageSize();
            final int bpv = (i < numCommonPages) ? this.subMutables[i].getBitsPerValue() : this.bitsPerValue;
            copy.subMutables[i] = this.newMutable(valueCount, bpv);
            if (i < numCommonPages) {
                final int copyLength = Math.min(valueCount, this.subMutables[i].size());
                PackedInts.copy(this.subMutables[i], 0, copy.subMutables[i], 0, copyLength, copyBuffer);
            }
        }
        return copy;
    }
    
    public final T grow(final long minSize) {
        assert minSize >= 0L;
        if (minSize <= this.size()) {
            final T result = (T)this;
            return result;
        }
        long extra = minSize >>> 3;
        if (extra < 3L) {
            extra = 3L;
        }
        final long newSize = minSize + extra;
        return this.resize(newSize);
    }
    
    public final T grow() {
        return this.grow(this.size() + 1L);
    }
    
    @Override
    public final String toString() {
        return this.getClass().getSimpleName() + "(size=" + this.size() + ",pageSize=" + this.pageSize() + ")";
    }
}
