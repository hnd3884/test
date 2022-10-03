package java.nio;

class ByteBufferAsFloatBufferRB extends ByteBufferAsFloatBufferB
{
    ByteBufferAsFloatBufferRB(final ByteBuffer byteBuffer) {
        super(byteBuffer);
    }
    
    ByteBufferAsFloatBufferRB(final ByteBuffer byteBuffer, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(byteBuffer, n, n2, n3, n4, n5);
    }
    
    @Override
    public FloatBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        final int n2 = (position << 2) + this.offset;
        assert n2 >= 0;
        return new ByteBufferAsFloatBufferRB(this.bb, -1, 0, n, n, n2);
    }
    
    @Override
    public FloatBuffer duplicate() {
        return new ByteBufferAsFloatBufferRB(this.bb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public FloatBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public FloatBuffer put(final float n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public FloatBuffer put(final int n, final float n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public FloatBuffer compact() {
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
