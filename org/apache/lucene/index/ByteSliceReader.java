package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.store.DataInput;

final class ByteSliceReader extends DataInput
{
    ByteBlockPool pool;
    int bufferUpto;
    byte[] buffer;
    public int upto;
    int limit;
    int level;
    public int bufferOffset;
    public int endIndex;
    
    public void init(final ByteBlockPool pool, final int startIndex, final int endIndex) {
        assert endIndex - startIndex >= 0;
        assert startIndex >= 0;
        assert endIndex >= 0;
        this.pool = pool;
        this.endIndex = endIndex;
        this.level = 0;
        this.bufferUpto = startIndex / 32768;
        this.bufferOffset = this.bufferUpto * 32768;
        this.buffer = pool.buffers[this.bufferUpto];
        this.upto = (startIndex & 0x7FFF);
        final int firstSize = ByteBlockPool.LEVEL_SIZE_ARRAY[0];
        if (startIndex + firstSize >= endIndex) {
            this.limit = (endIndex & 0x7FFF);
        }
        else {
            this.limit = this.upto + firstSize - 4;
        }
    }
    
    public boolean eof() {
        assert this.upto + this.bufferOffset <= this.endIndex;
        return this.upto + this.bufferOffset == this.endIndex;
    }
    
    @Override
    public byte readByte() {
        assert !this.eof();
        assert this.upto <= this.limit;
        if (this.upto == this.limit) {
            this.nextSlice();
        }
        return this.buffer[this.upto++];
    }
    
    public long writeTo(final DataOutput out) throws IOException {
        long size = 0L;
        while (this.limit + this.bufferOffset != this.endIndex) {
            out.writeBytes(this.buffer, this.upto, this.limit - this.upto);
            size += this.limit - this.upto;
            this.nextSlice();
        }
        assert this.endIndex - this.bufferOffset >= this.upto;
        out.writeBytes(this.buffer, this.upto, this.limit - this.upto);
        size += this.limit - this.upto;
        return size;
    }
    
    public void nextSlice() {
        final int nextIndex = ((this.buffer[this.limit] & 0xFF) << 24) + ((this.buffer[1 + this.limit] & 0xFF) << 16) + ((this.buffer[2 + this.limit] & 0xFF) << 8) + (this.buffer[3 + this.limit] & 0xFF);
        this.level = ByteBlockPool.NEXT_LEVEL_ARRAY[this.level];
        final int newSize = ByteBlockPool.LEVEL_SIZE_ARRAY[this.level];
        this.bufferUpto = nextIndex / 32768;
        this.bufferOffset = this.bufferUpto * 32768;
        this.buffer = this.pool.buffers[this.bufferUpto];
        this.upto = (nextIndex & 0x7FFF);
        if (nextIndex + newSize >= this.endIndex) {
            assert this.endIndex - nextIndex > 0;
            this.limit = this.endIndex - this.bufferOffset;
        }
        else {
            this.limit = this.upto + newSize - 4;
        }
    }
    
    @Override
    public void readBytes(final byte[] b, int offset, int len) {
        while (len > 0) {
            final int numLeft = this.limit - this.upto;
            if (numLeft >= len) {
                System.arraycopy(this.buffer, this.upto, b, offset, len);
                this.upto += len;
                break;
            }
            System.arraycopy(this.buffer, this.upto, b, offset, numLeft);
            offset += numLeft;
            len -= numLeft;
            this.nextSlice();
        }
    }
}
