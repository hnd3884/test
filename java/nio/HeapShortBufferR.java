package java.nio;

class HeapShortBufferR extends HeapShortBuffer
{
    HeapShortBufferR(final int n, final int n2) {
        super(n, n2);
        this.isReadOnly = true;
    }
    
    HeapShortBufferR(final short[] array, final int n, final int n2) {
        super(array, n, n2);
        this.isReadOnly = true;
    }
    
    protected HeapShortBufferR(final short[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(array, n, n2, n3, n4, n5);
        this.isReadOnly = true;
    }
    
    @Override
    public ShortBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapShortBufferR(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public ShortBuffer duplicate() {
        return new HeapShortBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public ShortBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
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
    public ShortBuffer put(final short[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ShortBuffer put(final ShortBuffer shortBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ShortBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }
}
