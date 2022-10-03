package org.tukaani.xz.index;

public class BlockInfo
{
    public int blockNumber;
    public long compressedOffset;
    public long uncompressedOffset;
    public long unpaddedSize;
    public long uncompressedSize;
    IndexDecoder index;
    
    public BlockInfo(final IndexDecoder index) {
        this.blockNumber = -1;
        this.compressedOffset = -1L;
        this.uncompressedOffset = -1L;
        this.unpaddedSize = -1L;
        this.uncompressedSize = -1L;
        this.index = index;
    }
    
    public int getCheckType() {
        return this.index.getStreamFlags().checkType;
    }
    
    public boolean hasNext() {
        return this.index.hasRecord(this.blockNumber + 1);
    }
    
    public void setNext() {
        this.index.setBlockInfo(this, this.blockNumber + 1);
    }
}
