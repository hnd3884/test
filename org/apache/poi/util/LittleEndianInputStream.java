package org.apache.poi.util;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.FilterInputStream;

public class LittleEndianInputStream extends FilterInputStream implements LittleEndianInput
{
    private static final int BUFFERED_SIZE = 8096;
    private static final int EOF = -1;
    private int readIndex;
    private int markIndex;
    
    public LittleEndianInputStream(final InputStream is) {
        super(is.markSupported() ? is : new BufferedInputStream(is, 8096));
        this.readIndex = 0;
        this.markIndex = -1;
    }
    
    @SuppressForbidden("just delegating")
    @Override
    public int available() {
        try {
            return super.available();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public byte readByte() {
        return (byte)this.readUByte();
    }
    
    @Override
    public int readUByte() {
        final byte[] buf = { 0 };
        try {
            checkEOF(this.read(buf), 1);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return LittleEndian.getUByte(buf);
    }
    
    public float readFloat() {
        return Float.intBitsToFloat(this.readInt());
    }
    
    @Override
    public double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }
    
    @Override
    public int readInt() {
        final byte[] buf = new byte[4];
        try {
            checkEOF(this.read(buf), buf.length);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return LittleEndian.getInt(buf);
    }
    
    public long readUInt() {
        final long retNum = this.readInt();
        return retNum & 0xFFFFFFFFL;
    }
    
    @Override
    public long readLong() {
        final byte[] buf = new byte[8];
        try {
            checkEOF(this.read(buf), 8);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return LittleEndian.getLong(buf);
    }
    
    @Override
    public short readShort() {
        return (short)this.readUShort();
    }
    
    @Override
    public int readUShort() {
        final byte[] buf = new byte[2];
        try {
            checkEOF(this.read(buf), 2);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return LittleEndian.getUShort(buf);
    }
    
    private static void checkEOF(final int actualBytes, final int expectedBytes) {
        if (expectedBytes != 0 && (actualBytes == -1 || actualBytes != expectedBytes)) {
            throw new RuntimeException("Unexpected end-of-file");
        }
    }
    
    @Override
    public void readFully(final byte[] buf) {
        this.readFully(buf, 0, buf.length);
    }
    
    @Override
    public void readFully(final byte[] buf, final int off, final int len) {
        try {
            checkEOF(this._read(buf, off, len), len);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int readBytes = super.read(b, off, len);
        this.readIndex += readBytes;
        return readBytes;
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        super.mark(readlimit);
        this.markIndex = this.readIndex;
    }
    
    @Override
    public synchronized void reset() throws IOException {
        super.reset();
        if (this.markIndex > -1) {
            this.readIndex = this.markIndex;
            this.markIndex = -1;
        }
    }
    
    public int getReadIndex() {
        return this.readIndex;
    }
    
    private int _read(final byte[] buffer, final int offset, final int length) throws IOException {
        int remaining;
        int count;
        for (remaining = length; remaining > 0; remaining -= count) {
            final int location = length - remaining;
            count = this.read(buffer, offset + location, remaining);
            if (-1 == count) {
                break;
            }
        }
        return length - remaining;
    }
    
    @Override
    public void readPlain(final byte[] buf, final int off, final int len) {
        this.readFully(buf, off, len);
    }
    
    public void skipFully(final int len) throws IOException {
        if (len == 0) {
            return;
        }
        final long skipped = IOUtils.skipFully(this, len);
        if (skipped > 2147483647L) {
            throw new IOException("can't skip further than 2147483647");
        }
        checkEOF((int)skipped, len);
    }
}
