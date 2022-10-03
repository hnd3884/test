package org.msgpack.io;

import java.io.IOException;
import java.nio.ByteBuffer;

abstract class BufferedOutput implements Output
{
    protected byte[] buffer;
    protected int filled;
    protected final int bufferSize;
    protected ByteBuffer castByteBuffer;
    
    public BufferedOutput(int bufferSize) {
        if (bufferSize < 9) {
            bufferSize = 9;
        }
        this.bufferSize = bufferSize;
    }
    
    private void allocateNewBuffer() {
        this.buffer = new byte[this.bufferSize];
        this.castByteBuffer = ByteBuffer.wrap(this.buffer);
    }
    
    private void reserve(final int len) throws IOException {
        if (this.buffer == null) {
            this.allocateNewBuffer();
            return;
        }
        if (this.bufferSize - this.filled < len) {
            if (!this.flushBuffer(this.buffer, 0, this.filled)) {
                this.buffer = new byte[this.bufferSize];
                this.castByteBuffer = ByteBuffer.wrap(this.buffer);
            }
            this.filled = 0;
        }
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.buffer == null) {
            if (this.bufferSize < len) {
                this.flushBuffer(b, off, len);
                return;
            }
            this.allocateNewBuffer();
        }
        if (len <= this.bufferSize - this.filled) {
            System.arraycopy(b, off, this.buffer, this.filled, len);
            this.filled += len;
        }
        else if (len <= this.bufferSize) {
            if (!this.flushBuffer(this.buffer, 0, this.filled)) {
                this.allocateNewBuffer();
            }
            this.filled = 0;
            System.arraycopy(b, off, this.buffer, 0, len);
            this.filled = len;
        }
        else {
            this.flush();
            this.flushBuffer(b, off, len);
        }
    }
    
    @Override
    public void write(final ByteBuffer bb) throws IOException {
        final int len = bb.remaining();
        if (this.buffer == null) {
            if (this.bufferSize < len) {
                this.flushByteBuffer(bb);
                return;
            }
            this.allocateNewBuffer();
        }
        if (len <= this.bufferSize - this.filled) {
            bb.get(this.buffer, this.filled, len);
            this.filled += len;
        }
        else if (len <= this.bufferSize) {
            if (!this.flushBuffer(this.buffer, 0, this.filled)) {
                this.allocateNewBuffer();
            }
            this.filled = 0;
            bb.get(this.buffer, 0, len);
            this.filled = len;
        }
        else {
            this.flush();
            this.flushByteBuffer(bb);
        }
    }
    
    @Override
    public void writeByte(final byte v) throws IOException {
        this.reserve(1);
        this.buffer[this.filled++] = v;
    }
    
    @Override
    public void writeShort(final short v) throws IOException {
        this.reserve(2);
        this.castByteBuffer.putShort(this.filled, v);
        this.filled += 2;
    }
    
    @Override
    public void writeInt(final int v) throws IOException {
        this.reserve(4);
        this.castByteBuffer.putInt(this.filled, v);
        this.filled += 4;
    }
    
    @Override
    public void writeLong(final long v) throws IOException {
        this.reserve(8);
        this.castByteBuffer.putLong(this.filled, v);
        this.filled += 8;
    }
    
    @Override
    public void writeFloat(final float v) throws IOException {
        this.reserve(4);
        this.castByteBuffer.putFloat(this.filled, v);
        this.filled += 4;
    }
    
    @Override
    public void writeDouble(final double v) throws IOException {
        this.reserve(8);
        this.castByteBuffer.putDouble(this.filled, v);
        this.filled += 8;
    }
    
    @Override
    public void writeByteAndByte(final byte b, final byte v) throws IOException {
        this.reserve(2);
        this.buffer[this.filled++] = b;
        this.buffer[this.filled++] = v;
    }
    
    @Override
    public void writeByteAndShort(final byte b, final short v) throws IOException {
        this.reserve(3);
        this.buffer[this.filled++] = b;
        this.castByteBuffer.putShort(this.filled, v);
        this.filled += 2;
    }
    
    @Override
    public void writeByteAndInt(final byte b, final int v) throws IOException {
        this.reserve(5);
        this.buffer[this.filled++] = b;
        this.castByteBuffer.putInt(this.filled, v);
        this.filled += 4;
    }
    
    @Override
    public void writeByteAndLong(final byte b, final long v) throws IOException {
        this.reserve(9);
        this.buffer[this.filled++] = b;
        this.castByteBuffer.putLong(this.filled, v);
        this.filled += 8;
    }
    
    @Override
    public void writeByteAndFloat(final byte b, final float v) throws IOException {
        this.reserve(5);
        this.buffer[this.filled++] = b;
        this.castByteBuffer.putFloat(this.filled, v);
        this.filled += 4;
    }
    
    @Override
    public void writeByteAndDouble(final byte b, final double v) throws IOException {
        this.reserve(9);
        this.buffer[this.filled++] = b;
        this.castByteBuffer.putDouble(this.filled, v);
        this.filled += 8;
    }
    
    @Override
    public void flush() throws IOException {
        if (this.filled > 0) {
            if (!this.flushBuffer(this.buffer, 0, this.filled)) {
                this.buffer = null;
            }
            this.filled = 0;
        }
    }
    
    protected void flushByteBuffer(final ByteBuffer bb) throws IOException {
        if (bb.hasArray()) {
            final byte[] array = bb.array();
            final int offset = bb.arrayOffset();
            this.flushBuffer(array, offset + bb.position(), bb.remaining());
            bb.position(bb.limit());
        }
        else {
            final byte[] buf = new byte[bb.remaining()];
            bb.get(buf);
            this.flushBuffer(buf, 0, buf.length);
        }
    }
    
    protected abstract boolean flushBuffer(final byte[] p0, final int p1, final int p2) throws IOException;
}
