package java.nio;

class ByteBufferAsCharBufferL extends CharBuffer
{
    protected final ByteBuffer bb;
    protected final int offset;
    
    ByteBufferAsCharBufferL(final ByteBuffer bb) {
        super(-1, 0, bb.remaining() >> 1, bb.remaining() >> 1);
        this.bb = bb;
        final int capacity = this.capacity();
        this.limit(capacity);
        final int position = this.position();
        assert position <= capacity;
        this.offset = position;
    }
    
    ByteBufferAsCharBufferL(final ByteBuffer bb, final int n, final int n2, final int n3, final int n4, final int offset) {
        super(n, n2, n3, n4);
        this.bb = bb;
        this.offset = offset;
    }
    
    @Override
    public CharBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = (position << 1) + this.offset;
        assert n2 >= 0;
        return new ByteBufferAsCharBufferL(this.bb, -1, 0, n, n, n2);
    }
    
    @Override
    public CharBuffer duplicate() {
        return new ByteBufferAsCharBufferL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public CharBuffer asReadOnlyBuffer() {
        return new ByteBufferAsCharBufferRL(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    protected int ix(final int n) {
        return (n << 1) + this.offset;
    }
    
    @Override
    public char get() {
        return Bits.getCharL(this.bb, this.ix(this.nextGetIndex()));
    }
    
    @Override
    public char get(final int n) {
        return Bits.getCharL(this.bb, this.ix(this.checkIndex(n)));
    }
    
    @Override
    char getUnchecked(final int n) {
        return Bits.getCharL(this.bb, this.ix(n));
    }
    
    @Override
    public CharBuffer put(final char c) {
        Bits.putCharL(this.bb, this.ix(this.nextPutIndex()), c);
        return this;
    }
    
    @Override
    public CharBuffer put(final int n, final char c) {
        Bits.putCharL(this.bb, this.ix(this.checkIndex(n)), c);
        return this;
    }
    
    @Override
    public CharBuffer compact() {
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
    
    public String toString(final int n, final int n2) {
        if (n2 > this.limit() || n > n2) {
            throw new IndexOutOfBoundsException();
        }
        try {
            final char[] array = new char[n2 - n];
            final CharBuffer wrap = CharBuffer.wrap(array);
            final CharBuffer duplicate = this.duplicate();
            duplicate.position(n);
            duplicate.limit(n2);
            wrap.put(duplicate);
            return new String(array);
        }
        catch (final StringIndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException();
        }
    }
    
    @Override
    public CharBuffer subSequence(final int n, final int n2) {
        final int position = this.position();
        final int limit = this.limit();
        assert position <= limit;
        final int n3 = (position <= limit) ? position : limit;
        final int n4 = limit - n3;
        if (n < 0 || n2 > n4 || n > n2) {
            throw new IndexOutOfBoundsException();
        }
        return new ByteBufferAsCharBufferL(this.bb, -1, n3 + n, n3 + n2, this.capacity(), this.offset);
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.LITTLE_ENDIAN;
    }
}
