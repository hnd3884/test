package org.apache.lucene.store;

import org.apache.lucene.util.BytesRef;

public final class ByteArrayDataInput extends DataInput
{
    private byte[] bytes;
    private int pos;
    private int limit;
    
    public ByteArrayDataInput(final byte[] bytes) {
        this.reset(bytes);
    }
    
    public ByteArrayDataInput(final byte[] bytes, final int offset, final int len) {
        this.reset(bytes, offset, len);
    }
    
    public ByteArrayDataInput() {
        this.reset(BytesRef.EMPTY_BYTES);
    }
    
    public void reset(final byte[] bytes) {
        this.reset(bytes, 0, bytes.length);
    }
    
    public void rewind() {
        this.pos = 0;
    }
    
    public int getPosition() {
        return this.pos;
    }
    
    public void setPosition(final int pos) {
        this.pos = pos;
    }
    
    public void reset(final byte[] bytes, final int offset, final int len) {
        this.bytes = bytes;
        this.pos = offset;
        this.limit = offset + len;
    }
    
    public int length() {
        return this.limit;
    }
    
    public boolean eof() {
        return this.pos == this.limit;
    }
    
    @Override
    public void skipBytes(final long count) {
        this.pos += (int)count;
    }
    
    @Override
    public short readShort() {
        return (short)((this.bytes[this.pos++] & 0xFF) << 8 | (this.bytes[this.pos++] & 0xFF));
    }
    
    @Override
    public int readInt() {
        return (this.bytes[this.pos++] & 0xFF) << 24 | (this.bytes[this.pos++] & 0xFF) << 16 | (this.bytes[this.pos++] & 0xFF) << 8 | (this.bytes[this.pos++] & 0xFF);
    }
    
    @Override
    public long readLong() {
        final int i1 = (this.bytes[this.pos++] & 0xFF) << 24 | (this.bytes[this.pos++] & 0xFF) << 16 | (this.bytes[this.pos++] & 0xFF) << 8 | (this.bytes[this.pos++] & 0xFF);
        final int i2 = (this.bytes[this.pos++] & 0xFF) << 24 | (this.bytes[this.pos++] & 0xFF) << 16 | (this.bytes[this.pos++] & 0xFF) << 8 | (this.bytes[this.pos++] & 0xFF);
        return (long)i1 << 32 | ((long)i2 & 0xFFFFFFFFL);
    }
    
    @Override
    public int readVInt() {
        byte b = this.bytes[this.pos++];
        if (b >= 0) {
            return b;
        }
        int i = b & 0x7F;
        b = this.bytes[this.pos++];
        i |= (b & 0x7F) << 7;
        if (b >= 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= (b & 0x7F) << 14;
        if (b >= 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= (b & 0x7F) << 21;
        if (b >= 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= (b & 0xF) << 28;
        if ((b & 0xF0) == 0x0) {
            return i;
        }
        throw new RuntimeException("Invalid vInt detected (too many bits)");
    }
    
    @Override
    public long readVLong() {
        byte b = this.bytes[this.pos++];
        if (b >= 0) {
            return b;
        }
        long i = (long)b & 0x7FL;
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 7;
        if (b >= 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 14;
        if (b >= 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 21;
        if (b >= 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 28;
        if (b >= 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 35;
        if (b >= 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 42;
        if (b >= 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 49;
        if (b >= 0) {
            return i;
        }
        b = this.bytes[this.pos++];
        i |= ((long)b & 0x7FL) << 56;
        if (b >= 0) {
            return i;
        }
        throw new RuntimeException("Invalid vLong detected (negative values disallowed)");
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
}
