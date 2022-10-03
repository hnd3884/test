package java.nio;

class ByteBufferAsLongBufferB extends LongBuffer
{
    protected final ByteBuffer bb;
    protected final int offset;
    
    ByteBufferAsLongBufferB(final ByteBuffer bb) {
        super(-1, 0, bb.remaining() >> 3, bb.remaining() >> 3);
        this.bb = bb;
        final int capacity = this.capacity();
        this.limit(capacity);
        final int position = this.position();
        assert position <= capacity;
        this.offset = position;
    }
    
    ByteBufferAsLongBufferB(final ByteBuffer bb, final int n, final int n2, final int n3, final int n4, final int offset) {
        super(n, n2, n3, n4);
        this.bb = bb;
        this.offset = offset;
    }
    
    @Override
    public LongBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = (position << 3) + this.offset;
        assert n2 >= 0;
        return new ByteBufferAsLongBufferB(this.bb, -1, 0, n, n, n2);
    }
    
    @Override
    public LongBuffer duplicate() {
        return new ByteBufferAsLongBufferB(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public LongBuffer asReadOnlyBuffer() {
        return new ByteBufferAsLongBufferRB(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return (n << 3) + this.offset;
    }
    
    @Override
    public long get() {
        return Bits.getLongB(this.bb, this.ix(this.nextGetIndex()));
    }
    
    @Override
    public long get(final int n) {
        return Bits.getLongB(this.bb, this.ix(this.checkIndex(n)));
    }
    
    @Override
    public LongBuffer put(final long n) {
        Bits.putLongB(this.bb, this.ix(this.nextPutIndex()), n);
        return this;
    }
    
    @Override
    public LongBuffer put(final int n, final long n2) {
        Bits.putLongB(this.bb, this.ix(this.checkIndex(n)), n2);
        return this;
    }
    
    @Override
    public LongBuffer compact() {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n = (position <= limit) ? (limit - position) : 0;
        final ByteBuffer duplicate = this.bb.duplicate();
        duplicate.limit(this.ix(limit));
        duplicate.position(this.ix(0));
        final ByteBuffer slice = duplicate.slice();
        slice.position(position << 3);
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
