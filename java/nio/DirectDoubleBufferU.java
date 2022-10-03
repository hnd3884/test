package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectDoubleBufferU extends DoubleBuffer implements DirectBuffer
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
    
    DirectDoubleBufferU(final DirectBuffer att, final int n, final int n2, final int n3, final int n4, final int n5) {
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
        return new DirectDoubleBufferU(this, -1, 0, n, n, n2);
    }
    
    @Override
    public DoubleBuffer duplicate() {
        return new DirectDoubleBufferU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public DoubleBuffer asReadOnlyBuffer() {
        return new DirectDoubleBufferRU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
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
        return DirectDoubleBufferU.unsafe.getDouble(this.ix(this.nextGetIndex()));
    }
    
    @Override
    public double get(final int n) {
        return DirectDoubleBufferU.unsafe.getDouble(this.ix(this.checkIndex(n)));
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
                Bits.copyToArray(this.ix(position), array, DirectDoubleBufferU.arrayBaseOffset, (long)n << 3, (long)n2 << 3);
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
        DirectDoubleBufferU.unsafe.putDouble(this.ix(this.nextPutIndex()), n);
        return this;
    }
    
    @Override
    public DoubleBuffer put(final int n, final double n2) {
        DirectDoubleBufferU.unsafe.putDouble(this.ix(this.checkIndex(n)), n2);
        return this;
    }
    
    @Override
    public DoubleBuffer put(final DoubleBuffer doubleBuffer) {
        if (doubleBuffer instanceof DirectDoubleBufferU) {
            if (doubleBuffer == this) {
                throw new IllegalArgumentException();
            }
            final DirectDoubleBufferU directDoubleBufferU = (DirectDoubleBufferU)doubleBuffer;
            final int position = directDoubleBufferU.position();
            final int limit = directDoubleBufferU.limit();
            assert position <= limit;
            final int n = (position <= limit) ? (limit - position) : 0;
            final int position2 = this.position();
            final int limit2 = this.limit();
            assert position2 <= limit2;
            if (n > ((position2 <= limit2) ? (limit2 - position2) : 0)) {
                throw new BufferOverflowException();
            }
            DirectDoubleBufferU.unsafe.copyMemory(directDoubleBufferU.ix(position), this.ix(position2), (long)n << 3);
            directDoubleBufferU.position(position + n);
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
                Bits.copyFromArray(array, DirectDoubleBufferU.arrayBaseOffset, (long)n << 3, this.ix(position), (long)n2 << 3);
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
        DirectDoubleBufferU.unsafe.copyMemory(this.ix(position), this.ix(0), (long)n << 3);
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
        return (ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN) ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }
    
    static {
        unsafe = Bits.unsafe();
        arrayBaseOffset = DirectDoubleBufferU.unsafe.arrayBaseOffset(double[].class);
        unaligned = Bits.unaligned();
    }
}
