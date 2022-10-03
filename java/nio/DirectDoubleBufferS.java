package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectDoubleBufferS extends DoubleBuffer implements DirectBuffer
{
    protected static final Unsafe unsafe;
    private static final long arrayBaseOffset;
    protected static final boolean unaligned;
    private final Object att;
    
    @Override
    public Object attachment() {
        return this.att;
    }
    
    @Override
    public Cleaner cleaner() {
        return null;
    }
    
    DirectDoubleBufferS(final DirectBuffer att, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4);
        this.address = att.address() + n5;
        this.att = att;
    }
    
    @Override
    public DoubleBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 3;
        assert n2 >= 0;
        return new DirectDoubleBufferS(this, -1, 0, n, n, n2);
    }
    
    @Override
    public DoubleBuffer duplicate() {
        return new DirectDoubleBufferS(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public DoubleBuffer asReadOnlyBuffer() {
        return new DirectDoubleBufferRS(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public long address() {
        return this.address;
    }
    
    private long ix(final int n) {
        return this.address + ((long)n << 3);
    }
    
    @Override
    public double get() {
        return Double.longBitsToDouble(Bits.swap(DirectDoubleBufferS.unsafe.getLong(this.ix(this.nextGetIndex()))));
    }
    
    @Override
    public double get(final int n) {
        return Double.longBitsToDouble(Bits.swap(DirectDoubleBufferS.unsafe.getLong(this.ix(this.checkIndex(n)))));
    }
    
    @Override
    public DoubleBuffer get(final double[] array, final int n, final int n2) {
        if ((long)n2 << 3 > 6L) {
            Buffer.checkBounds(n, n2, array.length);
            final int position = this.position();
            final int limit = this.limit();
            assert position <= limit;
            if (n2 > ((position <= limit) ? (limit - position) : 0)) {
                throw new BufferUnderflowException();
            }
            if (this.order() != ByteOrder.nativeOrder()) {
                Bits.copyToLongArray(this.ix(position), array, (long)n << 3, (long)n2 << 3);
            }
            else {
                Bits.copyToArray(this.ix(position), array, DirectDoubleBufferS.arrayBaseOffset, (long)n << 3, (long)n2 << 3);
            }
            this.position(position + n2);
        }
        else {
            super.get(array, n, n2);
        }
        return this;
    }
    
    @Override
    public DoubleBuffer put(final double n) {
        DirectDoubleBufferS.unsafe.putLong(this.ix(this.nextPutIndex()), Bits.swap(Double.doubleToRawLongBits(n)));
        return this;
    }
    
    @Override
    public DoubleBuffer put(final int n, final double n2) {
        DirectDoubleBufferS.unsafe.putLong(this.ix(this.checkIndex(n)), Bits.swap(Double.doubleToRawLongBits(n2)));
        return this;
    }
    
    @Override
    public DoubleBuffer put(final DoubleBuffer doubleBuffer) {
        if (doubleBuffer instanceof DirectDoubleBufferS) {
            if (doubleBuffer == this) {
                throw new IllegalArgumentException();
            }
            final DirectDoubleBufferS directDoubleBufferS = (DirectDoubleBufferS)doubleBuffer;
            final int position = directDoubleBufferS.position();
            final int limit = directDoubleBufferS.limit();
            assert position <= limit;
            final int n = (position <= limit) ? (limit - position) : 0;
            final int position2 = this.position();
            final int limit2 = this.limit();
            assert position2 <= limit2;
            if (n > ((position2 <= limit2) ? (limit2 - position2) : 0)) {
                throw new BufferOverflowException();
            }
            DirectDoubleBufferS.unsafe.copyMemory(directDoubleBufferS.ix(position), this.ix(position2), (long)n << 3);
            directDoubleBufferS.position(position + n);
            this.position(position2 + n);
        }
        else if (doubleBuffer.hb != null) {
            final int position3 = doubleBuffer.position();
            final int limit3 = doubleBuffer.limit();
            assert position3 <= limit3;
            final int n2 = (position3 <= limit3) ? (limit3 - position3) : 0;
            this.put(doubleBuffer.hb, doubleBuffer.offset + position3, n2);
            doubleBuffer.position(position3 + n2);
        }
        else {
            super.put(doubleBuffer);
        }
        return this;
    }
    
    @Override
    public DoubleBuffer put(final double[] array, final int n, final int n2) {
        if ((long)n2 << 3 > 6L) {
            Buffer.checkBounds(n, n2, array.length);
            final int position = this.position();
            final int limit = this.limit();
            assert position <= limit;
            if (n2 > ((position <= limit) ? (limit - position) : 0)) {
                throw new BufferOverflowException();
            }
            if (this.order() != ByteOrder.nativeOrder()) {
                Bits.copyFromLongArray(array, (long)n << 3, this.ix(position), (long)n2 << 3);
            }
            else {
                Bits.copyFromArray(array, DirectDoubleBufferS.arrayBaseOffset, (long)n << 3, this.ix(position), (long)n2 << 3);
            }
            this.position(position + n2);
        }
        else {
            super.put(array, n, n2);
        }
        return this;
    }
    
    @Override
    public DoubleBuffer compact() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        DirectDoubleBufferS.unsafe.copyMemory(this.ix(position), this.ix(0), (long)n << 3);
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
    public ByteOrder order() {
        return (ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }
    
    static {
        unsafe = Bits.unsafe();
        arrayBaseOffset = DirectDoubleBufferS.unsafe.arrayBaseOffset(double[].class);
        unaligned = Bits.unaligned();
    }
}
