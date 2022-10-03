package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectShortBufferU extends ShortBuffer implements DirectBuffer
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
    
    DirectShortBufferU(final DirectBuffer att, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4);
        this.address = att.address() + n5;
        this.att = att;
    }
    
    @Override
    public ShortBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 1;
        assert n2 >= 0;
        return new DirectShortBufferU(this, -1, 0, n, n, n2);
    }
    
    @Override
    public ShortBuffer duplicate() {
        return new DirectShortBufferU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public ShortBuffer asReadOnlyBuffer() {
        return new DirectShortBufferRU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public long address() {
        return this.address;
    }
    
    private long ix(final int n) {
        return this.address + ((long)n << 1);
    }
    
    @Override
    public short get() {
        return DirectShortBufferU.unsafe.getShort(this.ix(this.nextGetIndex()));
    }
    
    @Override
    public short get(final int n) {
        return DirectShortBufferU.unsafe.getShort(this.ix(this.checkIndex(n)));
    }
    
    @Override
    public ShortBuffer get(final short[] array, final int n, final int n2) {
        if ((long)n2 << 1 > 6L) {
            Buffer.checkBounds(n, n2, array.length);
            final int position = this.position();
            final int limit = this.limit();
            assert position <= limit;
            if (n2 > ((position <= limit) ? (limit - position) : 0)) {
                throw new BufferUnderflowException();
            }
            if (this.order() != ByteOrder.nativeOrder()) {
                Bits.copyToShortArray(this.ix(position), array, (long)n << 1, (long)n2 << 1);
            }
            else {
                Bits.copyToArray(this.ix(position), array, DirectShortBufferU.arrayBaseOffset, (long)n << 1, (long)n2 << 1);
            }
            this.position(position + n2);
        }
        else {
            super.get(array, n, n2);
        }
        return this;
    }
    
    @Override
    public ShortBuffer put(final short n) {
        DirectShortBufferU.unsafe.putShort(this.ix(this.nextPutIndex()), n);
        return this;
    }
    
    @Override
    public ShortBuffer put(final int n, final short n2) {
        DirectShortBufferU.unsafe.putShort(this.ix(this.checkIndex(n)), n2);
        return this;
    }
    
    @Override
    public ShortBuffer put(final ShortBuffer shortBuffer) {
        if (shortBuffer instanceof DirectShortBufferU) {
            if (shortBuffer == this) {
                throw new IllegalArgumentException();
            }
            final DirectShortBufferU directShortBufferU = (DirectShortBufferU)shortBuffer;
            final int position = directShortBufferU.position();
            final int limit = directShortBufferU.limit();
            assert position <= limit;
            final int n = (position <= limit) ? (limit - position) : 0;
            final int position2 = this.position();
            final int limit2 = this.limit();
            assert position2 <= limit2;
            if (n > ((position2 <= limit2) ? (limit2 - position2) : 0)) {
                throw new BufferOverflowException();
            }
            DirectShortBufferU.unsafe.copyMemory(directShortBufferU.ix(position), this.ix(position2), (long)n << 1);
            directShortBufferU.position(position + n);
            this.position(position2 + n);
        }
        else if (shortBuffer.hb != null) {
            final int position3 = shortBuffer.position();
            final int limit3 = shortBuffer.limit();
            assert position3 <= limit3;
            final int n2 = (position3 <= limit3) ? (limit3 - position3) : 0;
            this.put(shortBuffer.hb, shortBuffer.offset + position3, n2);
            shortBuffer.position(position3 + n2);
        }
        else {
            super.put(shortBuffer);
        }
        return this;
    }
    
    @Override
    public ShortBuffer put(final short[] array, final int n, final int n2) {
        if ((long)n2 << 1 > 6L) {
            Buffer.checkBounds(n, n2, array.length);
            final int position = this.position();
            final int limit = this.limit();
            assert position <= limit;
            if (n2 > ((position <= limit) ? (limit - position) : 0)) {
                throw new BufferOverflowException();
            }
            if (this.order() != ByteOrder.nativeOrder()) {
                Bits.copyFromShortArray(array, (long)n << 1, this.ix(position), (long)n2 << 1);
            }
            else {
                Bits.copyFromArray(array, DirectShortBufferU.arrayBaseOffset, (long)n << 1, this.ix(position), (long)n2 << 1);
            }
            this.position(position + n2);
        }
        else {
            super.put(array, n, n2);
        }
        return this;
    }
    
    @Override
    public ShortBuffer compact() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        DirectShortBufferU.unsafe.copyMemory(this.ix(position), this.ix(0), (long)n << 1);
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
        arrayBaseOffset = DirectShortBufferU.unsafe.arrayBaseOffset(short[].class);
        unaligned = Bits.unaligned();
    }
}
