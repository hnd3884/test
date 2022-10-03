package org.apache.lucene.util.fst;

final class ReverseBytesReader extends FST.BytesReader
{
    private final byte[] bytes;
    private int pos;
    
    public ReverseBytesReader(final byte[] bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public byte readByte() {
        return this.bytes[this.pos--];
    }
    
    @Override
    public void readBytes(final byte[] b, final int offset, final int len) {
        for (int i = 0; i < len; ++i) {
            b[offset + i] = this.bytes[this.pos--];
        }
    }
    
    @Override
    public void skipBytes(final long count) {
        this.pos -= (int)count;
    }
    
    @Override
    public long getPosition() {
        return this.pos;
    }
    
    @Override
    public void setPosition(final long pos) {
        this.pos = (int)pos;
    }
    
    @Override
    public boolean reversed() {
        return true;
    }
}
