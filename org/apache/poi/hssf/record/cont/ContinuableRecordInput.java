package org.apache.poi.hssf.record.cont;

import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.LittleEndianInput;

public class ContinuableRecordInput implements LittleEndianInput
{
    private final RecordInputStream _in;
    
    public ContinuableRecordInput(final RecordInputStream in) {
        this._in = in;
    }
    
    @Override
    public int available() {
        return this._in.available();
    }
    
    @Override
    public byte readByte() {
        return this._in.readByte();
    }
    
    @Override
    public int readUByte() {
        return this._in.readUByte();
    }
    
    @Override
    public short readShort() {
        return this._in.readShort();
    }
    
    @Override
    public int readUShort() {
        final int ch1 = this.readUByte();
        final int ch2 = this.readUByte();
        return (ch2 << 8) + (ch1 << 0);
    }
    
    @Override
    public int readInt() {
        final int ch1 = this._in.readUByte();
        final int ch2 = this._in.readUByte();
        final int ch3 = this._in.readUByte();
        final int ch4 = this._in.readUByte();
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }
    
    @Override
    public long readLong() {
        final int b0 = this._in.readUByte();
        final int b2 = this._in.readUByte();
        final int b3 = this._in.readUByte();
        final int b4 = this._in.readUByte();
        final int b5 = this._in.readUByte();
        final int b6 = this._in.readUByte();
        final int b7 = this._in.readUByte();
        final int b8 = this._in.readUByte();
        return ((long)b8 << 56) + ((long)b7 << 48) + ((long)b6 << 40) + ((long)b5 << 32) + ((long)b4 << 24) + (b3 << 16) + (b2 << 8) + (b0 << 0);
    }
    
    @Override
    public double readDouble() {
        return this._in.readDouble();
    }
    
    @Override
    public void readFully(final byte[] buf) {
        this._in.readFully(buf);
    }
    
    @Override
    public void readFully(final byte[] buf, final int off, final int len) {
        this._in.readFully(buf, off, len);
    }
    
    @Override
    public void readPlain(final byte[] buf, final int off, final int len) {
        this.readFully(buf, off, len);
    }
}
