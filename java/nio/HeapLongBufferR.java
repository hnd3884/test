package java.nio;

class HeapLongBufferR extends HeapLongBuffer
{
    HeapLongBufferR(final int n, final int n2) {
        super(n, n2);
        this.isReadOnly = true;
    }
    
    HeapLongBufferR(final long[] array, final int n, final int n2) {
        super(array, n, n2);
        this.isReadOnly = true;
    }
    
    protected HeapLongBufferR(final long[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(array, n, n2, n3, n4, n5);
        this.isReadOnly = true;
    }
    
    @Override
    public LongBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapLongBufferR(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public LongBuffer duplicate() {
        return new HeapLongBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public LongBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public LongBuffer put(final long n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public LongBuffer put(final int n, final long n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public LongBuffer put(final long[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public LongBuffer put(final LongBuffer longBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public LongBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }
}
