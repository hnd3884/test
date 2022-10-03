package org.apache.lucene.util.packed;

import java.util.Collection;

public final class PagedGrowableWriter extends AbstractPagedMutable<PagedGrowableWriter>
{
    final float acceptableOverheadRatio;
    
    public PagedGrowableWriter(final long size, final int pageSize, final int startBitsPerValue, final float acceptableOverheadRatio) {
        this(size, pageSize, startBitsPerValue, acceptableOverheadRatio, true);
    }
    
    PagedGrowableWriter(final long size, final int pageSize, final int startBitsPerValue, final float acceptableOverheadRatio, final boolean fillPages) {
        super(startBitsPerValue, size, pageSize);
        this.acceptableOverheadRatio = acceptableOverheadRatio;
        if (fillPages) {
            this.fillPages();
        }
    }
    
    @Override
    protected PackedInts.Mutable newMutable(final int valueCount, final int bitsPerValue) {
        return new GrowableWriter(bitsPerValue, valueCount, this.acceptableOverheadRatio);
    }
    
    @Override
    protected PagedGrowableWriter newUnfilledCopy(final long newSize) {
        return new PagedGrowableWriter(newSize, this.pageSize(), this.bitsPerValue, this.acceptableOverheadRatio, false);
    }
    
    @Override
    protected long baseRamBytesUsed() {
        return super.baseRamBytesUsed() + 4L;
    }
}
