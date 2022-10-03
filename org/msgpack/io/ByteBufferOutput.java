package org.msgpack.io;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

public class ByteBufferOutput implements Output
{
    private ByteBuffer buffer;
    private ExpandBufferCallback callback;
    
    public ByteBufferOutput(final ByteBuffer buffer) {
        this(buffer, null);
    }
    
    public ByteBufferOutput(final ByteBuffer buffer, final ExpandBufferCallback callback) {
        this.buffer = buffer;
        this.callback = callback;
    }
    
    private void reserve(final int len) throws IOException {
        if (len <= this.buffer.remaining()) {
            return;
        }
        if (this.callback == null) {
            throw new BufferOverflowException();
        }
        this.buffer = this.callback.call(this.buffer, len);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.reserve(len);
        this.buffer.put(b, off, len);
    }
    
    @Override
    public void write(final ByteBuffer bb) throws IOException {
        this.reserve(bb.remaining());
        this.buffer.put(bb);
    }
    
    @Override
    public void writeByte(final byte v) throws IOException {
        this.reserve(1);
        this.buffer.put(v);
    }
    
    @Override
    public void writeShort(final short v) throws IOException {
        this.reserve(2);
        this.buffer.putShort(v);
    }
    
    @Override
    public void writeInt(final int v) throws IOException {
        this.reserve(4);
        this.buffer.putInt(v);
    }
    
    @Override
    public void writeLong(final long v) throws IOException {
        this.reserve(8);
        this.buffer.putLong(v);
    }
    
    @Override
    public void writeFloat(final float v) throws IOException {
        this.reserve(4);
        this.buffer.putFloat(v);
    }
    
    @Override
    public void writeDouble(final double v) throws IOException {
        this.reserve(8);
        this.buffer.putDouble(v);
    }
    
    @Override
    public void writeByteAndByte(final byte b, final byte v) throws IOException {
        this.reserve(2);
        this.buffer.put(b);
        this.buffer.put(v);
    }
    
    @Override
    public void writeByteAndShort(final byte b, final short v) throws IOException {
        this.reserve(3);
        this.buffer.put(b);
        this.buffer.putShort(v);
    }
    
    @Override
    public void writeByteAndInt(final byte b, final int v) throws IOException {
        this.reserve(5);
        this.buffer.put(b);
        this.buffer.putInt(v);
    }
    
    @Override
    public void writeByteAndLong(final byte b, final long v) throws IOException {
        this.reserve(9);
        this.buffer.put(b);
        this.buffer.putLong(v);
    }
    
    @Override
    public void writeByteAndFloat(final byte b, final float v) throws IOException {
        this.reserve(5);
        this.buffer.put(b);
        this.buffer.putFloat(v);
    }
    
    @Override
    public void writeByteAndDouble(final byte b, final double v) throws IOException {
        this.reserve(9);
        this.buffer.put(b);
        this.buffer.putDouble(v);
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    @Override
    public void close() {
    }
    
    public interface ExpandBufferCallback
    {
        ByteBuffer call(final ByteBuffer p0, final int p1) throws IOException;
    }
}
