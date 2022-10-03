package java.nio;

class ByteBufferAsShortBufferB extends ShortBuffer
{
    protected final ByteBuffer bb;
    protected final int offset;
    
    ByteBufferAsShortBufferB(final ByteBuffer bb) {
        super(-1, 0, bb.remaining() >> 1, bb.remaining() >> 1);
        this.bb = bb;
        final int capacity = this.capacity();
        this.limit(capacity);
        final int position = this.position();
        assert position <= capacity;
        this.offset = position;
    }
    
    ByteBufferAsShortBufferB(final ByteBuffer bb, final int n, final int n2, final int n3, final int n4, final int offset) {
        super(n, n2, n3, n4);
        this.bb = bb;
        this.offset = offset;
    }
    
    @Override
    public ShortBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = (position << 1) + this.offset;
        assert n2 >= 0;
        return new ByteBufferAsShortBufferB(this.bb, -1, 0, n, n, n2);
    }
    
    @Override
    public ShortBuffer duplicate() {
        return new ByteBufferAsShortBufferB(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public ShortBuffer asReadOnlyBuffer() {
        return new ByteBufferAsShortBufferRB(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return (n << 1) + this.offset;
    }
    
    @Override
    public short get() {
        return Bits.getShortB(this.bb, this.ix(this.nextGetIndex()));
    }
    
    @Override
    public short get(final int n) {
        return Bits.getShortB(this.bb, this.ix(this.checkIndex(n)));
    }
    
    @Override
    public ShortBuffer put(final short n) {
        Bits.putShortB(this.bb, this.ix(this.nextPutIndex()), n);
        return this;
    }
    
    @Override
    public ShortBuffer put(final int n, final short n2) {
        Bits.putShortB(this.bb, this.ix(this.checkIndex(n)), n2);
        return this;
    }
    
    @Override
    public ShortBuffer compact() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        final ByteBuffer duplicate = this.bb.duplicate();
        duplicate.limit(this.ix(limit));
        duplicate.position(this.ix(0));
        final ByteBuffer slice = duplicate.slice();
        slice.position(position << 1);
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
        return ByteOrder.BIG_ENDIAN;
    }
}
