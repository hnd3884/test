package java.nio;

class ByteBufferAsFloatBufferL extends FloatBuffer
{
    protected final ByteBuffer bb;
    protected final int offset;
    
    ByteBufferAsFloatBufferL(final ByteBuffer bb) {
        super(-1, 0, bb.remaining() >> 2, bb.remaining() >> 2);
        this.bb = bb;
        final int capacity = this.capacity();
        this.limit(capacity);
        final int position = this.position();
        assert position <= capacity;
        this.offset = position;
    }
    
    ByteBufferAsFloatBufferL(final ByteBuffer bb, final int n, final int n2, final int n3, final int n4, final int offset) {
        super(n, n2, n3, n4);
        this.bb = bb;
        this.offset = offset;
    }
    
    @Override
    public FloatBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = (position << 2) + this.offset;
        assert n2 >= 0;
        return new ByteBufferAsFloatBufferL(this.bb, -1, 0, n, n, n2);
    }
    
    @Override
    public FloatBuffer duplicate() {
        return new ByteBufferAsFloatBufferL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public FloatBuffer asReadOnlyBuffer() {
        return new ByteBufferAsFloatBufferRL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return (n << 2) + this.offset;
    }
    
    @Override
    public float get() {
        return Bits.getFloatL(this.bb, this.ix(this.nextGetIndex()));
    }
    
    @Override
    public float get(final int n) {
        return Bits.getFloatL(this.bb, this.ix(this.checkIndex(n)));
    }
    
    @Override
    public FloatBuffer put(final float n) {
        Bits.putFloatL(this.bb, this.ix(this.nextPutIndex()), n);
        return this;
    }
    
    @Override
    public FloatBuffer put(final int n, final float n2) {
        Bits.putFloatL(this.bb, this.ix(this.checkIndex(n)), n2);
        return this;
    }
    
    @Override
    public FloatBuffer compact() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        final ByteBuffer duplicate = this.bb.duplicate();
        duplicate.limit(this.ix(limit));
        duplicate.position(this.ix(0));
        final ByteBuffer slice = duplicate.slice();
        slice.position(position << 2);
        slice.compact();
        this.position(n);
        this.limit(this.capacity());
        this.discardMark();
        return this;
    }
    
    @Override
    public boolean isDirect() {
        return this.bb.isDirect();
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.LITTLE_ENDIAN;
    }
}
