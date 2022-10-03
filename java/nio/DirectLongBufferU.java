package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectLongBufferU extends LongBuffer implements DirectBuffer
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
    
    DirectLongBufferU(final DirectBuffer att, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4);
        this.address = att.address() + n5;
        this.att = att;
    }
    
    @Override
    public LongBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 3;
        assert n2 >= 0;
        return new DirectLongBufferU(this, -1, 0, n, n, n2);
    }
    
    @Override
    public LongBuffer duplicate() {
        return new DirectLongBufferU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public LongBuffer asReadOnlyBuffer() {
        return new DirectLongBufferRU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public long address() {
        return this.address;
    }
    
    private long ix(final int n) {
        return this.address + ((long)n << 3);
    }
    
    @Override
    public long get() {
        return DirectLongBufferU.unsafe.getLong(this.ix(this.nextGetIndex()));
    }
    
    @Override
    public long get(final int n) {
        return DirectLongBufferU.unsafe.getLong(this.ix(this.checkIndex(n)));
    }
    
    @Override
    public LongBuffer get(final long[] array, final int n, final int n2) {
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
                Bits.copyToArray(this.ix(position), array, DirectLongBufferU.arrayBaseOffset, (long)n << 3, (long)n2 << 3);
            }
            this.position(position + n2);
        }
        else {
            super.get(array, n, n2);
        }
        return this;
    }
    
    @Override
    public LongBuffer put(final long n) {
        DirectLongBufferU.unsafe.putLong(this.ix(this.nextPutIndex()), n);
        return this;
    }
    
    @Override
    public LongBuffer put(final int n, final long n2) {
        DirectLongBufferU.unsafe.putLong(this.ix(this.checkIndex(n)), n2);
        return this;
    }
    
    @Override
    public LongBuffer put(final LongBuffer longBuffer) {
        if (longBuffer instanceof DirectLongBufferU) {
            if (longBuffer == this) {
                throw new IllegalArgumentException();
            }
            final DirectLongBufferU directLongBufferU = (DirectLongBufferU)longBuffer;
            final int position = directLongBufferU.position();
            final int limit = directLongBufferU.limit();
            assert position <= limit;
            final int n = (position <= limit) ? (limit - position) : 0;
            final int position2 = this.position();
            final int limit2 = this.limit();
            assert position2 <= limit2;
            if (n > ((position2 <= limit2) ? (limit2 - position2) : 0)) {
                throw new BufferOverflowException();
            }
            DirectLongBufferU.unsafe.copyMemory(directLongBufferU.ix(position), this.ix(position2), (long)n << 3);
            directLongBufferU.position(position + n);
            this.position(position2 + n);
        }
        else if (longBuffer.hb != null) {
            final int position3 = longBuffer.position();
            final int limit3 = longBuffer.limit();
            assert position3 <= limit3;
            final int n2 = (position3 <= limit3) ? (limit3 - position3) : 0;
            this.put(longBuffer.hb, longBuffer.offset + position3, n2);
            longBuffer.position(position3 + n2);
        }
        else {
            super.put(longBuffer);
        }
        return this;
    }
    
    @Override
    public LongBuffer put(final long[] array, final int n, final int n2) {
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
                Bits.copyFromArray(array, DirectLongBufferU.arrayBaseOffset, (long)n << 3, this.ix(position), (long)n2 << 3);
            }
            this.position(position + n2);
        }
        else {
            super.put(array, n, n2);
        }
        return this;
    }
    
    @Override
    public LongBuffer compact() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        DirectLongBufferU.unsafe.copyMemory(this.ix(position), this.ix(0), (long)n << 3);
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
        arrayBaseOffset = DirectLongBufferU.unsafe.arrayBaseOffset(long[].class);
        unaligned = Bits.unaligned();
    }
}
