package java.nio;

class ByteBufferAsShortBufferRB extends ByteBufferAsShortBufferB
{
    ByteBufferAsShortBufferRB(final ByteBuffer byteBuffer) {
        super(byteBuffer);
    }
    
    ByteBufferAsShortBufferRB(final ByteBuffer byteBuffer, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(byteBuffer, n, n2, n3, n4, n5);
    }
    
    @Override
    public ShortBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = (position << 1) + this.offset;
        assert n2 >= 0;
        return new ByteBufferAsShortBufferRB(this.bb, -1, 0, n, n, n2);
    }
    
    @Override
    public ShortBuffer duplicate() {
        return new ByteBufferAsShortBufferRB(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public ShortBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public ShortBuffer put(final short n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ShortBuffer put(final int n, final short n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ShortBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public boolean isDirect() {
        return this.bb.isDirect();
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.BIG_ENDIAN;
    }
}
