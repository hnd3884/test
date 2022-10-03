package org.apache.poi.util;

import java.io.ByteArrayInputStream;

public class LittleEndianByteArrayInputStream extends ByteArrayInputStream implements LittleEndianInput
{
    public LittleEndianByteArrayInputStream(final byte[] buf, final int offset, final int length) {
        super(buf, offset, length);
    }
    
    public LittleEndianByteArrayInputStream(final byte[] buf, final int offset) {
        this(buf, offset, buf.length - offset);
    }
    
    public LittleEndianByteArrayInputStream(final byte[] buf) {
        super(buf);
    }
    
    protected void checkPosition(final int i) {
        if (i > this.count - this.pos) {
            throw new RuntimeException("Buffer overrun, having " + this.count + " bytes in the stream and position is at " + this.pos + ", but trying to increment position by " + i);
        }
    }
    
    public int getReadIndex() {
        return this.pos;
    }
    
    public void setReadIndex(final int pos) {
        if (pos < 0 || pos >= this.count) {
            throw new IndexOutOfBoundsException();
        }
        this.pos = pos;
    }
    
    @Override
    public byte readByte() {
        this.checkPosition(1);
        return (byte)this.read();
    }
    
    @Override
    public int readInt() {
        final int size = 4;
        this.checkPosition(4);
        final int le = LittleEndian.getInt(this.buf, this.pos);
        final long skipped = super.skip(4L);
        assert skipped == 4L : "Buffer overrun";
        return le;
    }
    
    @Override
    public long readLong() {
        final int size = 8;
        this.checkPosition(8);
        final long le = LittleEndian.getLong(this.buf, this.pos);
        final long skipped = super.skip(8L);
        assert skipped == 8L : "Buffer overrun";
        return le;
    }
    
    @Override
    public short readShort() {
        final int size = 2;
        this.checkPosition(2);
        final short le = LittleEndian.getShort(this.buf, this.pos);
        final long skipped = super.skip(2L);
        assert skipped == 2L : "Buffer overrun";
        return le;
    }
    
    @Override
    public int readUByte() {
        return this.readByte() & 0xFF;
    }
    
    @Override
    public int readUShort() {
        return this.readShort() & 0xFFFF;
    }
    
    public long readUInt() {
        return (long)this.readInt() & 0xFFFFFFFFL;
    }
    
    @Override
    public double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }
    
    @Override
    public void readFully(final byte[] buffer, final int off, final int len) {
        this.checkPosition(len);
        this.read(buffer, off, len);
    }
    
    @Override
    public void readFully(final byte[] buffer) {
        this.checkPosition(buffer.length);
        this.read(buffer, 0, buffer.length);
    }
    
    @Override
    public void readPlain(final byte[] buf, final int off, final int len) {
        this.readFully(buf, off, len);
    }
}
