package org.tukaani.xz.index;

import org.tukaani.xz.common.Util;
import org.tukaani.xz.XZIOException;

abstract class IndexBase
{
    private final XZIOException invalidIndexException;
    long blocksSum;
    long uncompressedSum;
    long indexListSize;
    long recordCount;
    
    IndexBase(final XZIOException invalidIndexException) {
        this.blocksSum = 0L;
        this.uncompressedSum = 0L;
        this.indexListSize = 0L;
        this.recordCount = 0L;
        this.invalidIndexException = invalidIndexException;
    }
    
    private long getUnpaddedIndexSize() {
        return 1 + Util.getVLISize(this.recordCount) + this.indexListSize + 4L;
    }
    
    public long getIndexSize() {
        return this.getUnpaddedIndexSize() + 3L & 0xFFFFFFFFFFFFFFFCL;
    }
    
    public long getStreamSize() {
        return 12L + this.blocksSum + this.getIndexSize() + 12L;
    }
    
    int getIndexPaddingSize() {
        return (int)(4L - this.getUnpaddedIndexSize() & 0x3L);
    }
    
    void add(final long n, final long n2) throws XZIOException {
        this.blocksSum += (n + 3L & 0xFFFFFFFFFFFFFFFCL);
        this.uncompressedSum += n2;
        this.indexListSize += Util.getVLISize(n) + Util.getVLISize(n2);
        ++this.recordCount;
        if (this.blocksSum < 0L || this.uncompressedSum < 0L || this.getIndexSize() > 17179869184L || this.getStreamSize() < 0L) {
            throw this.invalidIndexException;
        }
    }
}
