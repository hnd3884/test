package org.msgpack.io;

import java.io.IOException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.io.InputStream;

public class StreamInput extends AbstractInput
{
    private final InputStream in;
    private byte[] castBuffer;
    private ByteBuffer castByteBuffer;
    private int filled;
    
    public StreamInput(final InputStream in) {
        this.in = in;
        this.castBuffer = new byte[8];
        this.castByteBuffer = ByteBuffer.wrap(this.castBuffer);
        this.filled = 0;
    }
    
    @Override
    public int read(final byte[] b, int off, final int len) throws IOException {
        int n;
        for (int remain = len; remain > 0; remain -= n, off += n) {
            n = this.in.read(b, off, remain);
            if (n <= 0) {
                throw new EOFException();
            }
            this.incrReadByteCount(n);
        }
        return len;
    }
    
    @Override
    public boolean tryRefer(final BufferReferer ref, final int size) throws IOException {
        return false;
    }
    
    @Override
    public byte readByte() throws IOException {
        final int n = this.in.read();
        if (n < 0) {
            throw new EOFException();
        }
        this.incrReadOneByteCount();
        return (byte)n;
    }
    
    @Override
    public void advance() {
        this.incrReadByteCount(this.filled);
        this.filled = 0;
    }
    
    private void require(final int len) throws IOException {
        while (this.filled < len) {
            final int n = this.in.read(this.castBuffer, this.filled, len - this.filled);
            if (n < 0) {
                throw new EOFException();
            }
            this.filled += n;
        }
    }
    
    @Override
    public byte getByte() throws IOException {
        this.require(1);
        return this.castBuffer[0];
    }
    
    @Override
    public short getShort() throws IOException {
        this.require(2);
        return this.castByteBuffer.getShort(0);
    }
    
    @Override
    public int getInt() throws IOException {
        this.require(4);
        return this.castByteBuffer.getInt(0);
    }
    
    @Override
    public long getLong() throws IOException {
        this.require(8);
        return this.castByteBuffer.getLong(0);
    }
    
    @Override
    public float getFloat() throws IOException {
        this.require(4);
        return this.castByteBuffer.getFloat(0);
    }
    
    @Override
    public double getDouble() throws IOException {
        this.require(8);
        return this.castByteBuffer.getDouble(0);
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
}
