package java.nio;

import java.io.FileDescriptor;
import sun.misc.VM;
import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectByteBuffer extends MappedByteBuffer implements DirectBuffer
{
    protected static final Unsafe unsafe;
    private static final long arrayBaseOffset;
    protected static final boolean unaligned;
    private final Object att;
    private final Cleaner cleaner;
    
    @Override
    public Object attachment() {
        return this.att;
    }
    
    @Override
    public Cleaner cleaner() {
        return this.cleaner;
    }
    
    DirectByteBuffer(final int n) {
        super(-1, 0, n, n);
        final boolean directMemoryPageAligned = VM.isDirectMemoryPageAligned();
        final int pageSize = Bits.pageSize();
        final long max = Math.max(1L, n + (long)(directMemoryPageAligned ? pageSize : 0));
        Bits.reserveMemory(max, n);
        long allocateMemory;
        try {
            allocateMemory = DirectByteBuffer.unsafe.allocateMemory(max);
        }
        catch (final OutOfMemoryError outOfMemoryError) {
            Bits.unreserveMemory(max, n);
            throw outOfMemoryError;
        }
        DirectByteBuffer.unsafe.setMemory(allocateMemory, max, (byte)0);
        if (directMemoryPageAligned && allocateMemory % pageSize != 0L) {
            this.address = allocateMemory + pageSize - (allocateMemory & (long)(pageSize - 1));
        }
        else {
            this.address = allocateMemory;
        }
        this.cleaner = Cleaner.create(this, new Deallocator(allocateMemory, max, n));
        this.att = null;
    }
    
    DirectByteBuffer(final long address, final int n, final Object att) {
        super(-1, 0, n, n);
        this.address = address;
        this.cleaner = null;
        this.att = att;
    }
    
    private DirectByteBuffer(final long address, final int n) {
        super(-1, 0, n, n);
        this.address = address;
        this.cleaner = null;
        this.att = null;
    }
    
    protected DirectByteBuffer(final int n, final long address, final FileDescriptor fileDescriptor, final Runnable runnable) {
        super(-1, 0, n, n, fileDescriptor);
        this.address = address;
        this.cleaner = Cleaner.create(this, runnable);
        this.att = null;
    }
    
    DirectByteBuffer(final DirectBuffer att, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4);
        this.address = att.address() + n5;
        this.cleaner = null;
        this.att = att;
    }
    
    @Override
    public ByteBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 0;
        assert n2 >= 0;
        return new DirectByteBuffer(this, -1, 0, n, n, n2);
    }
    
    @Override
    public ByteBuffer duplicate() {
        return new DirectByteBuffer(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public ByteBuffer asReadOnlyBuffer() {
        return new DirectByteBufferR(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public long address() {
        return this.address;
    }
    
    private long ix(final int n) {
        return this.address + ((long)n << 0);
    }
    
    @Override
    public byte get() {
        return DirectByteBuffer.unsafe.getByte(this.ix(this.nextGetIndex()));
    }
    
    @Override
    public byte get(final int n) {
        return DirectByteBuffer.unsafe.getByte(this.ix(this.checkIndex(n)));
    }
    
    @Override
    public ByteBuffer get(final byte[] array, final int n, final int n2) {
        if ((long)n2 << 0 > 6L) {
            Buffer.checkBounds(n, n2, array.length);
            final int position = this.position();
            final int limit = this.limit();
            assert position <= limit;
            if (n2 > ((position <= limit) ? (limit - position) : 0)) {
                throw new BufferUnderflowException();
            }
            Bits.copyToArray(this.ix(position), array, DirectByteBuffer.arrayBaseOffset, (long)n << 0, (long)n2 << 0);
            this.position(position + n2);
        }
        else {
            super.get(array, n, n2);
        }
        return this;
    }
    
    @Override
    public ByteBuffer put(final byte b) {
        DirectByteBuffer.unsafe.putByte(this.ix(this.nextPutIndex()), b);
        return this;
    }
    
    @Override
    public ByteBuffer put(final int n, final byte b) {
        DirectByteBuffer.unsafe.putByte(this.ix(this.checkIndex(n)), b);
        return this;
    }
    
    @Override
    public ByteBuffer put(final ByteBuffer byteBuffer) {
        if (byteBuffer instanceof DirectByteBuffer) {
            if (byteBuffer == this) {
                throw new IllegalArgumentException();
            }
            final DirectByteBuffer directByteBuffer = (DirectByteBuffer)byteBuffer;
            final int position = directByteBuffer.position();
            final int limit = directByteBuffer.limit();
            assert position <= limit;
            final int n = (position <= limit) ? (limit - position) : 0;
            final int position2 = this.position();
            final int limit2 = this.limit();
            assert position2 <= limit2;
            if (n > ((position2 <= limit2) ? (limit2 - position2) : 0)) {
                throw new BufferOverflowException();
            }
            DirectByteBuffer.unsafe.copyMemory(directByteBuffer.ix(position), this.ix(position2), (long)n << 0);
            directByteBuffer.position(position + n);
            this.position(position2 + n);
        }
        else if (byteBuffer.hb != null) {
            final int position3 = byteBuffer.position();
            final int limit3 = byteBuffer.limit();
            assert position3 <= limit3;
            final int n2 = (position3 <= limit3) ? (limit3 - position3) : 0;
            this.put(byteBuffer.hb, byteBuffer.offset + position3, n2);
            byteBuffer.position(position3 + n2);
        }
        else {
            super.put(byteBuffer);
        }
        return this;
    }
    
    @Override
    public ByteBuffer put(final byte[] array, final int n, final int n2) {
        if ((long)n2 << 0 > 6L) {
            Buffer.checkBounds(n, n2, array.length);
            final int position = this.position();
            final int limit = this.limit();
            assert position <= limit;
            if (n2 > ((position <= limit) ? (limit - position) : 0)) {
                throw new BufferOverflowException();
            }
            Bits.copyFromArray(array, DirectByteBuffer.arrayBaseOffset, (long)n << 0, this.ix(position), (long)n2 << 0);
            this.position(position + n2);
        }
        else {
            super.put(array, n, n2);
        }
        return this;
    }
    
    @Override
    public ByteBuffer compact() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        DirectByteBuffer.unsafe.copyMemory(this.ix(position), this.ix(0), (long)n << 0);
        this.position(n);
        this.limit(this.capacity());
        this.discardMark();
        return this;
    }
    
    @Override
    public boolean isDirect() {
        return true;
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    byte _get(final int n) {
        return DirectByteBuffer.unsafe.getByte(this.address + n);
    }
    
    @Override
    void _put(final int n, final byte b) {
        DirectByteBuffer.unsafe.putByte(this.address + n, b);
    }
    
    private char getChar(final long n) {
        if (DirectByteBuffer.unaligned) {
            final char char1 = DirectByteBuffer.unsafe.getChar(n);
            return this.nativeByteOrder ? char1 : Bits.swap(char1);
        }
        return Bits.getChar(n, this.bigEndian);
    }
    
    @Override
    public char getChar() {
        return this.getChar(this.ix(this.nextGetIndex(2)));
    }
    
    @Override
    public char getChar(final int n) {
        return this.getChar(this.ix(this.checkIndex(n, 2)));
    }
    
    private ByteBuffer putChar(final long n, final char c) {
        if (DirectByteBuffer.unaligned) {
            DirectByteBuffer.unsafe.putChar(n, this.nativeByteOrder ? c : Bits.swap(c));
        }
        else {
            Bits.putChar(n, c, this.bigEndian);
        }
        return this;
    }
    
    @Override
    public ByteBuffer putChar(final char c) {
        this.putChar(this.ix(this.nextPutIndex(2)), c);
        return this;
    }
    
    @Override
    public ByteBuffer putChar(final int n, final char c) {
        this.putChar(this.ix(this.checkIndex(n, 2)), c);
        return this;
    }
    
    @Override
    public CharBuffer asCharBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 1;
        if (!DirectByteBuffer.unaligned && (this.address + position) % 2L != 0L) {
            return this.bigEndian ? new ByteBufferAsCharBufferB(this, -1, 0, n, n, position) : new ByteBufferAsCharBufferL(this, -1, 0, n, n, position);
        }
        return (CharBuffer)(this.nativeByteOrder ? new DirectCharBufferU(this, -1, 0, n, n, position) : new DirectCharBufferS(this, -1, 0, n, n, position));
    }
    
    private short getShort(final long n) {
        if (DirectByteBuffer.unaligned) {
            final short short1 = DirectByteBuffer.unsafe.getShort(n);
            return this.nativeByteOrder ? short1 : Bits.swap(short1);
        }
        return Bits.getShort(n, this.bigEndian);
    }
    
    @Override
    public short getShort() {
        return this.getShort(this.ix(this.nextGetIndex(2)));
    }
    
    @Override
    public short getShort(final int n) {
        return this.getShort(this.ix(this.checkIndex(n, 2)));
    }
    
    private ByteBuffer putShort(final long n, final short n2) {
        if (DirectByteBuffer.unaligned) {
            DirectByteBuffer.unsafe.putShort(n, this.nativeByteOrder ? n2 : Bits.swap(n2));
        }
        else {
            Bits.putShort(n, n2, this.bigEndian);
        }
        return this;
    }
    
    @Override
    public ByteBuffer putShort(final short n) {
        this.putShort(this.ix(this.nextPutIndex(2)), n);
        return this;
    }
    
    @Override
    public ByteBuffer putShort(final int n, final short n2) {
        this.putShort(this.ix(this.checkIndex(n, 2)), n2);
        return this;
    }
    
    @Override
    public ShortBuffer asShortBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 1;
        if (!DirectByteBuffer.unaligned && (this.address + position) % 2L != 0L) {
            return this.bigEndian ? new ByteBufferAsShortBufferB(this, -1, 0, n, n, position) : new ByteBufferAsShortBufferL(this, -1, 0, n, n, position);
        }
        return (ShortBuffer)(this.nativeByteOrder ? new DirectShortBufferU(this, -1, 0, n, n, position) : new DirectShortBufferS(this, -1, 0, n, n, position));
    }
    
    private int getInt(final long n) {
        if (DirectByteBuffer.unaligned) {
            final int int1 = DirectByteBuffer.unsafe.getInt(n);
            return this.nativeByteOrder ? int1 : Bits.swap(int1);
        }
        return Bits.getInt(n, this.bigEndian);
    }
    
    @Override
    public int getInt() {
        return this.getInt(this.ix(this.nextGetIndex(4)));
    }
    
    @Override
    public int getInt(final int n) {
        return this.getInt(this.ix(this.checkIndex(n, 4)));
    }
    
    private ByteBuffer putInt(final long n, final int n2) {
        if (DirectByteBuffer.unaligned) {
            DirectByteBuffer.unsafe.putInt(n, this.nativeByteOrder ? n2 : Bits.swap(n2));
        }
        else {
            Bits.putInt(n, n2, this.bigEndian);
        }
        return this;
    }
    
    @Override
    public ByteBuffer putInt(final int n) {
        this.putInt(this.ix(this.nextPutIndex(4)), n);
        return this;
    }
    
    @Override
    public ByteBuffer putInt(final int n, final int n2) {
        this.putInt(this.ix(this.checkIndex(n, 4)), n2);
        return this;
    }
    
    @Override
    public IntBuffer asIntBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 2;
        if (!DirectByteBuffer.unaligned && (this.address + position) % 4L != 0L) {
            return this.bigEndian ? new ByteBufferAsIntBufferB(this, -1, 0, n, n, position) : new ByteBufferAsIntBufferL(this, -1, 0, n, n, position);
        }
        return (IntBuffer)(this.nativeByteOrder ? new DirectIntBufferU(this, -1, 0, n, n, position) : new DirectIntBufferS(this, -1, 0, n, n, position));
    }
    
    private long getLong(final long n) {
        if (DirectByteBuffer.unaligned) {
            final long long1 = DirectByteBuffer.unsafe.getLong(n);
            return this.nativeByteOrder ? long1 : Bits.swap(long1);
        }
        return Bits.getLong(n, this.bigEndian);
    }
    
    @Override
    public long getLong() {
        return this.getLong(this.ix(this.nextGetIndex(8)));
    }
    
    @Override
    public long getLong(final int n) {
        return this.getLong(this.ix(this.checkIndex(n, 8)));
    }
    
    private ByteBuffer putLong(final long n, final long n2) {
        if (DirectByteBuffer.unaligned) {
            DirectByteBuffer.unsafe.putLong(n, this.nativeByteOrder ? n2 : Bits.swap(n2));
        }
        else {
            Bits.putLong(n, n2, this.bigEndian);
        }
        return this;
    }
    
    @Override
    public ByteBuffer putLong(final long n) {
        this.putLong(this.ix(this.nextPutIndex(8)), n);
        return this;
    }
    
    @Override
    public ByteBuffer putLong(final int n, final long n2) {
        this.putLong(this.ix(this.checkIndex(n, 8)), n2);
        return this;
    }
    
    @Override
    public LongBuffer asLongBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 3;
        if (!DirectByteBuffer.unaligned && (this.address + position) % 8L != 0L) {
            return this.bigEndian ? new ByteBufferAsLongBufferB(this, -1, 0, n, n, position) : new ByteBufferAsLongBufferL(this, -1, 0, n, n, position);
        }
        return (LongBuffer)(this.nativeByteOrder ? new DirectLongBufferU(this, -1, 0, n, n, position) : new DirectLongBufferS(this, -1, 0, n, n, position));
    }
    
    private float getFloat(final long n) {
        if (DirectByteBuffer.unaligned) {
            final int int1 = DirectByteBuffer.unsafe.getInt(n);
            return Float.intBitsToFloat(this.nativeByteOrder ? int1 : Bits.swap(int1));
        }
        return Bits.getFloat(n, this.bigEndian);
    }
    
    @Override
    public float getFloat() {
        return this.getFloat(this.ix(this.nextGetIndex(4)));
    }
    
    @Override
    public float getFloat(final int n) {
        return this.getFloat(this.ix(this.checkIndex(n, 4)));
    }
    
    private ByteBuffer putFloat(final long n, final float n2) {
        if (DirectByteBuffer.unaligned) {
            final int floatToRawIntBits = Float.floatToRawIntBits(n2);
            DirectByteBuffer.unsafe.putInt(n, this.nativeByteOrder ? floatToRawIntBits : Bits.swap(floatToRawIntBits));
        }
        else {
            Bits.putFloat(n, n2, this.bigEndian);
        }
        return this;
    }
    
    @Override
    public ByteBuffer putFloat(final float n) {
        this.putFloat(this.ix(this.nextPutIndex(4)), n);
        return this;
    }
    
    @Override
    public ByteBuffer putFloat(final int n, final float n2) {
        this.putFloat(this.ix(this.checkIndex(n, 4)), n2);
        return this;
    }
    
    @Override
    public FloatBuffer asFloatBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 2;
        if (!DirectByteBuffer.unaligned && (this.address + position) % 4L != 0L) {
            return this.bigEndian ? new ByteBufferAsFloatBufferB(this, -1, 0, n, n, position) : new ByteBufferAsFloatBufferL(this, -1, 0, n, n, position);
        }
        return (FloatBuffer)(this.nativeByteOrder ? new DirectFloatBufferU(this, -1, 0, n, n, position) : new DirectFloatBufferS(this, -1, 0, n, n, position));
    }
    
    private double getDouble(final long n) {
        if (DirectByteBuffer.unaligned) {
            final long long1 = DirectByteBuffer.unsafe.getLong(n);
            return Double.longBitsToDouble(this.nativeByteOrder ? long1 : Bits.swap(long1));
        }
        return Bits.getDouble(n, this.bigEndian);
    }
    
    @Override
    public double getDouble() {
        return this.getDouble(this.ix(this.nextGetIndex(8)));
    }
    
    @Override
    public double getDouble(final int n) {
        return this.getDouble(this.ix(this.checkIndex(n, 8)));
    }
    
    private ByteBuffer putDouble(final long n, final double n2) {
        if (DirectByteBuffer.unaligned) {
            final long doubleToRawLongBits = Double.doubleToRawLongBits(n2);
            DirectByteBuffer.unsafe.putLong(n, this.nativeByteOrder ? doubleToRawLongBits : Bits.swap(doubleToRawLongBits));
        }
        else {
            Bits.putDouble(n, n2, this.bigEndian);
        }
        return this;
    }
    
    @Override
    public ByteBuffer putDouble(final double n) {
        this.putDouble(this.ix(this.nextPutIndex(8)), n);
        return this;
    }
    
    @Override
    public ByteBuffer putDouble(final int n, final double n2) {
        this.putDouble(this.ix(this.checkIndex(n, 8)), n2);
        return this;
    }
    
    @Override
    public DoubleBuffer asDoubleBuffer() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = ((position <= limit) ? (limit - position) : 0) >> 3;
        if (!DirectByteBuffer.unaligned && (this.address + position) % 8L != 0L) {
            return this.bigEndian ? new ByteBufferAsDoubleBufferB(this, -1, 0, n, n, position) : new ByteBufferAsDoubleBufferL(this, -1, 0, n, n, position);
        }
        return (DoubleBuffer)(this.nativeByteOrder ? new DirectDoubleBufferU(this, -1, 0, n, n, position) : new DirectDoubleBufferS(this, -1, 0, n, n, position));
    }
    
    static {
        unsafe = Bits.unsafe();
        arrayBaseOffset = DirectByteBuffer.unsafe.arrayBaseOffset(byte[].class);
        unaligned = Bits.unaligned();
    }
    
    private static class Deallocator implements Runnable
    {
        private static Unsafe unsafe;
        private long address;
        private long size;
        private int capacity;
        
        private Deallocator(final long address, final long size, final int capacity) {
            assert address != 0L;
            this.address = address;
            this.size = size;
            this.capacity = capacity;
        }
        
        @Override
        public void run() {
            if (this.address == 0L) {
                return;
            }
            Deallocator.unsafe.freeMemory(this.address);
            this.address = 0L;
            Bits.unreserveMemory(this.size, this.capacity);
        }
        
        static {
            Deallocator.unsafe = Unsafe.getUnsafe();
        }
    }
}
