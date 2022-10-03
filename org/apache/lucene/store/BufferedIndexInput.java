package org.apache.lucene.store;

import java.io.EOFException;
import java.io.IOException;

public abstract class BufferedIndexInput extends IndexInput implements RandomAccessInput
{
    public static final int BUFFER_SIZE = 1024;
    public static final int MIN_BUFFER_SIZE = 8;
    public static final int MERGE_BUFFER_SIZE = 4096;
    private int bufferSize;
    protected byte[] buffer;
    private long bufferStart;
    private int bufferLength;
    private int bufferPosition;
    
    @Override
    public final byte readByte() throws IOException {
        if (this.bufferPosition >= this.bufferLength) {
            this.refill();
        }
        return this.buffer[this.bufferPosition++];
    }
    
    public BufferedIndexInput(final String resourceDesc) {
        this(resourceDesc, 1024);
    }
    
    public BufferedIndexInput(final String resourceDesc, final IOContext context) {
        this(resourceDesc, bufferSize(context));
    }
    
    public BufferedIndexInput(final String resourceDesc, final int bufferSize) {
        super(resourceDesc);
        this.bufferSize = 1024;
        this.bufferStart = 0L;
        this.bufferLength = 0;
        this.bufferPosition = 0;
        this.checkBufferSize(bufferSize);
        this.bufferSize = bufferSize;
    }
    
    public final void setBufferSize(final int newSize) {
        assert this.bufferSize == this.buffer.length : "buffer=" + this.buffer + " bufferSize=" + this.bufferSize + " buffer.length=" + ((this.buffer != null) ? this.buffer.length : 0);
        if (newSize != this.bufferSize) {
            this.checkBufferSize(newSize);
            this.bufferSize = newSize;
            if (this.buffer != null) {
                final byte[] newBuffer = new byte[newSize];
                final int leftInBuffer = this.bufferLength - this.bufferPosition;
                int numToCopy;
                if (leftInBuffer > newSize) {
                    numToCopy = newSize;
                }
                else {
                    numToCopy = leftInBuffer;
                }
                System.arraycopy(this.buffer, this.bufferPosition, newBuffer, 0, numToCopy);
                this.bufferStart += this.bufferPosition;
                this.bufferPosition = 0;
                this.bufferLength = numToCopy;
                this.newBuffer(newBuffer);
            }
        }
    }
    
    protected void newBuffer(final byte[] newBuffer) {
        this.buffer = newBuffer;
    }
    
    public final int getBufferSize() {
        return this.bufferSize;
    }
    
    private void checkBufferSize(final int bufferSize) {
        if (bufferSize < 8) {
            throw new IllegalArgumentException("bufferSize must be at least MIN_BUFFER_SIZE (got " + bufferSize + ")");
        }
    }
    
    @Override
    public final void readBytes(final byte[] b, final int offset, final int len) throws IOException {
        this.readBytes(b, offset, len, true);
    }
    
    @Override
    public final void readBytes(final byte[] b, int offset, int len, final boolean useBuffer) throws IOException {
        final int available = this.bufferLength - this.bufferPosition;
        if (len <= available) {
            if (len > 0) {
                System.arraycopy(this.buffer, this.bufferPosition, b, offset, len);
            }
            this.bufferPosition += len;
        }
        else {
            if (available > 0) {
                System.arraycopy(this.buffer, this.bufferPosition, b, offset, available);
                offset += available;
                len -= available;
                this.bufferPosition += available;
            }
            if (useBuffer && len < this.bufferSize) {
                this.refill();
                if (this.bufferLength < len) {
                    System.arraycopy(this.buffer, 0, b, offset, this.bufferLength);
                    throw new EOFException("read past EOF: " + this);
                }
                System.arraycopy(this.buffer, 0, b, offset, len);
                this.bufferPosition = len;
            }
            else {
                final long after = this.bufferStart + this.bufferPosition + len;
                if (after > this.length()) {
                    throw new EOFException("read past EOF: " + this);
                }
                this.readInternal(b, offset, len);
                this.bufferStart = after;
                this.bufferPosition = 0;
                this.bufferLength = 0;
            }
        }
    }
    
    @Override
    public final short readShort() throws IOException {
        if (2 <= this.bufferLength - this.bufferPosition) {
            return (short)((this.buffer[this.bufferPosition++] & 0xFF) << 8 | (this.buffer[this.bufferPosition++] & 0xFF));
        }
        return super.readShort();
    }
    
    @Override
    public final int readInt() throws IOException {
        if (4 <= this.bufferLength - this.bufferPosition) {
            return (this.buffer[this.bufferPosition++] & 0xFF) << 24 | (this.buffer[this.bufferPosition++] & 0xFF) << 16 | (this.buffer[this.bufferPosition++] & 0xFF) << 8 | (this.buffer[this.bufferPosition++] & 0xFF);
        }
        return super.readInt();
    }
    
