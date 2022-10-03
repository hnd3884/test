package java.nio;

import java.io.FileDescriptor;
import sun.nio.ch.DirectBuffer;

class DirectByteBufferR extends DirectByteBuffer implements DirectBuffer
{
    DirectByteBufferR(final int n) {
        super(n);
    }
    
    protected DirectByteBufferR(final int n, final long n2, final FileDescriptor fileDescriptor, final Runnable runnable) {
        super(n, n2, fileDescriptor, runnable);
    }
    
    DirectByteBufferR(final DirectBuffer directBuffer, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(directBuffer, n, n2, n3, n4, n5);
    }
    
    @Override
    public ByteBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 0;
        assert n2 >= 0;
        return new DirectByteBufferR(this, -1, 0, n, n, n2);
    }
    
    @Override
    public ByteBuffer duplicate() {
        return new DirectByteBufferR(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public ByteBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public ByteBuffer put(final byte b) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer put(final int n, final byte b) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer put(final ByteBuffer byteBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer put(final byte[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public boolean isDirect() {
        return true;
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    byte _get(final int n) {
        return DirectByteBufferR.unsafe.getByte(this.address + n);
    }
    
    @Override
    void _put(final int n, final byte b) {
        throw new ReadOnlyBufferException();
    }
    
    private ByteBuffer putChar(final long n, final char c) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putChar(final char c) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putChar(final int n, final char c) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public CharBuffer asCharBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 1;
        if (!DirectByteBufferR.unaligned && (this.address + position) % 2L != 0L) {
            return this.bigEndian ? new ByteBufferAsCharBufferRB(this, -1, 0, n, n, position) : new ByteBufferAsCharBufferRL(this, -1, 0, n, n, position);
        }
        return (CharBuffer)(this.nativeByteOrder ? new DirectCharBufferRU(this, -1, 0, n, n, position) : new DirectCharBufferRS(this, -1, 0, n, n, position));
    }
    
    private ByteBuffer putShort(final long n, final short n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putShort(final short n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putShort(final int n, final short n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ShortBuffer asShortBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 1;
        if (!DirectByteBufferR.unaligned && (this.address + position) % 2L != 0L) {
            return this.bigEndian ? new ByteBufferAsShortBufferRB(this, -1, 0, n, n, position) : new ByteBufferAsShortBufferRL(this, -1, 0, n, n, position);
        }
        return (ShortBuffer)(this.nativeByteOrder ? new DirectShortBufferRU(this, -1, 0, n, n, position) : new DirectShortBufferRS(this, -1, 0, n, n, position));
    }
    
    private ByteBuffer putInt(final long n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putInt(final int n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putInt(final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public IntBuffer asIntBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 2;
        if (!DirectByteBufferR.unaligned && (this.address + position) % 4L != 0L) {
            return this.bigEndian ? new ByteBufferAsIntBufferRB(this, -1, 0, n, n, position) : new ByteBufferAsIntBufferRL(this, -1, 0, n, n, position);
        }
        return (IntBuffer)(this.nativeByteOrder ? new DirectIntBufferRU(this, -1, 0, n, n, position) : new DirectIntBufferRS(this, -1, 0, n, n, position));
    }
    
    private ByteBuffer putLong(final long n, final long n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putLong(final long n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putLong(final int n, final long n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public LongBuffer asLongBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 3;
        if (!DirectByteBufferR.unaligned && (this.address + position) % 8L != 0L) {
            return this.bigEndian ? new ByteBufferAsLongBufferRB(this, -1, 0, n, n, position) : new ByteBufferAsLongBufferRL(this, -1, 0, n, n, position);
        }
        return (LongBuffer)(this.nativeByteOrder ? new DirectLongBufferRU(this, -1, 0, n, n, position) : new DirectLongBufferRS(this, -1, 0, n, n, position));
    }
    
    private ByteBuffer putFloat(final long n, final float n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putFloat(final float n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putFloat(final int n, final float n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public FloatBuffer asFloatBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 2;
        if (!DirectByteBufferR.unaligned && (this.address + position) % 4L != 0L) {
            return this.bigEndian ? new ByteBufferAsFloatBufferRB(this, -1, 0, n, n, position) : new ByteBufferAsFloatBufferRL(this, -1, 0, n, n, position);
        }
        return (FloatBuffer)(this.nativeByteOrder ? new DirectFloatBufferRU(this, -1, 0, n, n, position) : new DirectFloatBufferRS(this, -1, 0, n, n, position));
    }
    
    private ByteBuffer putDouble(final long n, final double n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putDouble(final double n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteBuffer putDouble(final int n, final double n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public DoubleBuffer asDoubleBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 3;
        if (!DirectByteBufferR.unaligned && (this.address + position) % 8L != 0L) {
            return this.bigEndian ? new ByteBufferAsDoubleBufferRB(this, -1, 0, n, n, position) : new ByteBufferAsDoubleBufferRL(this, -1, 0, n, n, position);
        }
        return (DoubleBuffer)(this.nativeByteOrder ? new DirectDoubleBufferRU(this, -1, 0, n, n, position) : new DirectDoubleBufferRS(this, -1, 0, n, n, position));
    }
}
