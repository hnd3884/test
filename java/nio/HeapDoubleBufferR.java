package java.nio;

class HeapDoubleBufferR extends HeapDoubleBuffer
{
    HeapDoubleBufferR(final int n, final int n2) {
        super(n, n2);
        this.isReadOnly = true;
    }
    
    HeapDoubleBufferR(final double[] array, final int n, final int n2) {
        super(array, n, n2);
        this.isReadOnly = true;
    }
    
    protected HeapDoubleBufferR(final double[] array, final int n, final int n2, final int n3, final int n4, final int n5) {
        super(array, n, n2, n3, n4, n5);
        this.isReadOnly = true;
    }
    
    @Override
    public DoubleBuffer slice() {
        final int position = this.position();
        final int limit = this.limit();
        final int n = (position <= limit) ? (limit - position) : 0;
        return new HeapDoubleBufferR(this.hb, -1, 0, n, n, position + this.offset);
    }
    
    @Override
    public DoubleBuffer duplicate() {
        return new HeapDoubleBufferR(this.hb, this.markValue(), this.position(), this.limit(), this.capacity(), this.offset);
    }
    
    @Override
    public DoubleBuffer asReadOnlyBuffer() {
        return this.duplicate();
    }
    
    @Override
    public boolean isReadOnly() {
        return true;
    }
    
    @Override
    public DoubleBuffer put(final double n) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public DoubleBuffer put(final int n, final double n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public DoubleBuffer put(final double[] array, final int n, final int n2) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public DoubleBuffer put(final DoubleBuffer doubleBuffer) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public DoubleBuffer compact() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }
}