    @Override
    public final long readLong() throws IOException {
        if (8 <= this.bufferLength - this.bufferPosition) {
            final int i1 = (this.buffer[this.bufferPosition++] & 0xFF) << 24 | (this.buffer[this.bufferPosition++] & 0xFF) << 16 | (this.buffer[this.bufferPosition++] & 0xFF) << 8 | (this.buffer[this.bufferPosition++] & 0xFF);
            final int i2 = (this.buffer[this.bufferPosition++] & 0xFF) << 24 | (this.buffer[this.bufferPosition++] & 0xFF) << 16 | (this.buffer[this.bufferPosition++] & 0xFF) << 8 | (this.buffer[this.bufferPosition++] & 0xFF);
            return (long)i1 << 32 | ((long)i2 & 0xFFFFFFFFL);
        }
        return super.readLong();
    }
    
    @Override
    public final int readVInt() throws IOException {
        if (5 > this.bufferLength - this.bufferPosition) {
            return super.readVInt();
        }
        byte b = this.buffer[this.bufferPosition++];
        if (b >= 0) {
            return b;
        }
        int i = b & 0x7F;
        b = this.buffer[this.bufferPosition++];
        i |= (b & 0x7F) << 7;
        if (b >= 0) {
            return i;
        }
        b = this.buffer[this.bufferPosition++];
        i |= (b & 0x7F) << 14;
        if (b >= 0) {
            return i;
        }
        b = this.buffer[this.bufferPosition++];
        i |= (b & 0x7F) << 21;
        if (b >= 0) {
            return i;
        }
        b = this.buffer[this.bufferPosition++];
        i |= (b & 0xF) << 28;
        if ((b & 0xF0) == 0x0) {
            return i;
        }
        throw new IOException("Invalid vInt detected (too many bits)");
    }
    
    @Override
    public final long readVLong() throws IOException {
        if (9 > this.bufferLength - this.bufferPosition) {
            return super.readVLong();
        }
        byte b = this.buffer[this.bufferPosition++];
        if (b >= 0) {
            return b;
        }
        long i = (long)b & 0x7FL;
        b = this.buffer[this.bufferPosition++];
        i |= ((long)b & 0x7FL) << 7;
        if (b >= 0) {
            return i;
        }
        b = this.buffer[this.bufferPosition++];
        i |= ((long)b & 0x7FL) << 14;
        if (b >= 0) {
            return i;
        }
        b = this.buffer[this.bufferPosition++];
        i |= ((long)b & 0x7FL) << 21;
        if (b >= 0) {
            return i;
        }
        b = this.buffer[this.bufferPosition++];
        i |= ((long)b & 0x7FL) << 28;
        if (b >= 0) {
            return i;
        }
        b = this.buffer[this.bufferPosition++];
        i |= ((long)b & 0x7FL) << 35;
        if (b >= 0) {
            return i;
        }
        b = this.buffer[this.bufferPosition++];
        i |= ((long)b & 0x7FL) << 42;
        if (b >= 0) {
            return i;
        }
        b = this.buffer[this.bufferPosition++];
        i |= ((long)b & 0x7FL) << 49;
        if (b >= 0) {
            return i;
        }
        b = this.buffer[this.bufferPosition++];
        i |= ((long)b & 0x7FL) << 56;
        if (b >= 0) {
            return i;
        }
        throw new IOException("Invalid vLong detected (negative values disallowed)");
    }
    
    @Override
    public final byte readByte(final long pos) throws IOException {
        long index = pos - this.bufferStart;
        if (index < 0L || index >= this.bufferLength) {
            this.bufferStart = pos;
            this.bufferPosition = 0;
            this.bufferLength = 0;
            this.seekInternal(pos);
            this.refill();
            index = 0L;
        }
        return this.buffer[(int)index];
    }
    
    @Override
    public final short readShort(final long pos) throws IOException {
        long index = pos - this.bufferStart;
        if (index < 0L || index >= this.bufferLength - 1) {
            this.bufferStart = pos;
            this.bufferPosition = 0;
            this.bufferLength = 0;
            this.seekInternal(pos);
            this.refill();
            index = 0L;
        }
        return (short)((this.buffer[(int)index] & 0xFF) << 8 | (this.buffer[(int)index + 1] & 0xFF));
    }
    
    @Override
    public final int readInt(final long pos) throws IOException {
        long index = pos - this.bufferStart;
        if (index < 0L || index >= this.bufferLength - 3) {
            this.bufferStart = pos;
            this.bufferPosition = 0;
            this.bufferLength = 0;
            this.seekInternal(pos);
            this.refill();
            index = 0L;
        }
        return (this.buffer[(int)index] & 0xFF) << 24 | (this.buffer[(int)index + 1] & 0xFF) << 16 | (this.buffer[(int)index + 2] & 0xFF) << 8 | (this.buffer[(int)index + 3] & 0xFF);
    }
    
