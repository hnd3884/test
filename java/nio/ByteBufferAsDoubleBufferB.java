package java.nio;

class ByteBufferAsDoubleBufferB extends DoubleBuffer
{
    protected final ByteBuffer bb;
    protected final int offset;
    
    ByteBufferAsDoubleBufferB(final ByteBuffer bb) {
        super(-1, 0, bb.remaining() >> 3, bb.remaining() >> 3);
        this.bb = bb;
        final int capacity = this.capacity();
        this.limit(capacity);
        final int position = this.position();
        assert position <= capacity;
        this.offset = position;
    }
    
    ByteBufferAsDoubleBufferB(final ByteBuffer bb, final int n, final int n2, final int n3, final int n4, final int offset) {
        super(n, n2, n3, n4);
        this.bb = bb;
        this.offset = offset;
    }
    
    @Override
    public DoubleBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = (position << 3) + this.offset;
        assert n2 >= 0;
        return new ByteBufferAsDoubleBufferB(this.bb, -1, 0, n, n, n2);
    }
    
    @Override
    public DoubleBuffer duplicate() {
        return new ByteBufferAsDoubleBufferB(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public DoubleBuffer asReadOnlyBuffer() {
        return new ByteBufferAsDoubleBufferRB(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return (n << 3) + this.offset;
    }
    
    @Override
    public double get() {
        return Bits.getDoubleB(this.bb, this.ix(this.nextGetIndex()));
    }
    
    @Override
    public double get(final int n) {
        return Bits.getDoubleB(this.bb, this.ix(this.checkIndex(n)));
    }
    
    @Override
    public DoubleBuffer put(final double n) {
        Bits.putDoubleB(this.bb, this.ix(this.nextPutIndex()), n);
        return this;
    }
    
    @Override
    public DoubleBuffer put(final int n, final double n2) {
        Bits.putDoubleB(this.bb, this.ix(this.checkIndex(n)), n2);
        return this;
    }
    
    @Override
    public DoubleBuffer compact() {
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
