package org.apache.lucene.util.packed;

import java.util.Collection;
import org.apache.lucene.util.RamUsageEstimator;

public final class PagedMutable extends AbstractPagedMutable<PagedMutable>
{
    final PackedInts.Format format;
    
    public PagedMutable(final long size, final int pageSize, final int bitsPerValue, final float acceptableOverheadRatio) {
        this(size, pageSize, PackedInts.fastestFormatAndBits(pageSize, bitsPerValue, acceptableOverheadRatio));
        this.fillPages();
    }
    
    PagedMutable(final long size, final int pageSize, final PackedInts.FormatAndBits formatAndBits) {
        this(size, pageSize, formatAndBits.bitsPerValue, formatAndBits.format);
    }
    
    PagedMutable(final long size, final int pageSize, final int bitsPerValue, final PackedInts.Format format) {
        super(bitsPerValue, size, pageSize);
        this.format = format;
    }
    
    @Override
    protected PackedInts.Mutable newMutable(final int valueCount, final int bitsPerValue) {
        assert this.bitsPerValue >= bitsPerValue;
        return PackedInts.getMutable(valueCount, this.bitsPerValue, this.format);
    }
    
    @Override
    protected PagedMutable newUnfilledCopy(final long newSize) {
        return new PagedMutable(newSize, this.pageSize(), this.bitsPerValue, this.format);
    }
    
    @Override
    protected long baseRamBytesUsed() {
        return super.baseRamBytesUsed() + RamUsageEstimator.NUM_BYTES_OBJECT_REF;
    }
}
