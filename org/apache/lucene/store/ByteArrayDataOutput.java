package org.apache.lucene.store;

import org.apache.lucene.util.BytesRef;

public class ByteArrayDataOutput extends DataOutput
{
    private byte[] bytes;
    private int pos;
    private int limit;
    
    public ByteArrayDataOutput(final byte[] bytes) {
        this.reset(bytes);
    }
    
    public ByteArrayDataOutput(final byte[] bytes, final int offset, final int len) {
        this.reset(bytes, offset, len);
    }
    
    public ByteArrayDataOutput() {
        this.reset(BytesRef.EMPTY_BYTES);
    }
    
    public void reset(final byte[] bytes) {
        this.reset(bytes, 0, bytes.length);
    }
    
    public void reset(final byte[] bytes, final int offset, final int len) {
        this.bytes = bytes;
        this.pos = offset;
        this.limit = offset + len;
    }
    
    public int getPosition() {
        return this.pos;
    }
    
    @Override
    public void writeByte(final byte b) {
        assert this.pos < this.limit;
        this.bytes[this.pos++] = b;
    }
    
    @Override
    public void writeBytes(final byte[] b, final int offset, final int length) {
        assert this.pos + length <= this.limit;
        System.arraycopy(b, offset, this.bytes, this.pos, length);
        this.pos += length;
    }
}
