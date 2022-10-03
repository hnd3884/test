package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectIntBufferS extends IntBuffer implements DirectBuffer
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
    
    DirectIntBufferS(final DirectBuffer att, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4);
        this.address = att.address() + n5;
        this.att = att;
    }
    
    @Override
    public IntBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 2;
        assert n2 >= 0;
        return new DirectIntBufferS(this, -1, 0, n, n, n2);
    }
    
    @Override
    public IntBuffer duplicate() {
        return new DirectIntBufferS(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public IntBuffer asReadOnlyBuffer() {
        return new DirectIntBufferRS(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public long address() {
        return this.address;
    }
    
    private long ix(final int n) {
        return this.address + ((long)n << 2);
    }
    
    @Override
    public int get() {
        return Bits.swap(DirectIntBufferS.unsafe.getInt(this.ix(this.nextGetIndex())));
    }
    
    @Override
    public int get(final int n) {
        return Bits.swap(DirectIntBufferS.unsafe.getInt(this.ix(this.checkIndex(n))));
    }
    
    @Override
    public IntBuffer get(final int[] array, final int n, final int n2) {
        if ((long)n2 << 2 > 6L) {
            Buffer.checkBounds(n, n2, array.length);
            final int position = this.position();
            final int limit = this.limit();
            assert position <= limit;
            if (n2 > ((position <= limit) ? (limit - position) : 0)) {
                throw new BufferUnderflowException();
            }
            if (this.order() != ByteOrder.nativeOrder()) {
                Bits.copyToIntArray(this.ix(position), array, (long)n << 2, (long)n2 << 2);
            }
            else {
                Bits.copyToArray(this.ix(position), array, DirectIntBufferS.arrayBaseOffset, (long)n << 2, (long)n2 << 2);
            }
            this.position(position + n2);
        }
        else {
            super.get(array, n, n2);
        }
        return this;
    }
    
    @Override
    public IntBuffer put(final int n) {
        DirectIntBufferS.unsafe.putInt(this.ix(this.nextPutIndex()), Bits.swap(n));
        return this;
    }
    
    @Override
    public IntBuffer put(final int n, final int n2) {
        DirectIntBufferS.unsafe.putInt(this.ix(this.checkIndex(n)), Bits.swap(n2));
        return this;
    }
    
    @Override
    public IntBuffer put(final IntBuffer intBuffer) {
        if (intBuffer instanceof DirectIntBufferS) {
            if (intBuffer == this) {
                throw new IllegalArgumentException();
            }
            final DirectIntBufferS directIntBufferS = (DirectIntBufferS)intBuffer;
            final int position = directIntBufferS.position();
            final int limit = directIntBufferS.limit();
            assert position <= limit;
            final int n = (position <= limit) ? (limit - position) : 0;
            final int position2 = this.position();
            final int limit2 = this.limit();
            assert position2 <= limit2;
            if (n > ((position2 <= limit2) ? (limit2 - position2) : 0)) {
                throw new BufferOverflowException();
            }
            DirectIntBufferS.unsafe.copyMemory(directIntBufferS.ix(position), this.ix(position2), (long)n << 2);
            directIntBufferS.position(position + n);
            this.position(position2 + n);
        }
        else if (intBuffer.hb != null) {
            final int position3 = intBuffer.position();
            final int limit3 = intBuffer.limit();
            assert position3 <= limit3;
            final int n2 = (position3 <= limit3) ? (limit3 - position3) : 0;
            this.put(intBuffer.hb, intBuffer.offset + position3, n2);
            intBuffer.position(position3 + n2);
        }
        else {
            super.put(intBuffer);
        }
        return this;
    }
    
    @Override
    public IntBuffer put(final int[] array, final int n, final int n2) {
        if ((long)n2 << 2 > 6L) {
            Buffer.checkBounds(n, n2, array.length);
            final int position = this.position();
            final int limit = this.limit();
            assert position <= limit;
            if (n2 > ((position <= limit) ? (limit - position) : 0)) {
                throw new BufferOverflowException();
            }
            if (this.order() != ByteOrder.nativeOrder()) {
                Bits.copyFromIntArray(array, (long)n << 2, this.ix(position), (long)n2 << 2);
            }
            else {
                Bits.copyFromArray(array, DirectIntBufferS.arrayBaseOffset, (long)n << 2, this.ix(position), (long)n2 << 2);
            }
            this.position(position + n2);
        }
        else {
            super.put(array, n, n2);
        }
        return this;
    }
    
    @Override
    public IntBuffer compact() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        DirectIntBufferS.unsafe.copyMemory(this.ix(position), this.ix(0), (long)n << 2);
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
        arrayBaseOffset = DirectIntBufferS.unsafe.arrayBaseOffset(int[].class);
        unaligned = Bits.unaligned();
    }
}
