package java.nio;

class ByteBufferAsIntBufferL extends IntBuffer
{
    protected final ByteBuffer bb;
    protected final int offset;
    
    ByteBufferAsIntBufferL(final ByteBuffer bb) {
        super(-1, 0, bb.remaining() >> 2, bb.remaining() >> 2);
        this.bb = bb;
        final int capacity = this.capacity();
        this.limit(capacity);
        final int position = this.position();
        assert position <= capacity;
        this.offset = position;
    }
    
    ByteBufferAsIntBufferL(final ByteBuffer bb, final int n, final int n2, final int n3, final int n4, final int offset) {
        super(n, n2, n3, n4);
        this.bb = bb;
        this.offset = offset;
    }
    
    @Override
    public IntBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = (position << 2) + this.offset;
        assert n2 >= 0;
        return new ByteBufferAsIntBufferL(this.bb, -1, 0, n, n, n2);
    }
    
    @Override
    public IntBuffer duplicate() {
        return new ByteBufferAsIntBufferL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public IntBuffer asReadOnlyBuffer() {
        return new ByteBufferAsIntBufferRL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return (n << 2) + this.offset;
    }
    
    @Override
    public int get() {
        return Bits.getIntL(this.bb, this.ix(this.nextGetIndex()));
    }
    
    @Override
    public int get(final int n) {
        return Bits.getIntL(this.bb, this.ix(this.checkIndex(n)));
    }
    
    @Override
    public IntBuffer put(final int n) {
        Bits.putIntL(this.bb, this.ix(this.nextPutIndex()), n);
        return this;
    }
    
    @Override
    public IntBuffer put(final int n, final int n2) {
        Bits.putIntL(this.bb, this.ix(this.checkIndex(n)), n2);
        return this;
    }
    
    @Override
    public IntBuffer compact() {
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
