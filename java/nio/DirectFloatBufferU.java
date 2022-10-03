package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

class DirectFloatBufferU extends FloatBuffer implements DirectBuffer
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
    
    DirectFloatBufferU(final DirectBuffer att, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(n, n2, n3, n4);
        this.address = att.address() + n5;
        this.att = att;
    }
    
    @Override
    public FloatBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = position << 2;
        assert n2 >= 0;
        return new DirectFloatBufferU(this, -1, 0, n, n, n2);
    }
    
    @Override
    public FloatBuffer duplicate() {
        return new DirectFloatBufferU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public FloatBuffer asReadOnlyBuffer() {
        return new DirectFloatBufferRU(this, this.markValue(), this.position(), this.limit(), this.capacity(), 0);
    }
    
    @Override
    public long address() {
        return this.address;
    }
    
    private long ix(final int n) {
        return this.address + ((long)n << 2);
    }
    
    @Override
    public float get() {
        return DirectFloatBufferU.unsafe.getFloat(this.ix(this.nextGetIndex()));
    }
    
    @Override
    public float get(final int n) {
        return DirectFloatBufferU.unsafe.getFloat(this.ix(this.checkIndex(n)));
    }
    
    @Override
    public FloatBuffer get(final float[] array, final int n, final int n2) {
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
                Bits.copyToArray(this.ix(position), array, DirectFloatBufferU.arrayBaseOffset, (long)n << 2, (long)n2 << 2);
            }
            this.position(position + n2);
        }
        else {
            super.get(array, n, n2);
        }
        return this;
    }
    
    @Override
    public FloatBuffer put(final float n) {
        DirectFloatBufferU.unsafe.putFloat(this.ix(this.nextPutIndex()), n);
        return this;
    }
    
    @Override
    public FloatBuffer put(final int n, final float n2) {
        DirectFloatBufferU.unsafe.putFloat(this.ix(this.checkIndex(n)), n2);
        return this;
    }
    
    @Override
    public FloatBuffer put(final FloatBuffer floatBuffer) {
        if (floatBuffer instanceof DirectFloatBufferU) {
            if (floatBuffer == this) {
                throw new IllegalArgumentException();
            }
            final DirectFloatBufferU directFloatBufferU = (DirectFloatBufferU)floatBuffer;
            final int position = directFloatBufferU.position();
            final int limit = directFloatBufferU.limit();
            assert position <= limit;
            final int n = (position <= limit) ? (limit - position) : 0;
            final int position2 = this.position();
            final int limit2 = this.limit();
            assert position2 <= limit2;
            if (n > ((position2 <= limit2) ? (limit2 - position2) : 0)) {
                throw new BufferOverflowException();
            }
            DirectFloatBufferU.unsafe.copyMemory(directFloatBufferU.ix(position), this.ix(position2), (long)n << 2);
            directFloatBufferU.position(position + n);
            this.position(position2 + n);
        }
        else if (floatBuffer.hb != null) {
            final int position3 = floatBuffer.position();
            final int limit3 = floatBuffer.limit();
            assert position3 <= limit3;
            final int n2 = (position3 <= limit3) ? (limit3 - position3) : 0;
            this.put(floatBuffer.hb, floatBuffer.offset + position3, n2);
            floatBuffer.position(position3 + n2);
        }
        else {
            super.put(floatBuffer);
        }
        return this;
    }
    
    @Override
    public FloatBuffer put(final float[] array, final int n, final int n2) {
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
                Bits.copyFromArray(array, DirectFloatBufferU.arrayBaseOffset, (long)n << 2, this.ix(position), (long)n2 << 2);
            }
            this.position(position + n2);
        }
        else {
            super.put(array, n, n2);
        }
        return this;
    }
    
    @Override
    public FloatBuffer compact() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        DirectFloatBufferU.unsafe.copyMemory(this.ix(position), this.ix(0), (long)n << 2);
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
        arrayBaseOffset = DirectFloatBufferU.unsafe.arrayBaseOffset(float[].class);
        unaligned = Bits.unaligned();
    }
}
