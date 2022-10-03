package org.apache.lucene.util.fst;

final class ForwardBytesReader extends FST.BytesReader
{
    private final byte[] bytes;
    private int pos;
    
    public ForwardBytesReader(final byte[] bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public byte readByte() {
        return this.bytes[this.pos++];
    }
    
    @Override
    public void readBytes(final byte[] b, final int offset, final int len) {
        System.arraycopy(this.bytes, this.pos, b, offset, len);
        this.pos += len;
    }
    
    @Override
    public void skipBytes(final long count) {
        this.pos += (int)count;
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
        return false;
    }
}
