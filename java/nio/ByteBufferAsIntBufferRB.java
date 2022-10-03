package java.nio;

class ByteBufferAsIntBufferRB extends ByteBufferAsIntBufferB
{
    ByteBufferAsIntBufferRB(final ByteBuffer byteBuffer) {
        super(byteBuffer);
    }
    
    ByteBufferAsIntBufferRB(final ByteBuffer byteBuffer, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(byteBuffer, n, n2, n3, n4, n5);
    }
    
    @Override
    public IntBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = (position << 2) + this.offset;
        assert n2 >= 0;
        return new ByteBufferAsIntBufferRB(this.bb, -1, 0, n, n, n2);
    }
    
    @Override
    public IntBuffer duplicate() {
        return new ByteBufferAsIntBufferRB(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public IntBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public IntBuffer put(final int n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public IntBuffer put(final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public IntBuffer compact() {
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