    @Override
    public final long readLong(final long pos) throws IOException {
        long index = pos - this.bufferStart;
        if (index < 0L || index >= this.bufferLength - 7) {
            this.bufferStart = pos;
            this.bufferPosition = 0;
            this.bufferLength = 0;
            this.seekInternal(pos);
            this.refill();
            index = 0L;
        }
        final int i1 = (this.buffer[(int)index] & 0xFF) << 24 | (this.buffer[(int)index + 1] & 0xFF) << 16 | (this.buffer[(int)index + 2] & 0xFF) << 8 | (this.buffer[(int)index + 3] & 0xFF);
        final int i2 = (this.buffer[(int)index + 4] & 0xFF) << 24 | (this.buffer[(int)index + 5] & 0xFF) << 16 | (this.buffer[(int)index + 6] & 0xFF) << 8 | (this.buffer[(int)index + 7] & 0xFF);
        return (long)i1 << 32 | ((long)i2 & 0xFFFFFFFFL);
    }
    
    private void refill() throws IOException {
        final long start = this.bufferStart + this.bufferPosition;
        long end = start + this.bufferSize;
        if (end > this.length()) {
            end = this.length();
        }
        final int newLength = (int)(end - start);
        if (newLength <= 0) {
            throw new EOFException("read past EOF: " + this);
        }
        if (this.buffer == null) {
            this.newBuffer(new byte[this.bufferSize]);
            this.seekInternal(this.bufferStart);
        }
        this.readInternal(this.buffer, 0, newLength);
        this.bufferLength = newLength;
        this.bufferStart = start;
        this.bufferPosition = 0;
    }
    
    protected abstract void readInternal(final byte[] p0, final int p1, final int p2) throws IOException;
    
    @Override
    public final long getFilePointer() {
        return this.bufferStart + this.bufferPosition;
    }
    
    @Override
    public final void seek(final long pos) throws IOException {
        if (pos >= this.bufferStart && pos < this.bufferStart + this.bufferLength) {
            this.bufferPosition = (int)(pos - this.bufferStart);
        }
        else {
            this.bufferStart = pos;
            this.bufferPosition = 0;
            this.bufferLength = 0;
            this.seekInternal(pos);
        }
    }
    
    protected abstract void seekInternal(final long p0) throws IOException;
    
    @Override
    public BufferedIndexInput clone() {
        final BufferedIndexInput clone = (BufferedIndexInput)super.clone();
        clone.buffer = null;
        clone.bufferLength = 0;
        clone.bufferPosition = 0;
        clone.bufferStart = this.getFilePointer();
        return clone;
    }
    
    @Override
    public IndexInput slice(final String sliceDescription, final long offset, final long length) throws IOException {
        return wrap(sliceDescription, this, offset, length);
    }
    
    protected final int flushBuffer(final IndexOutput out, final long numBytes) throws IOException {
        int toCopy = this.bufferLength - this.bufferPosition;
        if (toCopy > numBytes) {
            toCopy = (int)numBytes;
        }
        if (toCopy > 0) {
            out.writeBytes(this.buffer, this.bufferPosition, toCopy);
            this.bufferPosition += toCopy;
        }
        return toCopy;
    }
    
    public static int bufferSize(final IOContext context) {
        switch (context.context) {
            case MERGE: {
                return 4096;
            }
            default: {
                return 1024;
            }
        }
    }
    
    public static BufferedIndexInput wrap(final String sliceDescription, final IndexInput other, final long offset, final long length) {
        return new SlicedIndexInput(sliceDescription, other, offset, length);
    }
    
    private static final class SlicedIndexInput extends BufferedIndexInput
    {
        IndexInput base;
        long fileOffset;
        long length;
        
        SlicedIndexInput(final String sliceDescription, final IndexInput base, final long offset, final long length) {
            super((sliceDescription == null) ? base.toString() : (base.toString() + " [slice=" + sliceDescription + "]"), 1024);
            if (offset < 0L || length < 0L || offset + length > base.length()) {
                throw new IllegalArgumentException("slice() " + sliceDescription + " out of bounds: " + base);
            }
            this.base = base.clone();
            this.fileOffset = offset;
            this.length = length;
        }
        
        @Override
        public SlicedIndexInput clone() {
            final SlicedIndexInput clone = (SlicedIndexInput)super.clone();
            clone.base = this.base.clone();
            clone.fileOffset = this.fileOffset;
            clone.length = this.length;
            return clone;
        }
        
        @Override
        protected void readInternal(final byte[] b, final int offset, final int len) throws IOException {
            final long start = this.getFilePointer();
            if (start + len > this.length) {
                throw new EOFException("read past EOF: " + this);
            }
            this.base.seek(this.fileOffset + start);
            this.base.readBytes(b, offset, len, false);
        }
        
        @Override
        protected void seekInternal(final long pos) {
        }
        
        @Override
        public void close() throws IOException {
            this.base.close();
        }
        
        @Override
        public long length() {
            return this.length;
        }
    }
}
