package org.apache.lucene.index;

import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.store.DataOutput;

final class ByteSliceWriter extends DataOutput
{
    private byte[] slice;
    private int upto;
    private final ByteBlockPool pool;
    int offset0;
    
    public ByteSliceWriter(final ByteBlockPool pool) {
        this.pool = pool;
    }
    
    public void init(final int address) {
        this.slice = this.pool.buffers[address >> 15];
        assert this.slice != null;
        this.upto = (address & 0x7FFF);
        this.offset0 = address;
        assert this.upto < this.slice.length;
    }
    
    @Override
    public void writeByte(final byte b) {
        assert this.slice != null;
        if (this.slice[this.upto] != 0) {
            this.upto = this.pool.allocSlice(this.slice, this.upto);
            this.slice = this.pool.buffer;
            this.offset0 = this.pool.byteOffset;
            assert this.slice != null;
        }
        this.slice[this.upto++] = b;
        assert this.upto != this.slice.length;
    }
    
    @Override
    public void writeBytes(final byte[] b, int offset, final int len) {
        final int offsetEnd = offset + len;
        while (offset < offsetEnd) {
            if (this.slice[this.upto] != 0) {
                this.upto = this.pool.allocSlice(this.slice, this.upto);
                this.slice = this.pool.buffer;
                this.offset0 = this.pool.byteOffset;
            }
            this.slice[this.upto++] = b[offset++];
            assert this.upto != this.slice.length;
        }
    }
    
    public int getAddress() {
        return this.upto + (this.offset0 & 0xFFFF8000);
    }
}
